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

### Phase 4: İrade Transformation (Completed — Current Release)
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

## Upcoming

### Phase 5: Polish & Quality
- Migrate composables from `Strings.kt` constants to `stringResource(Res.string.xxx)`
- Add loading/error/empty states to all screens consistently
- Improve animations and transitions
- Accessibility improvements (content descriptions, touch targets)

### Phase 6: Advanced Goals
- Recurring goals (daily, weekly)
- Goal categories and filtering
- Goal completion streaks with bonus multipliers
- Calendar day detail view (click day to see completions)

### Phase 7: Enhanced UX
- Onboarding flow for new users
- Settings screen (notification preferences, theme selection)
- Data export (JSON/CSV)
- Widget support (Android home screen widget for active goals)
