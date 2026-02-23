# 🤖 Autonomous Android Browser — Zero Paid API Build Prompt
### Fully On-Device AI · Free Telegram Bot · No External API Costs

---

## 🧠 PROJECT OVERVIEW

Build a fully autonomous **Android browser application** that:
- Learns and memorizes browser tasks by watching user interactions
- Executes tasks automatically and dynamically using on-device intelligence
- Receives real-time commands via a **free Telegram Bot** (no paid API)
- Uses **TensorFlow Lite** exclusively for all on-device AI/ML inference
- Works completely offline for task execution (only Telegram needs internet)

---

## 🏗️ TECH STACK — 100% FREE, NO PAID APIs

| Layer | Technology |
|---|---|
| Language | Kotlin (Android) |
| Web Rendering | Android WebView (built-in) |
| AI Engine | TensorFlow Lite (on-device, free) |
| Task Automation | Accessibility Services + UIAutomator2 |
| Telegram | Telegram Bot API (free, just needs a bot token from @BotFather) |
| Local Storage | Room Database (SQLite) |
| Task Queue | WorkManager |
| Networking | OkHttp (for Telegram polling only) |
| Background | Foreground Service |
| Scheduling | WorkManager (built-in Android) |

> ✅ **No OpenAI, No Gemini, No paid LLM — everything runs on the device using TFLite models**

---

## 📱 CORE MODULES TO BUILD

---

### 1. 🌐 Custom Android Browser

```
- WebView-based browser with full JavaScript injection
- Inject JS scripts to: click, type, scroll, extract data, wait for elements
- DOM traversal using document.querySelector and XPath via injected JS
- Support persistent cookies/sessions (CookieManager)
- Multiple tabs via TabLayout + ViewPager2
- Screenshot capture using View.getDrawingCache() or PixelCopy API
- Simulate gestures (click, swipe, long press) via MotionEvent dispatch
- Override WebViewClient to monitor page load events and URL changes
- Block ads/trackers via custom WebViewClient shouldInterceptRequest()
```

---

### 2. 🧬 TensorFlow Lite Intelligence Layer (100% On-Device)

Use pre-trained or custom-trained TFLite models for:

#### A. 🔍 UI Element Classifier Model
```
- Input: screenshot region (bitmap crop)
- Output: element type → [BUTTON, INPUT, LINK, DROPDOWN, CHECKBOX, TEXT, IMAGE]
- Used when a saved CSS selector fails (element moved or renamed)
- Falls back to visual detection: find "something that looks like a button"
- Train using labeled Android UI screenshots (Rico dataset or custom)
- Model format: .tflite — runs fully on-device, no internet needed
```

#### B. 📐 Element Locator (Visual Fallback)
```
- When DOM selector fails:
  1. Capture screenshot of current WebView
  2. Run TFLite object detection model (MobileNet SSD or EfficientDet)
  3. Detect bounding box of target element by visual similarity
  4. Convert bounding box coordinates to WebView touch coordinates
  5. Dispatch MotionEvent to simulate tap on detected location
- Handles dynamic websites where class names/IDs change frequently
```

#### C. ✅ Task Success / Failure Detector
```
- Input: before-screenshot + after-screenshot pair
- Output: success confidence score (0.0 → 1.0)
- Compare pixel difference + structural similarity (SSIM)
- Use TFLite image similarity model to detect meaningful page change
- If score < 0.5 → retry the step (up to 3 times with delay)
- Trigger alert to Telegram if all retries fail
```

#### D. 📝 Text Region Extractor (OCR — on-device)
```
- Use ML Kit Text Recognition (free, bundled, no API key needed)
- Extract visible text from screenshots for validation
- E.g., confirm "Welcome, John" appeared after login
- Used to verify task outcomes without relying on DOM alone
```

#### E. 🧠 Action Pattern Recognizer
```
- Record sequences of user interactions as feature vectors
- Train a simple TFLite sequence classifier (LSTM or Conv1D)
- Learn which action patterns belong to which task type
- Improve task matching over time as user runs more tasks
- Model improves on-device using transfer learning (no server needed)
```

---

### 3. 📡 Telegram Bot Integration (Free)

> Telegram Bot API is **completely free** — just create a bot via @BotFather and get a token. No credit card, no billing.

```
Setup:
  1. Message @BotFather on Telegram → /newbot → get BOT_TOKEN
  2. Store BOT_TOKEN in Android app (encrypted via Android Keystore)
  3. Start a Foreground Service on Android that polls:
     GET https://api.telegram.org/bot{TOKEN}/getUpdates?offset={offset}
  4. Parse incoming messages → dispatch to BotCommandHandler
  5. Send replies via:
     POST https://api.telegram.org/bot{TOKEN}/sendMessage
     POST https://api.telegram.org/bot{TOKEN}/sendPhoto (for screenshots)

Bot Commands:
  /run <task_name>          → Execute a saved learned task
  /learn <task_name>        → Start recording a new task from user interaction
  /stop                     → Stop current recording or running task
  /list                     → List all saved tasks with descriptions
  /status                   → Show current browser state + active task
  /screenshot               → Capture screen and send to Telegram
  /goto <url>               → Navigate browser to URL
  /click <css_selector>     → Click a DOM element
  /type <text>              → Type into currently focused input
  /scroll <up|down>         → Scroll the page
  /extract <selector>       → Extract and return text from a DOM element
  /delete <task_name>       → Remove a saved task
  /schedule <task> <time>   → Schedule task (e.g., /schedule login_gmail 08:00)
  /repeat <task> <interval> → Repeat task every N minutes
  /cancel_schedule <task>   → Cancel a scheduled task
  /retry <task_name>        → Retry last failed task
  /help                     → Show all commands

Polling loop (Foreground Service):
  - Poll every 1500ms when active, 5s when idle
  - Use offset tracking to avoid processing duplicate messages
  - Run as sticky Foreground Service with notification (prevents kill)
  - Restart on boot via RECEIVE_BOOT_COMPLETED broadcast receiver
```

---

### 4. 🎬 Task Recorder

```
When /learn <task_name> is sent:
  1. Enter RECORD MODE — display overlay indicator on screen
  2. Intercept all touch events on WebView via OnTouchListener
  3. Intercept JS events via injected JavaScript:
       document.addEventListener('click', ...)
       document.addEventListener('input', ...)
  4. For each interaction, record an ActionStep:
     {
       "action": "click" | "type" | "scroll" | "navigate" | "wait",
       "selector": "#email-input",           // CSS selector
       "xpath": "//input[@type='email']",    // XPath fallback
       "text_hint": "Email address field",   // Text near element (for AI fallback)
       "value": "user@example.com",          // For type actions
       "delay_after_ms": 800,               // Learned timing
       "screenshot_hash": "abc123"           // Visual fingerprint
     }
  5. When /stop is sent → save complete task to Room DB
  6. Confirm via Telegram: "✅ Task 'login_gmail' saved with 6 steps"
```

---

### 5. ⚙️ Task Executor

```
Execution flow for each task:
  1. Load ActionStep list from Room DB
  2. Open WebView, navigate to first URL
  3. For each step:
     a. Try CSS selector → inject JS to perform action
     b. If selector fails → use TFLite Element Locator (visual fallback)
     c. Wait for page to settle (MutationObserver via injected JS)
     d. Run TFLite Success Detector on before/after screenshots
     e. If failed → retry up to 3x with exponential backoff
     f. If all retries fail → report to Telegram and halt
  4. On task completion → capture final screenshot → send to Telegram

JavaScript injection examples:
  // Click:
  document.querySelector('#btn-login').click();

  // Type:
  let el = document.querySelector('#email');
  el.focus();
  el.value = 'user@mail.com';
  el.dispatchEvent(new Event('input', {bubbles: true}));

  // Wait for element:
  new MutationObserver((m, obs) => {
    if (document.querySelector('.welcome-message')) {
      obs.disconnect(); window._taskReady = true;
    }
  }).observe(document.body, {childList: true, subtree: true});

  // Extract text:
  document.querySelector('.price').innerText;
```

---

### 6. 📅 Scheduler

```
- Use Android WorkManager for reliable background scheduling
- /schedule login_gmail 08:00 → creates a PeriodicWorkRequest
- /repeat scrape_price 30 → repeats every 30 minutes
- Chain tasks: TaskA.then(TaskB).then(SendReport)
- Trigger conditions supported:
    - Time-based (daily at X:XX)
    - Interval-based (every N minutes)
    - Network connected trigger
    - Battery level trigger (only run if >30%)
- All schedules stored in Room DB and survive app restarts
```

---

## 📂 PROJECT STRUCTURE

```
app/
├── browser/
│   ├── CustomWebView.kt             # WebView + JS injection engine
│   ├── BrowserActivity.kt           # Main browser UI + tab management
│   ├── JSInjector.kt                # JS templates for click/type/scroll
│   └── PageStateMonitor.kt          # MutationObserver + load detection
│
├── automation/
│   ├── TaskExecutor.kt              # Step-by-step task runner
│   ├── ActionRecorder.kt            # Records user interactions
│   ├── GestureSimulator.kt          # MotionEvent-based tap/swipe
│   └── StepRetryHandler.kt          # Retry logic with backoff
│
├── tflite/
│   ├── UIElementClassifier.kt       # Classifies element type from bitmap
│   ├── VisualElementLocator.kt      # Finds elements by visual detection
│   ├── SuccessDetector.kt           # Before/after screenshot comparison
│   ├── OCRExtractor.kt              # ML Kit text extraction (free)
│   └── models/
│       ├── ui_classifier.tflite
│       ├── element_detector.tflite
│       └── success_detector.tflite
│
├── telegram/
│   ├── TelegramPollingService.kt    # Foreground service, polls getUpdates
│   ├── BotCommandHandler.kt         # Parses and routes /commands
│   ├── TelegramSender.kt            # Sends messages + photos
│   └── BootReceiver.kt              # Restart service on device reboot
│
├── data/
│   ├── AppDatabase.kt               # Room database
│   ├── TaskDao.kt                   # Task CRUD operations
│   ├── ScheduleDao.kt               # Schedule CRUD
│   └── models/
│       ├── Task.kt                  # Task entity
│       ├── ActionStep.kt            # Individual step entity
│       └── Schedule.kt              # Schedule entity
│
└── scheduler/
    ├── TaskScheduler.kt             # WorkManager job creator
    ├── ScheduledTaskWorker.kt       # WorkManager worker
    └── ChainBuilder.kt              # Multi-task chaining
```

---

## 🔐 PERMISSIONS (AndroidManifest.xml)

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>

<!-- For Accessibility-based automation -->
<uses-permission android:name="android.permission.BIND_ACCESSIBILITY_SERVICE"/>
```

---

## ⚙️ SETUP INSTRUCTIONS

```
Step 1 — Create Telegram Bot (free):
  → Open Telegram → message @BotFather
  → Send /newbot → follow prompts → copy your BOT_TOKEN

Step 2 — Add token to app:
  → Store in res/values/secrets.xml (gitignored)
  → Or enter via setup screen on first launch
  → Encrypted and stored in Android Keystore

Step 3 — Add TFLite models to assets/:
  → Download pre-trained MobileNet SSD from TensorFlow Hub (free)
  → Place ui_classifier.tflite in app/src/main/assets/models/
  → (Optional) fine-tune on Android UI datasets for better accuracy

Step 4 — Grant Permissions on device:
  → Enable Accessibility Service in Android Settings
  → Grant Overlay permission (SYSTEM_ALERT_WINDOW)
  → Allow app to run in background (disable battery optimization)

Step 5 — Launch & Connect:
  → Open app → Telegram polling service starts automatically
  → Open your Telegram bot chat
  → Send /help → you'll see all available commands
  → Send /goto https://example.com → browser navigates
  → Send /learn my_task → start recording!
```

---

## 🚀 FULL EXAMPLE FLOW

```
1. User on Telegram sends:   /learn login_amazon

2. App enters RECORD MODE:
   - Shows "🔴 Recording" overlay on browser
   - User navigates to amazon.com manually
   - User types email, password, clicks Sign In
   - User sends: /stop

3. App saves task "login_amazon" with 5 steps to Room DB
   Telegram reply: "✅ Saved 'login_amazon' — 5 steps recorded"

4. Next morning at 8AM (via /schedule login_amazon 08:00):
   - WorkManager triggers ScheduledTaskWorker
   - TaskExecutor loads steps from Room DB
   - WebView opens amazon.com
   - JS injection: types email → types password → clicks login
   - TFLite SuccessDetector: confirms page changed to home
   - Screenshot captured
   - Telegram message: "✅ login_amazon completed" + screenshot

5. If a step fails (e.g., Amazon changed button ID):
   - TFLite VisualElementLocator activates
   - Scans screenshot for "something that looks like a login button"
   - Taps detected coordinates
   - Retries up to 3 times
   - If still failing: "❌ Step 3 failed after 3 retries. Screenshot attached."
```

---

## 💡 ADVANCED FEATURES (Phase 2)

```
- On-device model fine-tuning: improve TFLite models from your own usage data
- Task chaining: /chain login_amazon → scrape_wishlist → send_report
- Data extraction to local CSV/JSON (no external DB needed)
- Stealth mode: randomize user-agent, vary action timing to avoid bot detection
- Multi-profile: store and switch between different login sessions
- Self-healing tasks: auto-update selectors when TFLite detects UI changes
- Local notification triggers: run task when a notification contains certain text
- P2P control: control device from another Android device on same WiFi (no internet)
```

---

## ✅ COST SUMMARY

| Component | Cost |
|---|---|
| TensorFlow Lite | Free & open source |
| ML Kit (OCR) | Free (Google, no key needed) |
| Telegram Bot API | Free forever (just needs BOT_TOKEN from @BotFather) |
| Android WebView | Free (built into Android) |
| Room / WorkManager | Free (Android Jetpack) |
| Pre-trained TFLite models (TF Hub) | Free |
| **TOTAL** | **$0.00** |

---

*No subscriptions. No API bills. No data leaving your device except Telegram messages.*
