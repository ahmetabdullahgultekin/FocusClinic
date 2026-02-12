# Changelog

All notable changes to the Focus Clinic project will be documented in this file.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Added
- Architecture Design Document (`ADD.md`) — full project specification with tech stack, data model, business rules, and implementation plan.
- Engineering standards (`CLAUDE.md`) — SOLID, design patterns, DRY/YAGNI/KISS rules, naming conventions, documentation and git workflow.
- Project changelog (`CHANGELOG.md`).
- Project readme (`README.md`).

### Decisions Made
- **MVI** over MVVM — unidirectional data flow fits session state machine.
- **Decompose** over Voyager — Voyager is no longer maintained.
- **SQLDelight** over Room — Room KMP is experimental, SQLDelight is stable.
- **Fully offline** — no cloud sync, no remote backend.
- **Reduced partial rewards** on interruption — `(actual/planned) * base * 0.5`.
- **3.0x multiplier cap** — additive stacking, capped to prevent inflation.
- **5-minute minimum** for reward eligibility.
