package com.example.ai

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiAiEngine(
    private val localEngine: EmbeddedJarvisEngine = EmbeddedJarvisEngine()
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateResponse(
        prompt: String,
        mode: ThinkingMode,
        memoryContext: String,
        image: Bitmap? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // No API key provided in Secrets; use robust embedded JARVIS engine
            Log.d("GeminiAiEngine", "Using embedded JARVIS engine (No API Key set in Secrets)")
            return@withContext localEngine.generateLocalResponse(prompt, mode, memoryContext)
        }

        try {
            val systemInstruction = buildString {
                append("You are CJ 2.0, the ultimate personal AI assistant combining the conversational intelligence of ChatGPT, the multimodal capabilities of Gemini, and the futuristic demeanor of Iron Man's JARVIS. ")
                append("You are the user's private personal AI assistant. You ALWAYS address the user as 'Boss'. ")
                append("Provide natural, conversational responses in Hindi, Hinglish, or English matching the user's language. ")
                append("ANSWER QUALITY: Clear, structured, accurate. First provide a direct answer, followed by detailed explanation or examples when appropriate. Never invent facts. ")
                append("CURRENT THINKING MODE: ${mode.name} -> ${mode.promptDirective}. ")
                if (memoryContext.isNotBlank()) {
                    append("\nUSER MEMORY CONTEXT (Pre-approved by Boss):\n$memoryContext\n")
                }
            }

            // Construct payload for Gemini 2.5 Flash / Gemini 1.5 Flash
            val root = JSONObject()

            // System instruction
            val sysInstObj = JSONObject()
            val sysParts = JSONArray().put(JSONObject().put("text", systemInstruction))
            sysInstObj.put("parts", sysParts)
            root.put("system_instruction", sysInstObj)

            // Contents
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            contentObj.put("role", "user")

            val partsArray = JSONArray()

            // Multimodal image attachment
            if (image != null) {
                val base64Image = bitmapToBase64(image)
                val inlineData = JSONObject().apply {
                    put("mime_type", "image/jpeg")
                    put("data", base64Image)
                }
                partsArray.put(JSONObject().put("inline_data", inlineData))
            }

            partsArray.put(JSONObject().put("text", prompt))
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            root.put("contents", contentsArray)

            // Generation config
            val genConfig = JSONObject().apply {
                put("temperature", if (mode == ThinkingMode.CREATIVE) 0.9 else 0.4)
                put("maxOutputTokens", 2048)
            }
            root.put("generationConfig", genConfig)

            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val requestBody = root.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: ""
                Log.e("GeminiAiEngine", "Gemini HTTP error ${response.code}: $errorBody")
                // Graceful fallback to local engine with helpful note
                val localAnswer = localEngine.generateLocalResponse(prompt, mode, memoryContext)
                return@withContext "$localAnswer\n\n*(Note: Cloud AI response code ${response.code}; responded via CJ Embedded Core)*"
            }

            val responseBody = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text")
                    if (text.isNotBlank()) {
                        return@withContext text
                    }
                }
            }
            localEngine.generateLocalResponse(prompt, mode, memoryContext)
        } catch (e: Exception) {
            Log.e("GeminiAiEngine", "Call failed: ${e.message}", e)
            val localFallback = localEngine.generateLocalResponse(prompt, mode, memoryContext)
            "$localFallback\n\n*(CJ Embedded Core active)*"
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        // Resize bitmap if oversized
        val scaled = if (bitmap.width > 1024 || bitmap.height > 1024) {
            val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val newWidth = if (ratio > 1) 1024 else (1024 * ratio).toInt()
            val newHeight = if (ratio > 1) (1024 / ratio).toInt() else 1024
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
