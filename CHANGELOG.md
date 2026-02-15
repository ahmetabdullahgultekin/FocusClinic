# Changelog

All notable changes to the İrade (Willpower) project will be documented in this file.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Phase 5: Polish, Localization & Missing Features (2026-02-15)

#### Added
- **Goal celebration + haptic feedback**: Confetti overlay and haptic feedback on goal completion (success haptic) and failure (error haptic).
- **Completion note dialog**: Optional note when completing a goal — dialog with text field and Complete/Skip buttons.
- **Interactive calendar**: Tap a day to see goal completions with names, rewards, and notes. Selected day highlighted with border.
- **Focus screen active goals widget**: Compact card below Start Focus button showing up to 5 active goals with quick-complete checkmarks.
- String resources: `goals_completion_note`, `goals_skip`, `calendar_no_completions`, `focus_active_goals` (Turkish + English).

#### Changed
- **Full localization migration**: Replaced all hardcoded `Strings.kt` constants with `stringResource(Res.string.xxx)` across all screens (Focus, Goals, Shop, Profile, Stats, Calendar, App navigation). Deleted `Strings.kt`.
- **Clinic → Profile rename**: Renamed `ClinicScreen`/`ClinicViewModel`/`ClinicState` to `ProfileScreen`/`ProfileViewModel`/`ProfileState`. Updated navigation, DI, and all references.
- CalendarView months and weekday headers now use localized string resources instead of hardcoded English.
- `modifierLabel()`, `statusLabel()`, `formatDuration()`, `navLabel()`, `monthName()` converted to `@Composable` functions for `stringResource()` access.

#### Removed
- `Strings.kt` — all hardcoded English UI constants replaced by XML string resources.
- `screens/clinic/` package — replaced by `screens/profile/`.

### İrade Transformation (2026-02-14)

**Complete rebrand from "Focus Clinic" (dental theme) to "İrade" (willpower/self-control theme).**

#### Added
- **WillpowerGoal feature**: Full CRUD for personal goals with coin/XP rewards on completion. Domain entities (`WillpowerGoal`, `GoalCompletion`), repository port, 4 use cases, SQLDelight persistence, MVI presentation.
- **Goals screen**: New tab with goal list, create/edit dialogs, complete/delete actions.
- **Calendar heatmap**: Monthly calendar view on Goals screen showing goal completion activity per day with color intensity.
- **CelebrationOverlay**: Confetti particle animation displayed on focus session completion.
- **HapticFeedback**: Platform-specific haptic feedback via `expect/actual`. Android: `Vibrator` API. iOS: `UIImpactFeedbackGenerator`. Triggers on session start (medium), completion (success), interruption (error).
- **Localization resources**: Turkish (`composeResources/values/strings.xml`) as default locale, English (`composeResources/values-en/strings.xml`) as secondary.
- **`GoalError`** sealed interface in `DomainError.kt` for goal-specific errors.
- **`EarnGoal`** transaction type for goal completion rewards.
- **`roadmap.md`**: Project milestones tracking document.

#### Changed
- **App name**: "Focus Clinic" → "İrade"
- **Theme**: `FocusClinicTheme` → `IradeTheme`. Colors: Deep Indigo (#3F51B5) + Warm Gold (#FFC107) + Soft Purple (#7E57C2).
- **PlayerLevel enum**: Intern → Beginner, Assistant → Apprentice, Resident → Determined, Specialist → Strong, Associate Professor → Master, Professor → Legend.
- **ShopCatalog items**: Ergonomic Chair → Focus Stone, LED Lamp → Perseverance Shield, Sterilizer → Willpower Fire, Digital X-Ray → Patience Medal, Wall Paint → Peace Garden, Diploma Frame → Motivation Wall, Potted Plant → Inspiration Plant, Aquarium → Victory Aquarium.
- **ProfileAttributes** replaces `ClinicAttributes`.
- **Currency**: "Coins" → "Sparks" with sparkles emoji (✨) replacing tooth emoji.
- **Focus screen**: Patient waiting/treating/happy/angry → fire/lightning/star/dash emoji with willpower messages.
- **Shop screen**: "Clinic Shop" → "Power Shop". Updated item emoji to willpower theme.
- **Stats screen**: Updated level emoji (seedling, lightning, biceps instead of medical).
- **Profile screen** (was Clinic): Updated all labels from CLINIC_* to PROFILE_*.
- **Bottom navigation**: Added Goals tab (5 tabs total). Clinic label → "Profile".
- **README.md**: Updated for İrade branding, added features section.

#### Removed
- All dental/clinic/patient/tooth/DUS references from code, strings, and emoji.
- `ClinicAttributes.kt` (replaced by `ProfileAttributes.kt`).

### Completion — All ADD.md Spec Items Resolved (2026-02-13)

**Overall completion: 100% of ADD.md spec.**

All 7 outstanding items from the prior review have been implemented.

#### Platform Reliability (P1) — DONE
- **Android ForegroundService + WakeLock**: `FocusTimerService` keeps timer alive when backgrounded. `TimerNotificationManager.android` acquires partial wake lock and starts foreground service with persistent notification. Service declared in AndroidManifest with `specialUse` foreground service type.
- **Timer completion notifications**: Both platforms now fire notifications. Android: `NotificationCompat` with completion channel. iOS: `UNUserNotificationCenter` immediate and scheduled notifications.
- **iOS notification scheduling**: `TimerNotificationManager.ios` schedules a `UNTimeIntervalNotificationTrigger` at session start time for the expected completion. Cancels on stop/interrupt.

#### Spec Compliance (P2) — DONE
- **`UpgradeClinicUseCase` resolved**: Removed from ADD.md. `PurchaseShopItemUseCase` IS the upgrade mechanism — equipment purchases add multipliers, decorations change visuals. No separate use case needed.
- **Data layer integration tests**: 5 test suites in `:core:data` `androidUnitTest` with SQLDelight `JdbcSqliteDriver.IN_MEMORY`. Covers all repository implementations with ~30 test cases.
- **ViewModel tests**: 2 test suites in `:composeApp` `commonTest`. `FocusViewModelTest` (11 tests) and `ShopViewModelTest` (8 tests) covering critical flows: start session, cancel session, purchase item, create/delete reward, validation.

#### Documentation (P3) — DONE
- **README.md**: Complete build instructions with prerequisites, Android/iOS build commands, and test commands.

#### Architecture Changes
- Extracted `TimerNotification` interface in commonMain for testability. `TimerNotificationManager` (`expect/actual`) implements it. ViewModel depends on the interface, not the concrete class. Follows Dependency Inversion principle.

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
