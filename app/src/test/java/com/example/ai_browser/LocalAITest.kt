package com.example.ai_browser

import org.junit.Assert.assertTrue
import org.junit.Test

class LocalAITest {

    private val localAI = LocalAI()

    @Test
    fun summarize_returnsSummary() {
        val input = "Artificial intelligence (AI) is intelligence demonstrated by machines, as opposed to the natural intelligence displayed by humans. Leading AI textbooks define the field as the study of \"intelligent agents\": any device that perceives its environment and takes actions that maximize its chance of successfully achieving its goals. Colloquially, the term \"artificial intelligence\" is often used to describe machines (or computers) that mimic \"cognitive\" functions that humans associate with the human mind, such as \"learning\" and \"problem solving\"."

        val summary = localAI.summarize(input)

        println(summary)
        assertTrue(summary.contains("AI Analysis"))
        assertTrue(summary.contains("Key Topics:"))
        assertTrue(summary.contains("intelligence"))
    }

    @Test
    fun summarize_emptyInput_returnsMessage() {
        val summary = localAI.summarize("")
        assertTrue(summary.contains("No content to analyze"))
    }
}
