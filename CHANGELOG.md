# Changelog

All notable changes to the Focus Clinic project will be documented in this file.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

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
- Architecture Design Document (`ADD.md`) — full project specification.
- Engineering standards (`CLAUDE.md`) — SOLID, design patterns, DRY/YAGNI/KISS rules, naming conventions, git workflow.

### Decisions Made
- **MVI** over MVVM — unidirectional data flow fits session state machine.
- **Decompose** over Voyager — Voyager is no longer maintained.
- **SQLDelight** over Room — Room KMP is experimental, SQLDelight is stable.
- **Fully offline** — no cloud sync, no remote backend.
- **Reduced partial rewards** on interruption — `(actual/planned) * base * 0.5`.
- **3.0x multiplier cap** — additive stacking, capped to prevent inflation.
- **5-minute minimum** for reward eligibility.
- **JDK 23** for Gradle builds (JDK 25 default incompatible with Gradle 8.11.1).
