package com.example.ai_browser.model

data class AIResult(
    val summary: String,
    val keywords: List<String>,
    val sentiment: String,
    val entities: List<String>
)
