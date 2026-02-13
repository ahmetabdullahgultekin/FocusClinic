# Changelog

All notable changes to the Focus Clinic project will be documented in this file.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Project Review — Roadmap Status (2026-02-13)

**Overall completion: ~85% of ADD.md spec.**

All 9 implementation steps addressed. Core application logic (domain, data, 4 UI screens, 90+ unit tests) is complete. Remaining gaps:

#### Outstanding Items (by priority)

**P1 — Platform Reliability:**
1. Android ForegroundService + WakeLock for timer survival when backgrounded (manifest permissions declared, no implementation)
2. Timer completion notification — `expect/actual NotificationManager` for both platforms
3. iOS background task registration via `BGTaskScheduler`

**P2 — Spec Compliance:**
4. `UpgradeClinicUseCase` — listed in ADD.md Section 3A but not implemented; decide if shop purchases cover this or implement separately
5. Data layer integration tests — `:core:data` `commonTest` with SQLDelight in-memory driver
6. Compose UI tests — `:composeApp` `commonTest` covering critical flows

**P3 — Documentation:**
7. README.md build instructions (JDK, Android SDK, run commands)

#### Deviations from Spec
- Dark mode implemented despite ADD.md Section 7 marking it "Not in V1 scope" — positive deviation, ADD.md updated.

### Added
- **Gradle multi-module KMP project** — `:core:domain`, `:core:data`, `:composeApp` with compile-time dependency enforcement.
- **Tech stack configured** — Kotlin 2.1.20, Compose Multiplatform 1.10.0, SQLDelight 2.2.1, Koin 4.1.1, Decompose 3.4.0.
- **Domain Layer (`:core:domain`):**
  - Entities: `FocusSession`, `UserProfile`, `ShopItem`, `InventoryItem`, `CustomReward`, `Transaction`, `ClinicAttributes`.
  - Value Objects: `Coin`, `ExperiencePoints`, `FocusDuration`, `Multiplier` (self-validating).
  - Sealed types: `SessionStatus`, `ModifierType`, `TransactionType`, `DomainError`, `DomainResult`.
  - Business rules: `RewardCalculator`, `MultiplierCalculator`, `FocusRules`, `ProgressionRules`, `PlayerLevel`.
  - Repository ports: `FocusSessionRepository`, `UserProfileRepository`, `InventoryRepository`, `CustomRewardRepository`, `TransactionRepository`.
  - Use cases: `StartFocusSession`, `CompleteFocusSession`, `InterruptFocusSession`, `PurchaseShopItem`, `PurchaseCustomReward`, `GetUserStats`.
- **Data Layer (`:core:data`):**
  - SQLDelight schema: 5 tables (`user_profile`, `focus_sessions`, `inventory`, `custom_rewards`, `transactions`).
  - Repository implementations: `SqlDelightFocusSessionRepository`, `SqlDelightUserProfileRepository`, `SqlDelightInventoryRepository`, `SqlDelightCustomRewardRepository`, `SqlDelightTransactionRepository`.
  - Mappers: extension functions for all DTO-to-domain conversions.
  - `expect/actual` DriverFactory for Android (AndroidSqliteDriver) and iOS (NativeSqliteDriver).
- **Presentation Layer (`:composeApp`):**
  - Decompose navigation with `RootComponent` and `ChildStack` (Focus, Clinic, Shop, Stats screens).
  - Bottom navigation bar with Material Icons.
  - Koin DI modules (`dataModule`, `domainModule`) wiring all layers.
  - Material 3 theme (Teal + Soft Blue palette).
  - Android entry point (`MainActivity`) and iOS entry point (`MainViewController`).
  - Placeholder screens for all 4 tabs.
- **Focus Feature (Step 5):**
  - MVI architecture: `FocusIntent`, `FocusState` (with `FocusPhase` sealed interface), `FocusViewModel`.
  - Coroutine-based countdown timer with 1-second tick interval.
  - Duration selector with predefined options (5, 10, 15, 25, 45, 60 min).
  - Circular timer ring with animated progress arc.
  - Patient visual with state-driven emoji (waiting, treating, happy, angry).
  - Session result card showing earned XP and Coins.
  - Grace period logic for app backgrounding (10-second tolerance before interruption).
  - `expect/actual` lifecycle observer: Android (`LifecycleEventObserver`), iOS (`NSNotificationCenter`).
  - Koin DI: `presentationModule` for ViewModel, `platformModule` for DriverFactory (expect/actual).
  - Centralized UI strings in `Strings` object (no hardcoded strings).
- **Shop Feature (Step 6):**
  - `ShopCatalog` — 8 predefined clinic items (4 equipment with XP/Coin bonuses, 4 decorations).
  - `SaveCustomRewardUseCase`, `DeactivateCustomRewardUseCase` — CRUD for user-created rewards.
  - MVI architecture: `ShopIntent`, `ShopState`, `ShopViewModel` with reactive balance/inventory/rewards.
  - Two-tab UI: Virtual Shop (item cards with Buy/Owned state) and Custom Rewards (create/redeem/delete).
  - `CreateRewardDialog` — AlertDialog with title and cost inputs for user-generated rewards.
  - Snackbar notifications for purchase success and error feedback.
  - FAB for creating new custom rewards.
  - Emoji-based item icons and modifier labels (e.g., "+10% XP", "Decor").
- **Clinic Feature (Step 7):**
  - `ClinicViewModel` — Observes user stats and inventory via reactive Flows.
  - `ClinicState` — Player level, XP progress (with next-level calculation), multipliers, equipment/decorations split.
  - Clinic room visual with emoji-based item grid (FlowRow) and empty-state hospital icon.
  - Player level card with animated XP progress bar, level number, title, and level-specific emoji.
  - Active multipliers card showing XP and Coin multiplier values.
  - Inventory sections for Equipment and Decorations with item lists.
- **Stats Feature (Step 8):**
  - `StatsViewModel` — Observes user stats and session history via reactive Flows.
  - `StatsState` — Computed summary properties (totalSessions, completedSessions, totalFocusMinutes, totalEarnedXp/Coins).
  - Summary row with 3 cards: total sessions, completed sessions, focus time.
  - Player summary card showing total XP, total Coins, and current level with emoji.
  - Session history list with status emoji, duration, earned XP/Coins per session.
  - Empty state message when no sessions exist.
  - Helper functions: `formatDuration()`, `statusEmoji()`, `statusLabel()`, `levelEmoji()`.
- **Polish (Step 9):**
  - Navigation crossfade animation via Decompose `stackAnimation(fade())`.
  - Animated tab content transitions in ShopScreen (`AnimatedContent` with fade).
  - `animateItem()` on LazyColumn items in ShopScreen and StatsScreen for smooth list animations.
  - Dark mode support with `isSystemInDarkTheme()` — complementary dark palette (Teal 200/Blue 200 tones).
  - `isProcessing` guard in ShopViewModel to prevent double-tap purchases.
  - Error feedback in FocusViewModel when session start fails (Snackbar via `errorMessage` state).
  - Input validation in ShopViewModel's `createReward()` (blank title, zero/negative cost).
  - Loading state in StatsScreen with `CircularProgressIndicator` while session history loads.
- **Domain Unit Tests:**
  - Fake repository implementations (`FakeRepositories.kt`) for all 5 repository ports using `MutableStateFlow`.
  - Value object tests: `CoinTest` (7), `ExperiencePointsTest` (5), `FocusDurationTest` (7), `MultiplierTest` (6).
  - Business rule tests: `PlayerLevelTest` (9), `RewardCalculatorTest` (9), `MultiplierCalculatorTest` (8).
  - Use case tests: `StartFocusSessionUseCaseTest` (7), `CompleteFocusSessionUseCaseTest` (8), `InterruptFocusSessionUseCaseTest` (8), `PurchaseShopItemUseCaseTest` (7), `PurchaseCustomRewardUseCaseTest` (4), `SaveCustomRewardUseCaseTest` (7), `DeactivateCustomRewardUseCaseTest` (2), `GetUserStatsUseCaseTest` (4).
  - 15 test suites, 90+ test cases covering value objects, business rules, and all use cases.
- Architecture Design Document (`ADD.md`) — full project specification.
- Engineering standards (`CLAUDE.md`) — SOLID, design patterns, DRY/YAGNI/KISS rules, naming conventions, git workflow.

### Fixed
- **`@JvmInline` KMP compatibility** — Used fully qualified `@kotlin.jvm.JvmInline` annotation on value objects so they compile on both JVM (Android) and Native (iOS) targets.
- **`Clock.System` migration** — Switched from `kotlinx.datetime.Clock` to `kotlin.time.Clock` due to kotlinx-datetime 0.7.x API change (transitive dependency bump).

### Decisions Made
- **MVI** over MVVM — unidirectional data flow fits session state machine.
- **Decompose** over Voyager — Voyager is no longer maintained.
- **SQLDelight** over Room — Room KMP is experimental, SQLDelight is stable.
- **Fully offline** — no cloud sync, no remote backend.
- **Reduced partial rewards** on interruption — `(actual/planned) * base * 0.5`.
- **3.0x multiplier cap** — additive stacking, capped to prevent inflation.
- **5-minute minimum** for reward eligibility.
- **JDK 23** for Gradle builds (JDK 25 default incompatible with Gradle 8.11.1).
