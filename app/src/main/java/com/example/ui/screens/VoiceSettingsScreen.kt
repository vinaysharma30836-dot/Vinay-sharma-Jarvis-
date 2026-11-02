package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.AiProviderType
import com.example.ui.MainViewModel
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGradient
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.HolographicCyan
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.voice.VoiceProfile

@Composable
fun VoiceSettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val voiceProfile by viewModel.voiceManager.currentProfile.collectAsStateWithLifecycle()
    val pitch by viewModel.voiceManager.pitch.collectAsStateWithLifecycle()
    val rate by viewModel.voiceManager.rate.collectAsStateWithLifecycle()
    val activeProvider by viewModel.activeProvider.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Voice Profile Selection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberBorderGradient, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = HolographicCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AUTHORIZED VOICE PROFILE",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option 1: RIYU VOICE (Authorized)
                    VoiceProfileOption(
                        title = "Riyu Voice (Authorized)",
                        subtitle = "Warm natural Hinglish conversational delivery with respectful Boss inflection",
                        isSelected = voiceProfile == VoiceProfile.RIYU_VOICE,
                        onClick = { viewModel.voiceManager.setVoiceProfile(VoiceProfile.RIYU_VOICE) },
                        testTag = "option_riyu_voice"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 2: DEFAULT CJ
                    VoiceProfileOption(
                        title = "CJ Core (Jarvis Futuristic)",
                        subtitle = "Crisp, cybernetic acoustic cadence with high-speed precision",
                        isSelected = voiceProfile == VoiceProfile.DEFAULT_CJ,
                        onClick = { viewModel.voiceManager.setVoiceProfile(VoiceProfile.DEFAULT_CJ) },
                        testTag = "option_default_cj"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Voice Testing Button
                    Button(
                        onClick = { viewModel.voiceManager.testCurrentVoice() },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = VoidBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_voice_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Spoken Voice Sample", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Voice Tuning (Pitch & Rate)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Acoustic Cadence Tuning",
                        color = HolographicCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Speaking Speed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Speaking Speed (Rate)", color = TextWhite, fontSize = 12.sp)
                        Text(text = "%.2fx".format(rate), color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = rate,
                        onValueChange = { viewModel.voiceManager.updateRate(it) },
                        valueRange = 0.6f..1.6f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricBlue,
                            activeTrackColor = HolographicCyan,
                            inactiveTrackColor = CyberSurfaceElevated
                        ),
                        modifier = Modifier.testTag("rate_slider")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Pitch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Vocal Pitch", color = TextWhite, fontSize = 12.sp)
                        Text(text = "%.2fx".format(pitch), color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = pitch,
                        onValueChange = { viewModel.voiceManager.updatePitch(it) },
                        valueRange = 0.7f..1.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricBlue,
                            activeTrackColor = HolographicCyan,
                            inactiveTrackColor = CyberSurfaceElevated
                        ),
                        modifier = Modifier.testTag("pitch_slider")
                    )
                }
            }
        }

        // AI Provider Engine Selector
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Memory, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Modular AI Engine", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // Gemini Cloud Provider
                    VoiceProfileOption(
                        title = "Google Gemini 2.5 Flash",
                        subtitle = "High-reasoning cloud multimodal intelligence via Secrets BuildConfig",
                        isSelected = activeProvider == AiProviderType.GEMINI,
                        onClick = { viewModel.setAiProvider(AiProviderType.GEMINI) },
                        testTag = "provider_gemini"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Local Embedded Core
                    VoiceProfileOption(
                        title = "Embedded JARVIS Core",
                        subtitle = "Autonomous offline intelligence with intent parsing and local memory",
                        isSelected = activeProvider == AiProviderType.EMBEDDED_JARVIS,
                        onClick = { viewModel.setAiProvider(AiProviderType.EMBEDDED_JARVIS) },
                        testTag = "provider_embedded"
                    )
                }
            }
        }

        // Voice Authorization & Security Notice
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurfaceElevated, shape = RoundedCornerShape(10.dp))
                    .border(1.dp, CyberBorder, shape = RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security",
                    tint = HolographicCyan,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Authorized Voice & Privacy Verified",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Riyu Voice profile is authenticated for Boss's private use. Zero audio or memory data is transmitted without approval.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Danger Zone: Clear History
        item {
            Button(
                onClick = { viewModel.clearChat() },
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed.copy(alpha = 0.2f), contentColor = EmergencyRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("clear_history_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clear Conversation History", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun VoiceProfileOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) ElectricBlue.copy(alpha = 0.15f) else CyberSurface,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                if (isSelected) HolographicCyan else CyberBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = HolographicCyan,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
