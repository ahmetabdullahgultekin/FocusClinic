# İrade (Willpower)

A gamified productivity app that strengthens willpower and self-control. Combines Pomodoro-style focus mechanics with RPG progression, custom goals, and a Token Economy system.

**"Strengthen your willpower, one focus session at a time."** Earn sparks by focusing, complete personal goals, and spend rewards on real-life treats or in-app power-ups.

## Features

- **Focus Engine** — Timed focus sessions (5–60 min) with real-time countdown, interruption detection, and reward calculation
- **Willpower Goals** — Create, complete, and track personal goals with spark/XP rewards
- **Calendar Heatmap** — Monthly view showing goal completion activity
- **Power Shop** — Buy willpower-themed items (Focus Stone, Perseverance Shield, etc.) that boost XP/spark multipliers
- **Custom Rewards** — Create real-life rewards ("Watch an episode", "Coffee break") and redeem with earned sparks
- **Player Progression** — Level up from Beginner to Legend with XP-based advancement
- **Celebrations** — Confetti animation and haptic feedback on session completion
- **Localization** — Turkish (primary) + English

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
│   ├── commonMain/   # Shared UI, components, string resources
│   ├── androidMain/  # Android: ForegroundService, WakeLock, haptics, notifications
│   └── iosMain/      # iOS: UNUserNotificationCenter, UIImpactFeedbackGenerator
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
- [`roadmap.md`](roadmap.md) — Project milestones and upcoming work
