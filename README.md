# AI Android Browser (No API)

This is a simple Android browser application that features simulated local AI capabilities without relying on external APIs.

## Features

-   **Browse the Web**: Enter a URL to navigate to any website.
-   **Local AI Analysis**: Click "Analyze Page" to extract text from the current page and generate a summary and keyword list.
-   **Privacy First**: All processing happens on-device using heuristic algorithms. No data is sent to external AI services.

## Architecture

-   **Language**: Kotlin
-   **UI**: XML Layouts (ConstraintLayout)
-   **Browser Engine**: Android `WebView`
-   **AI Engine**: `LocalAI.kt` (Custom implementation simulating NLP tasks locally)

## Building the Project

1.  Clone the repository.
2.  Open in Android Studio or use the command line.
3.  Build the APK:
    ```bash
    ./gradlew assembleDebug
    ```
4.  Run unit tests:
    ```bash
    ./gradlew testDebugUnitTest
    ```

## Requirements

-   Android SDK 34
-   JDK 17+ (Project uses Java 1.8 compatibility)

## Notes

This project was created based on the requirements inferred from the filename `AI_Android_Browser_No_API_Prompt.md`.
