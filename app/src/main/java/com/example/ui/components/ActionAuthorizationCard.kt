package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.automation.ActionType
import com.example.automation.ProposedAction
import com.example.ui.theme.CyberBorderGradient
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.HolographicCyan
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun ActionAuthorizationCard(
    action: ProposedAction,
    onConfirm: (ProposedAction) -> Unit,
    onCancel: (ProposedAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedPayload by remember(action.id) { mutableStateOf(action.editablePayload) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberSurfaceElevated, shape = RoundedCornerShape(16.dp))
            .border(1.5.dp, CyberBorderGradient, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("action_auth_card_${action.id}")
    ) {
        // Top Security Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Permission Gated",
                    tint = NeonAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AUTHORIZATION REQUIRED",
                    color = NeonAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = "Zero-Assumed Consent",
                color = TextMuted,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action Details
        Row(verticalAlignment = Alignment.CenterVertically) {
            val icon = when (action.type) {
                ActionType.WHATSAPP -> Icons.Default.Lock
                ActionType.INSTAGRAM -> Icons.Default.PersonSearch
                ActionType.YOUTUBE -> Icons.Default.PlayCircle
                ActionType.CALL -> Icons.Default.Call
                ActionType.ALARM -> Icons.Default.Alarm
                ActionType.SYSTEM_SETTING -> Icons.Default.Warning
                ActionType.APP_LAUNCH -> Icons.Default.Apps
                ActionType.WEB_NAVIGATE -> Icons.Default.Language
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = action.title,
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Target: ${action.target}",
                    color = HolographicCyan,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = action.detail,
            color = TextMuted,
            fontSize = 13.sp
        )

        // Editing Field
        if (isEditing) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = editedPayload,
                onValueChange = { editedPayload = it },
                label = { Text("Edit Parameter / Message") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("action_payload_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HolographicCyan,
                    unfocusedBorderColor = Color(0xFF233659),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedLabelColor = HolographicCyan
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Confirmation Actions: Confirm, Edit, Cancel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cancel
            OutlinedButton(
                onClick = { onCancel(action) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_cancel_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = EmergencyRed
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmergencyRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cancel", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reject", fontSize = 12.sp)
            }

            // Edit toggle
            OutlinedButton(
                onClick = { isEditing = !isEditing },
                modifier = Modifier
                    .weight(0.9f)
                    .testTag("action_edit_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = HolographicCyan
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, HolographicCyan.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isEditing) "Done" else "Edit", fontSize = 12.sp)
            }

            // Authorize / Confirm
            Button(
                onClick = {
                    onConfirm(action.copy(editablePayload = editedPayload))
                },
                modifier = Modifier
                    .weight(1.3f)
                    .testTag("action_confirm_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricBlue,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Authorize", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Authorize", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
