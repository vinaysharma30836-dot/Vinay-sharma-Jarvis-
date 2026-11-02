package com.example.automation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.Settings
import android.widget.Toast
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

enum class ActionType(val displayName: String, val icon: String) {
    WHATSAPP("WhatsApp Message", "chat"),
    INSTAGRAM("Instagram Profile", "person_search"),
    YOUTUBE("YouTube Video", "play_circle"),
    CALL("Phone Call", "call"),
    ALARM("Set Alarm", "alarm"),
    SYSTEM_SETTING("Device Settings", "settings"),
    APP_LAUNCH("Launch Application", "apps"),
    WEB_NAVIGATE("Web Navigation", "language")
}

data class ProposedAction(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: ActionType,
    val title: String,
    val target: String,
    val detail: String,
    val editablePayload: String,
    val rawData: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)

class CjAutomationManager(private val context: Context) {

    fun executeAction(action: ProposedAction, onResult: (Boolean, String) -> Unit) {
        try {
            when (action.type) {
                ActionType.WHATSAPP -> {
                    val phone = action.rawData["phone"]?.trim().orEmpty()
                    val message = action.editablePayload.ifBlank { action.rawData["message"].orEmpty() }
                    val encoded = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
                    
                    val uri = if (phone.isNotBlank()) {
                        Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=$encoded")
                    } else {
                        Uri.parse("https://api.whatsapp.com/send?text=$encoded")
                    }
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        setPackage("com.whatsapp")
                    }
                    // Try launching WhatsApp specifically, fallback to general intent if not installed
                    try {
                        context.startActivity(intent)
                        onResult(true, "WhatsApp chat opened for ${targetMessage(action.target)}.")
                    } catch (e: Exception) {
                        val fallback = Intent(Intent.ACTION_VIEW, uri).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(fallback)
                        onResult(true, "Opened in browser/client.")
                    }
                }

                ActionType.INSTAGRAM -> {
                    val username = action.editablePayload.ifBlank { action.target }.replace("@", "").trim()
                    val appUri = Uri.parse("http://instagram.com/_u/$username")
                    val intent = Intent(Intent.ACTION_VIEW, appUri).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        setPackage("com.instagram.android")
                    }
                    try {
                        context.startActivity(intent)
                        onResult(true, "Instagram profile @$username opened.")
                    } catch (e: Exception) {
                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/$username")).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(webIntent)
                        onResult(true, "Opened Instagram profile via web.")
                    }
                }

                ActionType.YOUTUBE -> {
                    val query = action.editablePayload.ifBlank { action.target }.trim()
                    val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
                    val appIntent = Intent(Intent.ACTION_SEARCH).apply {
                        setPackage("com.google.android.youtube")
                        putExtra("query", query)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    try {
                        context.startActivity(appIntent)
                        onResult(true, "Searching YouTube for '$query'.")
                    } catch (e: Exception) {
                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$encodedQuery")).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(webIntent)
                        onResult(true, "Opened YouTube search in browser.")
                    }
                }

                ActionType.CALL -> {
                    val phoneNumber = action.editablePayload.ifBlank { action.target }.trim()
                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(dialIntent)
                    onResult(true, "Dialer opened for $phoneNumber. Ready to call.")
                }

                ActionType.ALARM -> {
                    val hour = action.rawData["hour"]?.toIntOrNull() ?: 7
                    val minute = action.rawData["minute"]?.toIntOrNull() ?: 0
                    val message = action.editablePayload.ifBlank { action.target }
                    
                    val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                        putExtra(AlarmClock.EXTRA_HOUR, hour)
                        putExtra(AlarmClock.EXTRA_MINUTES, minute)
                        putExtra(AlarmClock.EXTRA_MESSAGE, message)
                        putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    try {
                        context.startActivity(alarmIntent)
                        onResult(true, "Alarm set for %02d:%02d ('$message').".format(hour, minute))
                    } catch (e: Exception) {
                        onResult(false, "Could not launch Alarm app: ${e.localizedMessage}")
                    }
                }

                ActionType.SYSTEM_SETTING -> {
                    val settingKey = action.rawData["setting"] ?: "general"
                    val intent = when (settingKey.lowercase()) {
                        "display", "brightness" -> Intent(Settings.ACTION_DISPLAY_SETTINGS)
                        "sound", "volume" -> Intent(Settings.ACTION_SOUND_SETTINGS)
                        "wifi" -> Intent(Settings.ACTION_WIFI_SETTINGS)
                        "bluetooth" -> Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                        else -> Intent(Settings.ACTION_SETTINGS)
                    }.apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    onResult(true, "Opened device settings: $settingKey.")
                }

                ActionType.APP_LAUNCH -> {
                    val packageName = action.rawData["package"] ?: action.editablePayload
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
                    if (launchIntent != null) {
                        launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(launchIntent)
                        onResult(true, "Launched app: ${action.target}")
                    } else {
                        onResult(false, "App package '$packageName' not found on device.")
                    }
                }

                ActionType.WEB_NAVIGATE -> {
                    var url = action.editablePayload.ifBlank { action.target }
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "https://$url"
                    }
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(webIntent)
                    onResult(true, "Navigated to $url")
                }
            }
        } catch (e: Exception) {
            onResult(false, "Action failed: ${e.localizedMessage}")
        }
    }

    private fun targetMessage(target: String): String = if (target.isNotBlank()) target else "recipient"
}
