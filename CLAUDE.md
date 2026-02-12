# Focus Clinic - Project Engineering Standards

## Non-Negotiable Principles

These rules apply to EVERY file, class, function, and decision in this project. No exceptions.

### SOLID Principles
- **Single Responsibility:** One class = one reason to change. ViewModels don't do data access. Use cases don't format UI strings.
- **Open-Closed:** Extend behavior via interfaces and new implementations, never by modifying existing classes. Use strategy/factory patterns instead of adding branches.
- **Liskov Substitution:** Any implementation of a port/interface must be fully substitutable without the caller knowing.
- **Interface Segregation:** Small, focused interfaces. A repository should NOT have a god-interface with 20 methods. Split by concern.
- **Dependency Inversion:** Domain depends on NOTHING. Data and Presentation depend on Domain abstractions (ports), never on concrete implementations.

### Design Patterns (Apply Where They Fit)
- **Strategy Pattern:** For interchangeable reward calculation, multiplier computation, session completion policies. NEVER use `when`/`if` chains to pick behavior — inject a strategy.
- **Factory Pattern:** For creating domain entities with validation (e.g., `FocusSession.create()`). No raw constructor calls with unvalidated data.
- **Builder Pattern:** For complex object construction where multiple optional parameters exist.
- **Composite Pattern:** For multiplier stacking (list of modifiers composed into a single multiplier).
- **Adapter Pattern:** For all `expect/actual` platform implementations and DTO-to-domain mapping.
- **State Pattern:** For the Focus Engine state machine (Idle, Focusing, Completed, Interrupted, Cancelled). Each state is an object with its own behavior, NOT a `when(state)` block in the ViewModel.

### DRY - Don't Repeat Yourself
- If the same logic appears in 2+ places, extract it immediately.
- Mapping logic belongs in dedicated mapper functions/extensions, not duplicated across repositories.
- UI patterns (coin display, timer format, status badges) are reusable composables, not copy-pasted across screens.

### YAGNI - You Aren't Gonna Need It
- Do NOT build features, abstractions, or infrastructure for hypothetical future use.
- No cloud sync design. No multi-user support. No analytics framework. No feature flags.
- If it's not in the ADD, don't build it.

### KISS - Keep It Simple
- Prefer simple, readable code over clever solutions.
- No over-abstraction. Three similar lines are better than a premature abstraction.
- If a pattern adds complexity without clear benefit for the current scope, skip it.

## Code Quality Rules

### No Hardcoded Strings
- **UI text:** All user-facing strings go in a centralized `Strings` object or resource system. No inline `"Start Focus"` in composables.
- **Database values:** Enum-like constants (status codes, item types) are defined as domain constants or sealed classes, never raw strings.
- **Keys and identifiers:** Navigation routes, Koin qualifier names, preference keys — all defined as named constants.
- **Numbers:** No magic numbers. `5` minutes minimum → `const val MIN_REWARD_DURATION_MINUTES = 5`. `0.5` penalty → `const val INTERRUPTION_PENALTY_MULTIPLIER = 0.5`.

### No Repeated Conditional Logic
- **Ban `when`/`if` chains that switch on type or enum in multiple places.** This is a code smell — use polymorphism instead.
- Session status behavior → State Pattern (each status knows how to calculate its own rewards).
- Item type behavior → Strategy/polymorphism (each item type knows its own effect).
- Transaction type handling → sealed class with behavior methods, not external `when` blocks.
- **One `when` is acceptable** at a single mapping boundary (e.g., deserializing a DB string to a sealed class). Two `when` blocks on the same type = refactor immediately.

### Best Practices
- **Immutability by default:** All state classes, value objects, and DTOs are immutable (`val`, not `var`). Use `copy()` for mutations.
- **Null safety:** Avoid nullable types where possible. Use sealed results (`Success`/`Error`) instead of returning null.
- **Value Objects for domain concepts:** `Coin(amount)` not `Int`, `ExperiencePoints(value)` not `Long`. They self-validate on construction.
- **Sealed hierarchies for errors:** Domain errors are sealed classes, not exceptions or string messages.
- **Extension functions for mapping:** `FocusSessionDto.toDomain()` not `FocusSessionMapper.map(dto)`.

## UI/UX Standards
- **Composable functions are dumb:** They receive state and emit events. Zero business logic in composables.
- **Preview-friendly:** Every screen composable should work with `@Preview` using sample state.
- **Consistent spacing/padding:** Use theme-defined dimensions, not hardcoded `dp` values.
- **Responsive feedback:** Every user action gets immediate visual feedback (button state change, loading indicator, animation).
- **Error states:** Every screen handles empty state, loading state, and error state. No blank screens.
- **Accessibility:** Content descriptions on all icons and interactive elements.

## Architecture Enforcement
- `:core:domain` has ZERO dependencies on `:core:data` or `:composeApp`. This is enforced by Gradle module structure.
- Use cases are the ONLY entry point to domain logic from the presentation layer.
- ViewModels NEVER access repositories directly — always through use cases.
- Platform-specific code lives ONLY in `expect/actual` declarations inside `:composeApp`.

## Naming Conventions
| Element | Convention | Example |
|---------|-----------|---------|
| Entities | PascalCase | `FocusSession` |
| Value Objects | PascalCase | `Coin`, `ExperiencePoints` |
| Use Cases | `{Action}UseCase` | `CompleteFocusSessionUseCase` |
| Repository Ports | `{Domain}Repository` | `FocusSessionRepository` |
| Repository Impls | `SqlDelight{Domain}Repository` | `SqlDelightFocusSessionRepository` |
| ViewModels | `{Screen}ViewModel` | `FocusViewModel` |
| State classes | `{Screen}State` | `FocusState` |
| Intent classes | `{Screen}Intent` | `FocusIntent` |
| Composables | `{Feature}Screen`, `{Component}` | `FocusScreen`, `CoinDisplay` |
| Constants | UPPER_SNAKE_CASE | `MAX_MULTIPLIER_CAP` |
| Mappers | `{Type}.toDomain()`, `{Type}.toDto()` | `FocusSessionEntity.toDomain()` |

## Documentation & Git Workflow

### Living Documents — Keep Updated
These files MUST be updated whenever a significant change happens (new feature, architecture change, major fix, new module, dependency change):

- **`CLAUDE.md`** — Update when new engineering rules, patterns, conventions, or project-level decisions are made.
- **`CHANGELOG.md`** — Log every meaningful change with date, category, and description. Follow [Keep a Changelog](https://keepachangelog.com/) format: `Added`, `Changed`, `Fixed`, `Removed`.
- **`README.md`** — Update when project setup steps change, new modules are added, or the tech stack evolves.
- **`ADD.md`** — Update when architectural decisions are revised or new features are scoped.

### Git Discipline
- **Commit after each meaningful milestone:** completed feature, module setup, major refactor, bug fix. Don't accumulate a huge diff.
- **Commit message format:** Concise, imperative mood. Prefix with scope: `feat(domain):`, `fix(data):`, `refactor(ui):`, `docs:`, `chore:`.
- **Push when:** a feature/module is complete and stable, or at the end of a working session. Don't leave unpushed work.
- **Never commit:** broken code, hardcoded secrets, generated files that should be in `.gitignore`.
- **Branch strategy:** Work on `main` for now (solo project). Create feature branches if parallel work is needed later.
