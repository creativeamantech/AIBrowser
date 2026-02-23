package com.example.ai_browser

import java.util.Locale

class LocalAI {

    /**
     * Simulates a local AI model by summarizing the text.
     * In a real "No API" scenario, this would use a TFLite model or On-Device ML.
     * Here, we use a heuristic approach to demonstrate the concept without heavy model downloads.
     */
    fun summarize(text: String): String {
        if (text.isBlank()) return "No content to analyze."

        val sentences = text.split(Regex("[.!?]")).filter { it.isNotBlank() }
        val words = text.lowercase(Locale.ROOT).split(Regex("\\W+")).filter { it.isNotBlank() && it.length > 3 }

        // simple keyword extraction (frequency based)
        val wordFreq = words.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(5).map { it.key }

        // Simple extraction summarization (first 3 meaningful sentences)
        val summarySentences = sentences.take(3).joinToString(". ") { it.trim() }

        return buildString {
            append("## AI Analysis (Local Model Simulation)\n\n")
            append("**Key Topics:** ${wordFreq.joinToString(", ")}\n\n")
            append("**Summary:**\n$summarySentences.")
            if (sentences.size > 3) {
                append("..")
            }
            append("\n\n(Processed locally on device)")
        }
    }
}
