package com.example.ai_browser

import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_browser.model.AIResult
import com.example.ai_browser.model.LocalAI
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var urlEditText: EditText
    private lateinit var goButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var forwardButton: ImageButton
    private lateinit var clearButton: ImageButton
    private lateinit var analyzeButton: ExtendedFloatingActionButton
    private lateinit var progressBar: ProgressBar
    private val localAI = LocalAI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)
        urlEditText = findViewById(R.id.url_edit_text)
        goButton = findViewById(R.id.go_button)
        backButton = findViewById(R.id.back_button)
        forwardButton = findViewById(R.id.forward_button)
        clearButton = findViewById(R.id.clear_button)
        analyzeButton = findViewById(R.id.analyze_button)
        progressBar = findViewById(R.id.progress_bar)

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
                updateNavigationButtons()
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null) {
                    urlEditText.setText(url)
                }
                updateNavigationButtons()
                progressBar.visibility = View.INVISIBLE
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }
        }
        // Load a default page
        webView.loadUrl("https://en.wikipedia.org/wiki/Artificial_intelligence")
    }

    private fun updateNavigationButtons() {
        backButton.isEnabled = webView.canGoBack()
        forwardButton.isEnabled = webView.canGoForward()
        backButton.alpha = if (webView.canGoBack()) 1.0f else 0.5f
        forwardButton.alpha = if (webView.canGoForward()) 1.0f else 0.5f
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

        urlEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        clearButton.setOnClickListener {
            urlEditText.text.clear()
        }

        backButton.setOnClickListener {
            if (webView.canGoBack()) webView.goBack()
        }

        forwardButton.setOnClickListener {
            if (webView.canGoForward()) webView.goForward()
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
            val result = localAI.analyze(text)
            showAnalysisResult(result)
            }
        }
    }

    private fun showAnalysisResult(result: AIResult) {
        val message = buildString {
            append("## Summary\n${result.summary}\n\n")
            append("## Keywords\n${result.keywords.joinToString(", ")}\n\n")
            append("## Sentiment\n${result.sentiment}\n\n")
            if (result.entities.isNotEmpty()) {
                append("## Key Entities\n${result.entities.joinToString(", ")}")
            }
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title)
            .setMessage(message)
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
