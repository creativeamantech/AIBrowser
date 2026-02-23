package com.example.ai_browser

import com.example.ai_browser.model.LocalAI
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalAITest {

    private val localAI = LocalAI()

    @Test
    fun analyze_returnsCompleteResult() {
        val input = "Artificial intelligence (AI) is intelligence demonstrated by machines, as opposed to the natural intelligence displayed by humans. Leading AI textbooks define the field as the study of \"intelligent agents\": any device that perceives its environment and takes actions that maximize its chance of successfully achieving its goals. Colloquially, the term \"artificial intelligence\" is often used to describe machines (or computers) that mimic \"cognitive\" functions that humans associate with the human mind, such as \"learning\" and \"problem solving\"."

        val result = localAI.analyze(input)

        // Check if keywords are not empty
        assertTrue(result.keywords.isNotEmpty())
        // Sentiment might vary based on heuristic, but we assert it is not empty
        assertTrue(result.sentiment.isNotEmpty())
    }

    @Test
    fun analyze_sentimentPositive() {
        val input = "This is a great, wonderful, and amazing product. I love it!"
        val result = localAI.analyze(input)
        assertEquals("Positive", result.sentiment)
    }

    @Test
    fun analyze_sentimentNegative() {
        val input = "This is a terrible, horrible, and awful product. I hate it!"
        val result = localAI.analyze(input)
        assertEquals("Negative", result.sentiment)
    }
}
