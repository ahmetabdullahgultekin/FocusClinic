# Focus Clinic

A gamified productivity app for students preparing for difficult exams. Combines Pomodoro-style focus mechanics with RPG elements (managing a dental clinic) and a Token Economy system.

**"Productivity meets RPG."** Earn currency by focusing, spend it on real-life rewards or in-game clinic upgrades.

## Tech Stack

| Concern | Choice |
|:---|:---|
| Language | Kotlin (100% commonMain) |
| UI | Compose Multiplatform (Material 3) |
| Architecture | MVI + Clean Architecture |
| DI | Koin |
| Navigation | Decompose |
| Database | SQLDelight |
| Async | Kotlin Coroutines & Flow |

## Platform

- Android
- iOS

## Project Structure

```
FocusClinic/
├── core/
│   ├── domain/       # Pure Kotlin — entities, value objects, use cases, ports
│   └── data/         # SQLDelight, repository implementations, mappers
├── composeApp/       # UI, ViewModels, DI, platform adapters (expect/actual)
├── androidApp/       # Android entry point
└── iosApp/           # iOS entry point
```

## Documentation

- [`ADD.md`](ADD.md) — Architecture Design Document (full specification)
- [`CLAUDE.md`](CLAUDE.md) — Engineering standards and project rules
- [`CHANGELOG.md`](CHANGELOG.md) — Change log

## Build & Run

> Setup instructions will be added once the project is initialized.
