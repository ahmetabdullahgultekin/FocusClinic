# İrade (Willpower) — Project Roadmap

## Completed

### Phase 1: Foundation (Completed)
- Gradle multi-module KMP project setup (`:core:domain`, `:core:data`, `:composeApp`)
- Tech stack: Kotlin 2.1.20, Compose Multiplatform 1.10.0, SQLDelight 2.2.1, Koin 4.1.1, Decompose 3.4.0
- Clean Architecture with MVI pattern
- Domain layer: entities, value objects, repository ports, use cases, business rules
- Data layer: SQLDelight schema, repository implementations, mappers
- Presentation layer: Decompose navigation, Koin DI, Material 3 theme

### Phase 2: Core Features (Completed)
- Focus Engine: timer with state machine (Idle → Focusing → Completed/Interrupted)
- Shop: virtual items with multiplier effects + user-created real-life rewards (CRUD)
- Profile (was Clinic): player level, XP progress, inventory visualization
- Stats: session history, summary cards, player progress
- 90+ domain unit tests, data integration tests, ViewModel tests

### Phase 3: Platform Reliability (Completed)
- Android ForegroundService + WakeLock for timer persistence
- Timer notifications on both platforms (Android NotificationCompat, iOS UNUserNotificationCenter)
- iOS background notification scheduling
- `expect/actual` lifecycle observer for app backgrounding detection

### Phase 4: İrade Transformation (Completed)
- **Domain Rebrand:** Removed all dental/clinic/patient references. New willpower theme:
  - PlayerLevel: Beginner → Apprentice → Determined → Strong → Master → Legend
  - ShopCatalog: Focus Stone, Perseverance Shield, Willpower Fire, Patience Medal, etc.
  - ProfileAttributes replaces ClinicAttributes
- **WillpowerGoal Feature:** Full domain + data + presentation
  - Create, update, complete, deactivate goals
  - Goal completions earn coins and XP
  - 4 use cases with tests, SQLDelight persistence, MVI screen
- **Calendar Heatmap:** Monthly view with completion count coloring on Goals screen
- **UI Rebrand:**
  - IradeTheme: Deep Indigo (#3F51B5) + Warm Gold (#FFC107) + Soft Purple (#7E57C2)
  - All screens updated with willpower-themed emoji and strings
  - Sparkles (✨) replace tooth emoji for currency
  - Fire/lightning/star emoji for focus states
- **Localization:** Turkish (default) + English string resources in composeResources
- **Celebrations:** Confetti overlay with particle animation on session completion
- **Haptic Feedback:** Platform-specific (Android Vibrator, iOS UIImpactFeedbackGenerator)
  - Medium haptic on session start, success/error haptic on result

### Phase 5: Polish, Localization & Missing Features (Completed — Current Release)
- **Localization migration**: Replaced all `Strings.kt` hardcoded constants with `stringResource(Res.string.xxx)`. Deleted `Strings.kt`. All screens fully localized (Turkish default, English secondary).
- **Clinic → Profile rename**: Rebranding cleanup — renamed all Clinic* files/classes to Profile*.
- **Goal celebration + haptic feedback**: Confetti overlay + haptic feedback on goal completion.
- **Completion note dialog**: Optional note when completing goals (Complete/Skip).
- **Interactive calendar**: Tap a day to see completions with goal names, rewards, and notes.
- **Focus screen active goals widget**: Quick-complete up to 5 goals directly from the Focus screen.
- Localized calendar months and weekday headers via string resources.

### Phase 6: Advanced Goals (Completed)
- **Recurring goals**: Daily and Weekly recurrence types with `IsGoalCompletableUseCase` enforcing one completion per period
- **Goal categories**: 6 predefined categories (Health, Productivity, Learning, Fitness, Habits, Other) with horizontal FilterChip filtering
- **Streak system**: Consecutive-day tracking with `CalculateStreakUseCase`, StreakCard UI (current/best streak), tier-based multipliers (3d→1.1x, 7d→1.25x, 14d→1.5x, 30d→2.0x) applied to completion rewards
- Domain: `RecurrenceType`, `GoalCategory`, `StreakInfo`, `StreakRules`, `IsGoalCompletableUseCase`, `CalculateStreakUseCase`
- Data: SQLDelight migration v1→v2 (new columns on `willpower_goals` and `user_profile`)

### Phase 7: Enhanced UX (Completed)
- **Onboarding flow**: 3-page HorizontalPager (Welcome, Focus, Goals & Rewards) with page indicators, shown once on first launch
- **Settings screen**: Notification toggle, theme selector (System/Light/Dark), data export button, about section. MVI architecture.
- **`SettingsRepository`** + `SqlDelightSettingsRepository` for persisting onboarding, theme, notification preferences
- **Navigation updates**: `Screen.Onboarding` and `Screen.Settings` routes, bottom nav hidden during onboarding/settings
- **Profile gear icon**: Quick access to Settings from Profile screen
- String resources for all new UI (Turkish + English)

## Upcoming

### Phase 8: Refinement & Platform Features
- Widget support (Android home screen widget for active goals)
- Data export implementation (JSON/CSV file sharing)
- Accessibility audit (content descriptions, 48dp touch targets, semantics)
- Loading/error/empty states audit across all screens
- Performance profiling and optimization
