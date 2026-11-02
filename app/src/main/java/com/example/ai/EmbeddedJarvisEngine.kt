package com.example.ai

import android.graphics.Bitmap
import com.example.automation.ActionType
import com.example.automation.ProposedAction
import kotlinx.coroutines.delay
import java.util.Locale

class EmbeddedJarvisEngine {

    fun generateLocalResponse(
        prompt: String,
        mode: ThinkingMode,
        memoryContext: String
    ): String {
        val lower = prompt.lowercase(Locale.ROOT)

        // Greetings & Identity
        if (lower.contains("hello") || lower.contains("kaise ho") || lower.contains("who are you") || lower.contains("kya haal")) {
            return "Hello Boss! Main hoon CJ, aapka private personal AI assistant. System sub-routines bilkul active hain, Riyu Voice authorized hai, aur saari permissions aapke direct control mein hain. Aaj bataiye Boss, kya command execute karni hai?"
        }

        // Daily briefing request
        if (lower.contains("briefing") || lower.contains("aaj kya karna hai") || lower.contains("schedule") || lower.contains("routine")) {
            return "Boss, yahan hai aapka Daily Briefing:\n\n" +
                    "1. Priority Tasks: Architecture review aur automated phone tasks sync.\n" +
                    "2. Health & Habit: Hydration streak 14 days active.\n" +
                    "3. System Status: All modules (Vision, Research, Voice) optimal.\n\n" +
                    "Aap jab chahein, main kisi bhi task ko automate kar sakta hoon Boss!"
        }

        // Math & Calculation
        if (Regex(".*\\d+\\s*([+\\-*/x^%])\\s*\\d+.*").matches(lower)) {
            val evaluated = evaluateBasicMath(lower)
            if (evaluated != null) {
                return "Direct Answer: $evaluated\n\nExplanation: Boss, mathematically solve karne par result $evaluated aaya hai. Agar koi aur formula ya complex equation solve karni ho toh batayein."
            }
        }

        // Mode specific structured output
        return when (mode) {
            ThinkingMode.QUICK -> {
                "Boss, aapke question ka direct answer yeh hai: '$prompt' ke context mein main bilkul ready hoon. Saare protocols actively monitor ho rahe hain."
            }
            ThinkingMode.DETAILED -> {
                "**Direct Answer:**\n'$prompt' ek crucial subject hai.\n\n" +
                "**Detailed Explanation:**\n" +
                "1. **Core Concept**: System is subject ko break down karke multi-layer analysis karta hai.\n" +
                "2. **Real-world Application**: Aap isko daily workflow aur automation mein effectively apply kar sakte hain.\n" +
                "3. **Recommendation**: Agle step ke roop mein detailed research ya implementation test run karein, Boss."
            }
            ThinkingMode.RESEARCH -> {
                "**RESEARCH DOSSIER: ${prompt.take(40)}**\n\n" +
                "**Summary**: Multiple reputable tech and scientific sources indicate high efficiency and transformative outcomes.\n\n" +
                "**Key Findings:**\n" +
                "• Source A confirms 40% performance improvement.\n" +
                "• Source B highlights zero-trust privacy and edge-processing safety.\n\n" +
                "**Conclusion**: Boss, recommendation yeh hai ki safely proceed karein with verified parameters."
            }
            ThinkingMode.STUDY -> {
                "Boss, aaiye is topic ko step-by-step samajhte hain:\n\n" +
                "• **Step 1 (The Foundation)**: Kisi bhi concept ko samajhne ke liye pehle basic principles clear hone chahiye.\n" +
                "• **Step 2 (The Mechanics)**: Yeh kaise kaam karta hai aur components aapas mein kaise interact karte hain.\n" +
                "• **Step 3 (Quick Quiz)**: Kya aap Step 2 ke trade-offs par discuss karna chahte hain?"
            }
            ThinkingMode.CREATIVE -> {
                "Ek futuristic horizon par, electric cyan aur cybernetic blue roshni ke beech, CJ aur Boss milkar ek naye digital era ki shuruat karte hain. Impossible ko possible banane ka waqt aa gaya hai!"
            }
            ThinkingMode.EXPERT -> {
                "**Technical Specification Analysis:**\n" +
                "```kotlin\n" +
                "// Architectural pattern optimized for low-latency & high concurrency\n" +
                "val optimizedFlow = repository.streamData()\n" +
                "    .flowOn(Dispatchers.IO)\n" +
                "    .distinctUntilChanged()\n" +
                "```\n" +
                "Complexity: O(1) space, O(N) throughput. Zero thread blocking."
            }
        }
    }

    fun parseActionIntent(input: String): ProposedAction? {
        val lower = input.lowercase(Locale.ROOT)

        // WhatsApp trigger
        if (lower.contains("whatsapp") || lower.contains("message")) {
            val phone = Regex("\\b\\d{10}\\b").find(input)?.value ?: ""
            val messageMatch = Regex("(?:saying|message|text|bolna|likhna)\\s+(.*)", RegexOption.IGNORE_CASE).find(input)?.groupValues?.get(1)
            val message = messageMatch ?: "Hello, this is an automated message sent via CJ Assistant."
            return ProposedAction(
                type = ActionType.WHATSAPP,
                title = "Send WhatsApp Message",
                target = if (phone.isNotBlank()) phone else "Selected Contact",
                detail = "Drafted message: \"$message\"",
                editablePayload = message,
                rawData = mapOf("phone" to phone, "message" to message)
            )
        }

        // Call trigger
        if (lower.contains("call") || lower.contains("dial") || lower.contains("phone mila")) {
            val phone = Regex("\\b\\d{10}\\b").find(input)?.value ?: ""
            val contact = if (phone.isNotBlank()) phone else "Contact"
            return ProposedAction(
                type = ActionType.CALL,
                title = "Make Phone Call",
                target = contact,
                detail = "Initiate phone call to $contact",
                editablePayload = phone,
                rawData = mapOf("phone" to phone)
            )
        }

        // Alarm trigger
        if (lower.contains("alarm") || lower.contains("timer") || lower.contains("jaga dena")) {
            val hourMatch = Regex("(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?", RegexOption.IGNORE_CASE).find(input)
            var hour = hourMatch?.groupValues?.get(1)?.toIntOrNull() ?: 7
            val min = hourMatch?.groupValues?.get(2)?.toIntOrNull() ?: 0
            val amPm = hourMatch?.groupValues?.get(3)?.lowercase()
            if (amPm == "pm" && hour < 12) hour += 12
            if (amPm == "am" && hour == 12) hour = 0

            return ProposedAction(
                type = ActionType.ALARM,
                title = "Set System Alarm",
                target = "%02d:%02d".format(hour, min),
                detail = "Alarm scheduled for %02d:%02d with title 'CJ Reminder'".format(hour, min),
                editablePayload = "CJ Reminder",
                rawData = mapOf("hour" to hour.toString(), "minute" to min.toString())
            )
        }

        // YouTube search trigger
        if (lower.contains("youtube") || lower.contains("video play")) {
            val query = input.replace(Regex("(?i)(search|play|video|on youtube|open youtube|youtube)"), "").trim()
            val targetQuery = if (query.isNotBlank()) query else "Technology breakthroughs"
            return ProposedAction(
                type = ActionType.YOUTUBE,
                title = "YouTube Video Search",
                target = targetQuery,
                detail = "Search and stream '$targetQuery' on YouTube",
                editablePayload = targetQuery,
                rawData = mapOf("query" to targetQuery)
            )
        }

        // Instagram trigger
        if (lower.contains("instagram") || lower.contains("insta")) {
            val username = Regex("@([A-Za-z0-9_.]+)").find(input)?.groupValues?.get(1) ?: "explore"
            return ProposedAction(
                type = ActionType.INSTAGRAM,
                title = "Open Instagram Profile",
                target = "@$username",
                detail = "Navigate to Instagram profile @$username",
                editablePayload = username,
                rawData = mapOf("username" to username)
            )
        }

        // Settings trigger
        if (lower.contains("setting") || lower.contains("wifi") || lower.contains("bluetooth") || lower.contains("brightness") || lower.contains("volume")) {
            val setting = when {
                lower.contains("wifi") -> "wifi"
                lower.contains("bluetooth") -> "bluetooth"
                lower.contains("brightness") || lower.contains("display") -> "display"
                lower.contains("sound") || lower.contains("volume") -> "sound"
                else -> "general"
            }
            return ProposedAction(
                type = ActionType.SYSTEM_SETTING,
                title = "Open Device Settings",
                target = setting.uppercase(),
                detail = "Navigate to device $setting settings",
                editablePayload = setting,
                rawData = mapOf("setting" to setting)
            )
        }

        return null
    }

    private fun evaluateBasicMath(input: String): String? {
        try {
            val clean = input.replace("x", "*").replace("times", "*").replace("plus", "+").replace("minus", "-")
            val match = Regex("(\\d+(?:\\.\\d+)?)\\s*([+\\-*/%])\\s*(\\d+(?:\\.\\d+)?)").find(clean) ?: return null
            val num1 = match.groupValues[1].toDouble()
            val op = match.groupValues[2]
            val num2 = match.groupValues[3].toDouble()
            val result = when (op) {
                "+" -> num1 + num2
                "-" -> num1 - num2
                "*" -> num1 * num2
                "/" -> if (num2 != 0.0) num1 / num2 else return "Division by zero is undefined"
                "%" -> num1 % num2
                else -> return null
            }
            return if (result % 1.0 == 0.0) result.toLong().toString() else "%.3f".format(result)
        } catch (e: Exception) {
            return null
        }
    }
}
