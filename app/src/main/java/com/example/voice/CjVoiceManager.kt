package com.example.voice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

enum class VoiceProfile(
    val id: String,
    val displayName: String,
    val description: String,
    val defaultPitch: Float,
    val defaultRate: Float
) {
    RIYU_VOICE(
        id = "RIYU",
        displayName = "Riyu Voice (Authorized)",
        description = "Warm, articulate, natural Hinglish cadence with respectful 'Boss' inflection",
        defaultPitch = 1.05f,
        defaultRate = 0.95f
    ),
    DEFAULT_CJ(
        id = "DEFAULT_CJ",
        displayName = "CJ Core (Jarvis Crisp)",
        description = "Sleek, futuristic cybernetic AI tone with precise acoustic delivery",
        defaultPitch = 0.92f,
        defaultRate = 1.05f
    )
}

class CjVoiceManager(private val context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentProfile = MutableStateFlow(VoiceProfile.RIYU_VOICE)
    val currentProfile: StateFlow<VoiceProfile> = _currentProfile.asStateFlow()

    private val _pitch = MutableStateFlow(VoiceProfile.RIYU_VOICE.defaultPitch)
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    private val _rate = MutableStateFlow(VoiceProfile.RIYU_VOICE.defaultRate)
    val rate: StateFlow<Float> = _rate.asStateFlow()

    // Real-time simulated/metered amplitude for waveform (0.0f to 1.0f)
    private val _speechAmplitude = MutableStateFlow(0f)
    val speechAmplitude: StateFlow<Float> = _speechAmplitude.asStateFlow()

    private var amplitudeJob: Job? = null

    init {
        initializeTts()
    }

    private fun initializeTts() {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                applyLocaleAndVoice()
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                        startWaveformSimulation()
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        stopWaveformSimulation()
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        stopWaveformSimulation()
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _isSpeaking.value = false
                        stopWaveformSimulation()
                    }
                })
            } else {
                Log.e("CjVoiceManager", "Failed to initialize TextToSpeech engine")
            }
        }
    }

    private fun applyLocaleAndVoice() {
        tts?.let { engine ->
            // Try Indian Hindi first, fallback to Indian English, then US English
            val hiLocale = Locale("hi", "IN")
            val enInLocale = Locale("en", "IN")
            val available = engine.isLanguageAvailable(hiLocale)
            if (available >= TextToSpeech.LANG_AVAILABLE) {
                engine.language = hiLocale
            } else if (engine.isLanguageAvailable(enInLocale) >= TextToSpeech.LANG_AVAILABLE) {
                engine.language = enInLocale
            } else {
                engine.language = Locale.US
            }

            // Find matching voice if available
            try {
                val voices = engine.voices
                if (!voices.isNullOrEmpty()) {
                    val matchingVoice = when (_currentProfile.value) {
                        VoiceProfile.RIYU_VOICE -> voices.firstOrNull { it.locale.language == "hi" || it.name.contains("hin", true) || it.name.contains("in", true) }
                        VoiceProfile.DEFAULT_CJ -> voices.firstOrNull { it.locale.language == "en" && !it.isNetworkConnectionRequired }
                    }
                    if (matchingVoice != null) {
                        engine.voice = matchingVoice
                    }
                }
            } catch (e: Exception) {
                Log.w("CjVoiceManager", "Custom voice query note: ${e.message}")
            }

            engine.setPitch(_pitch.value)
            engine.setSpeechRate(_rate.value)
        }
    }

    fun setVoiceProfile(profile: VoiceProfile) {
        _currentProfile.value = profile
        _pitch.value = profile.defaultPitch
        _rate.value = profile.defaultRate
        applyLocaleAndVoice()
    }

    fun updatePitch(newPitch: Float) {
        _pitch.value = newPitch.coerceIn(0.5f, 2.0f)
        tts?.setPitch(_pitch.value)
    }

    fun updateRate(newRate: Float) {
        _rate.value = newRate.coerceIn(0.5f, 2.0f)
        tts?.setSpeechRate(_rate.value)
    }

    fun speak(text: String, utteranceId: String = "cj_${System.currentTimeMillis()}") {
        if (!isInitialized || text.isBlank()) return
        
        // Clean markdown fences, asterisks, URLs for smooth human-like audio delivery
        val cleanSpeech = cleanTextForSpeech(text)

        applyLocaleAndVoice()
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        }
        tts?.speak(cleanSpeech, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
        stopWaveformSimulation()
    }

    fun testCurrentVoice() {
        val sampleText = when (_currentProfile.value) {
            VoiceProfile.RIYU_VOICE -> "Hello Boss! Main hoon Riyu Voice. System sub-routines bilkul perfect kaam kar rahe hain."
            VoiceProfile.DEFAULT_CJ -> "CJ AI Core online. All cybernetic protocols running at maximum efficiency, Boss."
        }
        speak(sampleText)
    }

    private fun cleanTextForSpeech(input: String): String {
        return input
            .replace(Regex("```[a-zA-Z]*"), "")
            .replace("```", "")
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
            .replace(Regex("\\*(.*?)\\*"), "$1")
            .replace(Regex("#+\\s*"), "")
            .replace(Regex("https?://\\S+"), "web link")
            .trim()
    }

    private fun startWaveformSimulation() {
        amplitudeJob?.cancel()
        amplitudeJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive && _isSpeaking.value) {
                // Dynamic acoustic waveform fluctuations
                _speechAmplitude.value = Random.nextFloat() * 0.7f + 0.3f
                delay(80)
            }
            _speechAmplitude.value = 0f
        }
    }

    private fun stopWaveformSimulation() {
        amplitudeJob?.cancel()
        _speechAmplitude.value = 0f
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        stopWaveformSimulation()
    }
}
