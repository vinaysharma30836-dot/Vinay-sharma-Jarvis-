package com.example.ai

import android.graphics.Bitmap

enum class ThinkingMode(val displayName: String, val promptDirective: String) {
    QUICK(
        "Quick Mode",
        "Give a direct, concise, and punchy answer (2-4 sentences max), followed by quick next steps if relevant."
    ),
    DETAILED(
        "Detailed Mode",
        "First provide a clear direct answer, followed by a well-structured detailed breakdown with examples and logical steps."
    ),
    RESEARCH(
        "Research Mode",
        "Conduct a deep research synthesis: cross-compare perspectives, identify potential conflicting claims, summarize key findings with simulated source citations, and provide an executive conclusion."
    ),
    STUDY(
        "Study Mode",
        "Act as an elite interactive tutor: break concepts down step-by-step using intuitive analogies, simple language, and check understanding."
    ),
    CREATIVE(
        "Creative Mode",
        "Unleash high creativity, imaginative brainstorming, engaging tone, and expressive writing."
    ),
    EXPERT(
        "Expert Mode",
        "Provide rigorous senior engineering and technical depth, architectural trade-offs, edge cases, and production-ready code."
    )
}

enum class AiProviderType(val displayName: String) {
    GEMINI("Google Gemini (Cloud AI)"),
    EMBEDDED_JARVIS("Embedded JARVIS Core (Offline/Local)")
}

data class MessagePayload(
    val role: String, // "user" or "model"
    val content: String,
    val image: Bitmap? = null
)

data class ResearchReport(
    val topic: String,
    val executiveSummary: String,
    val keyPoints: List<String>,
    val comparisonPoints: List<Pair<String, String>>,
    val sources: List<String>,
    val conclusion: String
)
