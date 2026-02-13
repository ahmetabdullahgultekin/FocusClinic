# Focus Clinic

A gamified productivity app for students preparing for difficult exams. Combines Pomodoro-style focus mechanics with RPG elements (managing a dental clinic) and a Token Economy system.

**"Productivity meets RPG."** Earn currency by focusing, spend it on real-life rewards or in-game clinic upgrades.

## Tech Stack

| Concern | Choice |
|:---|:---|
| Language | Kotlin 2.1.20 (100% commonMain) |
| UI | Compose Multiplatform 1.10.0 (Material 3) |
| Architecture | MVI + Clean Architecture |
| DI | Koin 4.1.1 |
| Navigation | Decompose 3.4.0 |
| Database | SQLDelight 2.2.1 |
| Async | Kotlin Coroutines 1.10.2 & Flow |

## Platform

- Android (minSdk 24, targetSdk 35)
- iOS (x64, arm64, simulator arm64)

## Project Structure

```
FocusClinic/
├── core/
│   ├── domain/       # Pure Kotlin — entities, value objects, use cases, ports
│   └── data/         # SQLDelight, repository implementations, mappers
├── composeApp/       # UI, ViewModels, DI, platform adapters (expect/actual)
│   ├── commonMain/   # Shared UI and business logic
│   ├── androidMain/  # Android platform: ForegroundService, WakeLock, notifications
│   └── iosMain/      # iOS platform: UNUserNotificationCenter
├── androidApp/       # Android entry point
└── iosApp/           # iOS entry point
```

## Prerequisites

- **JDK 17+** (JDK 23 recommended; JDK 25 is incompatible with Gradle 8.11.1)
- **Android SDK** with compileSdk 35
- **Xcode 15+** (for iOS builds)

## Build & Run

### Android

```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Install and run on connected device/emulator
./gradlew :composeApp:installDebug
```

### iOS

Open the `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator or device.

Alternatively, build the shared framework:

```bash
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

### Tests

```bash
# Domain unit tests (value objects, rules, use cases)
./gradlew :core:domain:allTests

# Data layer integration tests (SQLDelight repositories)
./gradlew :core:data:testDebugUnitTest

# Presentation layer ViewModel tests
./gradlew :composeApp:allTests
```

## Documentation

- [`ADD.md`](ADD.md) — Architecture Design Document (full specification)
- [`CLAUDE.md`](CLAUDE.md) — Engineering standards and project rules
- [`CHANGELOG.md`](CHANGELOG.md) — Change log
