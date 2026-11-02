package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.automation.ActionType
import com.example.automation.ProposedAction
import com.example.data.local.ActionHistoryEntity
import com.example.ui.MainViewModel
import com.example.ui.components.ActionAuthorizationCard
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AutomationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val pendingAction by viewModel.pendingAction.collectAsStateWithLifecycle()
    val actionHistory by viewModel.actionHistory.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("CONTROLS") } // CONTROLS or AUDIT_LOG

    // Form inputs for custom triggers
    var whatsappTarget by remember { mutableStateOf("") }
    var whatsappMessage by remember { mutableStateOf("") }

    var youtubeQuery by remember { mutableStateOf("") }
    var instagramUser by remember { mutableStateOf("") }
    var phoneCallTarget by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(16.dp)
    ) {
        // Top Security Doctrine Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberSurfaceElevated, shape = RoundedCornerShape(12.dp))
                .border(1.dp, CyberBorder, shape = RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Zero-Assumed Consent",
                tint = HolographicCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "STRICT PERMISSION PROTOCOL",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "No action executes without Boss's explicit confirmation.",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector: Phone Controls vs Action Audit History
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
                        if (activeTab == "CONTROLS") ElectricBlue else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { activeTab = "CONTROLS" }
                    .padding(vertical = 8.dp)
                    .testTag("tab_controls"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Phone Automation",
                    color = if (activeTab == "CONTROLS") VoidBlack else TextWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (activeTab == "AUDIT_LOG") ElectricBlue else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { activeTab = "AUDIT_LOG" }
                    .padding(vertical = 8.dp)
                    .testTag("tab_audit_log"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Action Audit (${actionHistory.size})",
                    color = if (activeTab == "AUDIT_LOG") VoidBlack else TextWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pending Action Card (if any pending)
        if (pendingAction != null) {
            ActionAuthorizationCard(
                action = pendingAction!!,
                onConfirm = { viewModel.authorizeAction(it) },
                onCancel = { viewModel.rejectAction(it) },
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (activeTab == "CONTROLS") {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // WhatsApp Automation Card
                item {
                    AutomationTriggerCard(
                        icon = Icons.Default.Chat,
                        title = "WhatsApp Automation",
                        subtitle = "Draft and send messages with confirmation"
                    ) {
                        OutlinedTextField(
                            value = whatsappTarget,
                            onValueChange = { whatsappTarget = it },
                            label = { Text("Contact or Phone Number") },
                            placeholder = { Text("+91 9876543210") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("whatsapp_phone_input"),
                            colors = outlineColors()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = whatsappMessage,
                            onValueChange = { whatsappMessage = it },
                            label = { Text("Draft Message") },
                            placeholder = { Text("Hello Boss, meeting at 5 PM") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("whatsapp_msg_input"),
                            colors = outlineColors()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.sendUserMessage("Send whatsapp to $whatsappTarget saying $whatsappMessage")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = VoidBlack),
                            modifier = Modifier
                                .align(Alignment.End)
                                .testTag("draft_whatsapp_button")
                        ) {
                            Text("Draft & Review Action", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // YouTube Automation Card
                item {
                    AutomationTriggerCard(
                        icon = Icons.Default.PlayCircle,
                        title = "YouTube Video Control",
                        subtitle = "Search & stream videos via intent"
                    ) {
                        OutlinedTextField(
                            value = youtubeQuery,
                            onValueChange = { youtubeQuery = it },
                            label = { Text("Search Video Topic") },
                            placeholder = { Text("SpaceX Starship Launch") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("youtube_input"),
                            colors = outlineColors()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.sendUserMessage("Play youtube video for $youtubeQuery")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = VoidBlack),
                            modifier = Modifier
                                .align(Alignment.End)
                                .testTag("search_youtube_button")
                        ) {
                            Text("Draft Search Action", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Instagram Automation Card
                item {
                    AutomationTriggerCard(
                        icon = Icons.Default.PersonSearch,
                        title = "Instagram Search",
                        subtitle = "Find and open Instagram profiles"
                    ) {
                        OutlinedTextField(
                            value = instagramUser,
                            onValueChange = { instagramUser = it },
                            label = { Text("Instagram Username") },
                            placeholder = { Text("@nasa") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("instagram_input"),
                            colors = outlineColors()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.sendUserMessage("Open instagram profile for @$instagramUser")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = VoidBlack),
                            modifier = Modifier
                                .align(Alignment.End)
                                .testTag("open_instagram_button")
                        ) {
                            Text("Open Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Quick System Settings Controls
                item {
                    AutomationTriggerCard(
                        icon = Icons.Default.Settings,
                        title = "Device System Settings",
                        subtitle = "Access Wi-Fi, Display, Sound, and Bluetooth"
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.sendUserMessage("Open wifi settings") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_wifi"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated)
                            ) {
                                Text("Wi-Fi", fontSize = 11.sp, color = HolographicCyan)
                            }
                            Button(
                                onClick = { viewModel.sendUserMessage("Open display settings") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_display"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated)
                            ) {
                                Text("Display", fontSize = 11.sp, color = HolographicCyan)
                            }
                            Button(
                                onClick = { viewModel.sendUserMessage("Open sound volume settings") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_sound"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated)
                            ) {
                                Text("Sound", fontSize = 11.sp, color = HolographicCyan)
                            }
                        }
                    }
                }
            }
        } else {
            // Action History Audit Log
            if (actionHistory.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recorded actions yet. All verified executions will be audited here.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(actionHistory, key = { it.id }) { action ->
                        ActionHistoryItem(action = action)
                    }
                }
            }
        }
    }
}

@Composable
fun AutomationTriggerCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberBorder, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = title, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun ActionHistoryItem(action: ActionHistoryEntity) {
    val timeFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(action.timestamp) { timeFormat.format(Date(action.timestamp)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberSurface, shape = RoundedCornerShape(10.dp))
            .border(1.dp, CyberBorder, shape = RoundedCornerShape(10.dp))
            .padding(12.dp)
            .testTag("action_history_${action.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = action.title,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .background(
                            when (action.status) {
                                "COMPLETED" -> Color(0xFF00C896).copy(alpha = 0.2f)
                                "REJECTED" -> EmergencyRed.copy(alpha = 0.2f)
                                else -> NeonAmber.copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = action.status,
                        color = when (action.status) {
                            "COMPLETED" -> Color(0xFF00F59B)
                            "REJECTED" -> EmergencyRed
                            else -> NeonAmber
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = "Target: ${action.target} • $formattedTime",
                color = TextMuted,
                fontSize = 11.sp
            )
            if (action.notes.isNotBlank()) {
                Text(
                    text = action.notes,
                    color = HolographicCyan,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun outlineColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = HolographicCyan,
    unfocusedBorderColor = CyberBorder,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    focusedLabelColor = HolographicCyan,
    unfocusedLabelColor = TextMuted
)
