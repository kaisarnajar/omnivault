# TaskVault

TaskVault is an offline-first Android application designed for managing TODOs and notes efficiently. It guarantees that the user can interact with their tasks instantly regardless of network conditions.

## Features

- **Offline-First Architecture**: View, add, edit, and delete tasks even without an internet connection.
- **Real-time Syncing**: Automatically synchronizes your tasks with Firebase when the network is available.
- **Modern UI**: Built entirely using Jetpack Compose for a responsive, modern interface.
- **MVVM Architecture**: Follows the Model-View-ViewModel pattern for separation of concerns and maintainability.

## Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Local Database (SSOT)**: [Room](https://developer.android.com/training/data-storage/room)
- **Remote Database**: [Firebase Realtime Database](https://firebase.google.com/products/realtime-database)
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
   - Set up the **Firebase Realtime Database** in test mode (or configure appropriate security rules).

4. **Run the application:**
   - Select an emulator or physical device.
   - Click the "Run" button in Android Studio.
