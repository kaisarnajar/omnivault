# 🔐 OmniVault

> **An all-in-one, offline-first Android productivity, security & utility hub — built with Jetpack Compose, Material 3, and Kotlin.**

OmniVault combines 11 essential tools into a single, beautifully designed app so you can manage tasks, track expenses, log workouts, track sleep, save web bookmarks, track debts, log moods, stay focused, and secure your secrets — all in one place.

---

## ✨ Features

| Feature | Description |
|---|---|
| ✅ **Tasks** | Manage to-dos with Eisenhower priority matrix, deadlines, and search |
| 📅 **Calendar** | View and organize your upcoming schedule at a glance |
| 🍅 **Pomodoro Timer** | Stay focused with timed sessions, breaks, ambient sounds, and session history |
| 📝 **Notes** | Jot down ideas, meeting minutes, and personal reflections |
| 💸 **Expense Tracker** | Monitor daily spending and categorize your financial outflow |
| 🔒 **Secret Vault** | Store passwords, API keys, and sensitive data behind biometric authentication |
| 📷 **QR Scanner** | Instantly scan and read QR codes and barcodes |
| 💳 **Credit / Debit Ledger** | Track who owes you and who you owe with transaction logs and net balances |
| 😄 **Mood Journal** | Log daily moods with emojis, reflections, and a chronological history timeline |
| 🔖 **Bookmarks** | Save and categorize URLs, articles, and web content for later reading |
| 🏋️ **Fitness Tracker** | Track gym workouts (with target muscles), running distance/duration, and sports |
| 😴 **Sleep Log** | Track nightly sleep schedules, sleep duration, and quality ratings |

### 🏠 Home Screen Widgets
Key features come with **Jetpack Glance home screen widgets** so you can view your tasks, notes, focus time, expenses, and vault secrets right from your Android home screen.

### 🔧 Dedicated Debug Tools
Includes a dedicated, scrollable **Debug Tools** modal dialog accessible from Profile settings to inject test sample data for all 9 data-backed features with instant toast feedback.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| **Language** | [Kotlin](https://kotlinlang.org/) |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) + Material 3 |
| **Widgets** | [Jetpack Glance](https://developer.android.com/jetpack/compose/glance) |
| **Dependency Injection** | [Dagger Hilt](https://dagger.dev/hilt/) |
| **Local Database** | [Room Database](https://developer.android.com/training/data-storage/room) |
| **Remote Database** | [Firebase Realtime Database](https://firebase.google.com/products/realtime-database) |
| **Authentication** | [Firebase Auth](https://firebase.google.com/docs/auth) (Email/Password + Google Sign-In) |
| **Biometric Security** | [AndroidX Biometric](https://developer.android.com/training/sign-in/biometric-auth) |
| **Async & State** | [Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html) |
| **Navigation** | Compose Navigation |

---

## 🏗 Architecture

```
app/
├── data/
│   ├── local/          # Room entities, DAOs, and database (v15)
│   ├── remote/         # Firebase remote data sources
│   └── repository/     # Repository implementations
├── di/                 # Hilt dependency injection modules
├── domain/             # Repository interfaces and domain models
├── ui/
│   ├── auth/           # Login & Registration screens
│   ├── bookmark/       # Bookmarks screen & ViewModel
│   ├── calendar/       # Calendar screen
│   ├── components/     # Shared UI components (GlassCard, OmniVaultBackground, etc.)
│   ├── expense/        # Expense tracker screen & ViewModel
│   ├── fitness/        # Fitness tracker screen & ViewModel
│   ├── ledger/         # Credit/Debit ledger screens & ViewModel
│   ├── mood/           # Mood journal screen & ViewModel
│   ├── notes/          # Notes list & detail screens
│   ├── pomodoro/       # Pomodoro timer screen & ViewModel
│   ├── profile/        # User profile & Debug Tools modal
│   ├── scanner/        # QR code scanner
│   ├── theme/          # Curated color palette, typography, and theming
│   ├── todos/          # Task list & add task screens
│   ├── tools/          # Central Tools Dashboard Hub
│   └── vault/          # Secret vault screens
├── widget/             # Jetpack Glance home screen widgets
├── worker/             # Alarm scheduler & receivers
└── MainActivity.kt
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17+
- An Android device or emulator (API 26+)

### Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/kaisarnajar/taskvault.git
   cd taskvault
   ```

2. **Configure Firebase:**
   - Go to the [Firebase Console](https://console.firebase.google.com/)
   - Create a new project (or use an existing one)
   - Add an Android app with package name `app.taskvault`
   - Download `google-services.json` and place it in the `app/` directory
   - Enable **Firebase Authentication** (Email/Password) and the **Realtime Database**

3. **Open in Android Studio:**
   - Open the project and let Gradle sync complete

4. **Run the app:**
   - Select your target device/emulator and click ▶️ Run

---

## 📊 Project Stats

| Metric | Value |
|---|---|
| Kotlin source files | ~100 |
| Lines of Kotlin code | ~10,000+ |
| Built-in Tools | 10 |
| Home screen widgets | 5 |

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

<p align="center">
  Built with ❤️ using Kotlin & Jetpack Compose
</p>
