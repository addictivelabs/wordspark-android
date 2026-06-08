# WordSpark Android

A daily word challenge app built with Kotlin and Jetpack Compose.

## Overview

WordSpark presents players with a new word puzzle every day. Players score points by solving the puzzle within the time limit and build streaks by playing consistently.

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Navigation:** Navigation Compose
- **Backend:** Firebase Auth + Firestore
- **Billing:** Google Play Billing v6
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 35

## Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK with API 35

### Firebase Setup

1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add an Android app with package name `com.wordspark.app`
3. Download `google-services.json` and place it in the `app/` directory
4. Enable **Authentication** (Email/Password) and **Firestore** in the Firebase console

### Build

```bash
./gradlew assembleDebug
```

The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## CI

GitHub Actions runs `./gradlew assembleDebug` on every push and pull request to `main`. See [`.github/workflows/build.yml`](.github/workflows/build.yml).

## Project Structure

```
app/src/main/java/com/wordspark/app/
├── MainActivity.kt
├── WordSparkApplication.kt
├── navigation/
│   └── AppNavigation.kt
└── ui/
    ├── home/HomeScreen.kt
    ├── puzzle/PuzzleScreen.kt
    ├── results/ResultsScreen.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```
