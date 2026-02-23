package com.example.ai_browser

import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var urlEditText: EditText
    private lateinit var goButton: Button
    private lateinit var analyzeButton: Button
    private val localAI = LocalAI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)
        urlEditText = findViewById(R.id.url_edit_text)
        goButton = findViewById(R.id.go_button)
        analyzeButton = findViewById(R.id.analyze_button)

        setupWebView()
        setupListeners()
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: android.webkit.WebResourceRequest): Boolean {
                val url = request.url.toString()
                view.loadUrl(url)
                urlEditText.setText(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null) {
                    urlEditText.setText(url)
                }
            }
        }
        // Load a default page
        webView.loadUrl("https://en.wikipedia.org/wiki/Artificial_intelligence")
    }

    private fun setupListeners() {
        goButton.setOnClickListener {
            loadUrlFromInput()
        }

        urlEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_GO) {
                loadUrlFromInput()
                true
            } else {
                false
            }
        }

        analyzeButton.setOnClickListener {
            analyzePageContent()
        }
    }

    private fun loadUrlFromInput() {
        val url = urlEditText.text.toString()
        if (url.isNotEmpty()) {
            val finalUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else {
                url
            }
            webView.loadUrl(finalUrl)

            // Hide keyboard
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
            imm?.hideSoftInputFromWindow(urlEditText.windowToken, 0)
        }
    }

    private fun analyzePageContent() {
        // Extract text from the WebView
        webView.evaluateJavascript(
            "(function() { return document.body.innerText; })();"
        ) { value ->
            // The value returned is a JSON string (e.g., "\"Content...\""), so we need to unescape it properly.
            val text = try {
                if (value == null || value == "null") ""
                else org.json.JSONTokener(value).nextValue().toString()
            } catch (e: Exception) {
                value ?: ""
            }

            if (text.isBlank()) {
                Toast.makeText(this@MainActivity, "No content to analyze", Toast.LENGTH_SHORT).show()
            } else {
                // Perform local AI analysis
                val summary = localAI.summarize(text)
                showAnalysisResult(summary)
            }
        }
    }

    private fun showAnalysisResult(summary: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title)
            .setMessage(summary)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
