package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.ThinkingMode
import com.example.ui.MainViewModel
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGradient
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.HolographicCyan
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack

@Composable
fun VisionResearchScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedImage by viewModel.selectedImage.collectAsStateWithLifecycle()
    val researchReport by viewModel.researchReport.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("VISION") } // VISION or RESEARCH
    var visionPrompt by remember { mutableStateOf("CJ, is photo mein kya hai aur isko detail mein samjhao.") }
    var researchTopic by remember { mutableStateOf("") }

    // Photo picker launcher
    val photoPicker = rememberLauncherForActivityResult(
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

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            viewModel.setSelectedImage(it)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(16.dp)
    ) {
        // Tab Selector: Live Vision vs Smart Research
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberSurface, shape = RoundedCornerShape(10.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (activeTab == "VISION") ElectricBlue else androidx.compose.ui.graphics.Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { activeTab = "VISION" }
                    .padding(vertical = 8.dp)
                    .testTag("tab_vision"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = if (activeTab == "VISION") VoidBlack else TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live Vision",
                        color = if (activeTab == "VISION") VoidBlack else TextWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (activeTab == "RESEARCH") ElectricBlue else androidx.compose.ui.graphics.Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { activeTab = "RESEARCH" }
                    .padding(vertical = 8.dp)
                    .testTag("tab_research_mode"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = if (activeTab == "RESEARCH") VoidBlack else TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Smart Research",
                        color = if (activeTab == "RESEARCH") VoidBlack else TextWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeTab == "VISION") {
            // Live Vision UI
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberBorderGradient, shape = RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "FEATURE A: LIVE VISION & MULTIMODAL OCR",
                                color = HolographicCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Share camera feed or photo for CJ to analyze machinery, documents, or screenshots.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Attachment action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        photoPicker.launch(
                                            androidx.activity.result.PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("gallery_picker_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurface)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = HolographicCyan, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pick Photo", fontSize = 12.sp, color = TextWhite)
                                }

                                Button(
                                    onClick = { cameraLauncher.launch(null) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("camera_capture_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurface)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Camera", fontSize = 12.sp, color = TextWhite)
                                }
                            }

                            // Selected Image Preview
                            if (selectedImage != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                                ) {
                                    Image(
                                        bitmap = selectedImage!!.asImageBitmap(),
                                        contentDescription = "Selected Visual",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { viewModel.setSelectedImage(null) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(6.dp)
                                            .background(VoidBlack.copy(alpha = 0.7f), shape = RoundedCornerShape(20.dp))
                                            .size(30.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextWhite, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = visionPrompt,
                                onValueChange = { visionPrompt = it },
                                label = { Text("Vision Query") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("vision_prompt_input"),
                                colors = outlineColors()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    viewModel.setThinkingMode(ThinkingMode.DETAILED)
                                    viewModel.sendUserMessage(visionPrompt)
                                },
                                enabled = selectedImage != null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("analyze_vision_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricBlue,
                                    contentColor = VoidBlack
                                )
                            ) {
                                Icon(Icons.Default.FindInPage, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (selectedImage != null) "Analyze Photo with CJ" else "Attach a photo first",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Smart Research Mode UI
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberBorderGradient, shape = RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "FEATURE B: SMART DEEP RESEARCH",
                                color = NeonAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Cross-references multiple sources, resolves conflicting claims, and delivers an executive decision report for Boss.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = researchTopic,
                                onValueChange = { researchTopic = it },
                                label = { Text("Research Topic") },
                                placeholder = { Text("India mein best electric car comparison") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("research_topic_input"),
                                colors = outlineColors()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    viewModel.conductResearch(researchTopic)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("conduct_research_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricBlue,
                                    contentColor = VoidBlack
                                )
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Execute Deep Research", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Display Active Research Report if available
                if (researchReport != null) {
                    val report = researchReport!!
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = CyberSurface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "DOSSIER: ${report.topic}",
                                    color = HolographicCyan,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Executive Summary",
                                    color = ElectricBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = report.executiveSummary,
                                    color = TextWhite,
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Key Comparative Findings",
                                    color = ElectricBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                report.keyPoints.forEach { point ->
                                    Text(text = "• $point", color = TextWhite, fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp))
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Simulated Verified Citations",
                                    color = NeonAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                report.sources.forEach { source ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Link, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = source, color = TextMuted, fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CyberSurfaceElevated, shape = RoundedCornerShape(8.dp))
                                        .border(1.dp, HolographicCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "Boss Recommendation: ${report.conclusion}",
                                        color = HolographicCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
