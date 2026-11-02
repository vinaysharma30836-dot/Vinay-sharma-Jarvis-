package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.ThinkingMode
import com.example.data.local.ChatMessageEntity
import com.example.ui.MainViewModel
import com.example.ui.components.ActionAuthorizationCard
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.CjArcReactorCore
import com.example.ui.components.CjCoreState
import com.example.ui.components.EmergencyStopBanner
import com.example.ui.theme.ArcReactorCore
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGradient
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.HolographicCyan
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CoreDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val coreState by viewModel.coreState.collectAsStateWithLifecycle()
    val thinkingMode by viewModel.currentThinkingMode.collectAsStateWithLifecycle()
    val pendingAction by viewModel.pendingAction.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsStateWithLifecycle()
    val isListening by viewModel.speechManager.isListening.collectAsStateWithLifecycle()
    val speechAmplitude by viewModel.voiceManager.speechAmplitude.collectAsStateWithLifecycle()
    val rmsLevel by viewModel.speechManager.rmsLevel.collectAsStateWithLifecycle()
    val voiceProfile by viewModel.voiceManager.currentProfile.collectAsStateWithLifecycle()
    val selectedImage by viewModel.selectedImage.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it))
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            }
            viewModel.setSelectedImage(bitmap)
        }
    }

    // Permission launcher for microphone
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.speechManager.startListening()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
    ) {
        // Top Emergency Banner / Holographic Header
        EmergencyStopBanner(
            onEmergencyStop = { viewModel.triggerEmergencyStop() },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        // Central Arc Reactor & Waveform Panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                CjArcReactorCore(
                    state = coreState,
                    audioAmplitude = if (isSpeaking) speechAmplitude else rmsLevel,
                    size = 135.dp
                )

                // State badge over core
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(CyberSurface.copy(alpha = 0.85f), shape = RoundedCornerShape(12.dp))
                        .border(1.dp, CyberBorder, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = when (coreState) {
                            CjCoreState.LISTENING -> "LISTENING (BOSS)"
                            CjCoreState.THINKING -> "COMPUTING..."
                            CjCoreState.SPEAKING -> "RIYU VOICE ACTIVE"
                            CjCoreState.EMERGENCY_STOP -> "EMERGENCY HALT"
                            CjCoreState.IDLE -> "JARVIS ONLINE"
                        },
                        color = when (coreState) {
                            CjCoreState.EMERGENCY_STOP -> EmergencyRed
                            CjCoreState.LISTENING -> HolographicCyan
                            CjCoreState.SPEAKING -> ElectricBlue
                            CjCoreState.THINKING -> NeonCyan
                            else -> TextMuted
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Audio Waveform Visualizer
            AudioWaveformVisualizer(
                isActive = isSpeaking || isListening,
                amplitude = if (isSpeaking) speechAmplitude else rmsLevel,
                barCount = 24,
                maxHeight = 24.dp
            )

            // Wake word status info chip
            Text(
                text = "Wake Phrases: 'CJ, hello' • 'Hey CJ' • 'CJ, suno'",
                color = HolographicCyan.copy(alpha = 0.75f),
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Thinking Mode Selector Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ThinkingMode.values().forEach { mode ->
                FilterChip(
                    selected = thinkingMode == mode,
                    onClick = { viewModel.setThinkingMode(mode) },
                    label = { Text(mode.displayName, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlue.copy(alpha = 0.2f),
                        selectedLabelColor = HolographicCyan,
                        containerColor = CyberSurface,
                        labelColor = TextMuted
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = thinkingMode == mode,
                        borderColor = CyberBorder,
                        selectedBorderColor = HolographicCyan
                    ),
                    modifier = Modifier.testTag("mode_chip_${mode.name}")
                )
            }
        }

        // Quick Command Prompts Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AssistChip(
                onClick = { viewModel.generateMorningBriefing() },
                label = { Text("🌅 Morning Briefing", fontSize = 11.sp, color = TextWhite) },
                colors = AssistChipDefaults.assistChipColors(containerColor = CyberSurfaceElevated),
                border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = CyberBorder),
                modifier = Modifier.testTag("chip_briefing")
            )
            AssistChip(
                onClick = { viewModel.conductResearch("India mein best gaming laptop under 1 lakh") },
                label = { Text("⚡ Deep Laptop Research", fontSize = 11.sp, color = TextWhite) },
                colors = AssistChipDefaults.assistChipColors(containerColor = CyberSurfaceElevated),
                border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = CyberBorder),
                modifier = Modifier.testTag("chip_research")
            )
            AssistChip(
                onClick = { viewModel.sendUserMessage("Send whatsapp to 9876543210 saying meeting at 5 PM") },
                label = { Text("📱 WhatsApp Rahul", fontSize = 11.sp, color = TextWhite) },
                colors = AssistChipDefaults.assistChipColors(containerColor = CyberSurfaceElevated),
                border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = CyberBorder),
                modifier = Modifier.testTag("chip_whatsapp")
            )
            AssistChip(
                onClick = { viewModel.sendUserMessage("Set alarm for 7:00 AM") },
                label = { Text("⏰ Alarm 7 AM", fontSize = 11.sp, color = TextWhite) },
                colors = AssistChipDefaults.assistChipColors(containerColor = CyberSurfaceElevated),
                border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = CyberBorder),
                modifier = Modifier.testTag("chip_alarm")
            )
            AssistChip(
                onClick = { viewModel.voiceManager.testCurrentVoice() },
                label = { Text("🎙️ Test ${voiceProfile.displayName}", fontSize = 11.sp, color = TextWhite) },
                colors = AssistChipDefaults.assistChipColors(containerColor = CyberSurfaceElevated),
                border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = CyberBorder),
                modifier = Modifier.testTag("chip_voice_test")
            )
        }

        // Conversation History Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatMessageItem(
                    message = message,
                    onSpeak = { viewModel.voiceManager.speak(message.content) },
                    onCopy = { clipboardManager.setText(AnnotatedString(message.content)) }
                )
            }

            // Inline Proposed Action Card awaiting Boss Confirmation
            if (pendingAction != null) {
                item {
                    ActionAuthorizationCard(
                        action = pendingAction!!,
                        onConfirm = { viewModel.authorizeAction(it) },
                        onCancel = { viewModel.rejectAction(it) },
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        // Attached Image Preview
        if (selectedImage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(CyberSurfaceElevated, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, CyberBorder, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Image attached",
                        tint = HolographicCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Image attached for Live Vision Analysis",
                        color = TextWhite,
                        fontSize = 12.sp
                    )
                }
                IconButton(
                    onClick = { viewModel.setSelectedImage(null) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Bottom Command / Chat Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepSpace)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Voice Interrupt / Mic Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (isListening) HolographicCyan else if (isSpeaking) EmergencyRed else CyberSurfaceElevated
                    )
                    .border(
                        1.5.dp,
                        if (isListening) HolographicCyan else if (isSpeaking) EmergencyRed else ElectricBlue,
                        CircleShape
                    )
                    .clickable {
                        if (isSpeaking) {
                            viewModel.voiceManager.stopSpeaking()
                        } else if (isListening) {
                            viewModel.speechManager.stopListening()
                        } else {
                            micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                        }
                    }
                    .testTag("voice_mic_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSpeaking) Icons.Default.Stop else if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                    contentDescription = "Voice Control",
                    tint = if (isListening) VoidBlack else TextWhite,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Camera / Image Attachment Button
            IconButton(
                onClick = {
                    photoPickerLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                modifier = Modifier.testTag("attach_image_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Attach Photo",
                    tint = HolographicCyan
                )
            }

            // Input Text Field
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = {
                    Text(
                        "Talk to CJ or command phone...",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HolographicCyan,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Send Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(ElectricBlue)
                    .clickable {
                        if (textInput.isNotBlank() || selectedImage != null) {
                            viewModel.sendUserMessage(textInput)
                            textInput = ""
                        }
                    }
                    .testTag("send_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Command",
                    tint = VoidBlack,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessageEntity,
    onSpeak: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBoss = message.sender == "BOSS"
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chat_message_${message.id}"),
        horizontalAlignment = if (isBoss) Alignment.End else Alignment.Start
    ) {
        // Sender and Mode Tag
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isBoss) "BOSS" else "CJ 2.0 (RIYU VOICE)",
                color = if (isBoss) ElectricBlue else HolographicCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = formattedTime,
                color = TextMuted,
                fontSize = 9.sp
            )
            if (!isBoss && message.mode.isNotBlank()) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .background(CyberBorder, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = message.mode,
                        color = NeonCyan,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Message bubble
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.88f)
                .background(
                    if (isBoss) CyberSurfaceElevated else CyberSurface,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isBoss) 16.dp else 2.dp,
                        bottomEnd = if (isBoss) 2.dp else 16.dp
                    )
                )
                .border(
                    1.dp,
                    if (isBoss) SolidColor(ElectricBlue.copy(alpha = 0.4f)) else CyberBorderGradient,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isBoss) 16.dp else 2.dp,
                        bottomEnd = if (isBoss) 2.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = message.content,
                    color = TextWhite,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                // Bottom utility bar for CJ messages: Speak audio / Copy
                if (!isBoss) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onSpeak,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Play Riyu Voice",
                                tint = HolographicCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy text",
                                tint = TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
