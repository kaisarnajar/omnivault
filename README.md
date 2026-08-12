# OmniVault (formerly TaskVault)

OmniVault is an offline-first Android application designed to be your ultimate all-in-one productivity and security hub. It features a modern, animated interface powered by Jetpack Compose and guarantees that you can interact with your tools instantly regardless of network conditions.

## Features

- **Tools Hub**: A centralized, grid-based dashboard giving you access to all your utilities.
- **Tasks**: Manage your to-do lists efficiently. View, add, edit, and delete tasks.
- **Calendar**: View your schedule and organize your events.
- **Pomodoro Timer**: Stay focused and track your productivity with an integrated Pomodoro timer and history logs.
- **Notes**: Take down quick thoughts and detailed notes with ease.
- **Expense Tracker**: Keep a close eye on your spending and monitor your financial health.
- **Secret Vault with Biometric Security**: Securely store sensitive information, passwords, and secrets behind your device's biometric authentication (fingerprint/face unlock).
- **Authentication**: Firebase Authentication for user accounts and secure profile management.
- **Offline-First Architecture**: Use the app completely offline with local storage (Room), syncing seamlessly to Firebase Realtime Database.
- **Modern UI**: Built entirely using Jetpack Compose with glassmorphism effects, smooth animations, and theming capabilities.

## Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Dependency Injection**: [Dagger Hilt](https://dagger.dev/hilt/)
- **Local Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Remote Database**: [Firebase Realtime Database](https://firebase.google.com/products/realtime-database)
- **Authentication**: [Firebase Auth](https://firebase.google.com/docs/auth)
- **Security**: [AndroidX Biometric](https://developer.android.com/training/sign-in/biometric-auth)
- **Asynchronous Programming**: [Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)
- **Navigation**: Compose Navigation

## Setup Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/kaisarnajar/taskvault.git
   cd taskvault
   ```

2. **Open in Android Studio:**
   - Launch Android Studio and select "Open an existing project".
   - Navigate to the cloned `taskvault` directory.
   - Let Android Studio download dependencies and perform the initial Gradle sync.

3. **Configure Firebase:**
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Create a new project or select an existing one.
   - Add an Android app to the Firebase project with the package name `app.taskvault`.
   - Download the generated `google-services.json` file.
   - Place `google-services.json` inside the `app/` directory of the project.
   - Set up **Firebase Authentication** (Email/Password) and the **Firebase Realtime Database**.

4. **Run the application:**
   - Select an emulator or physical device.
   - Click the "Run" button in Android Studio.
