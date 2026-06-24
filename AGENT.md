# ClassSync Agent Guide

## Purpose

ClassSync is an Android app for students who want one place to collect Classroom coursework, optional Gmail reminders, manually managed tasks, planner views, exam prep, reminders, and a homescreen widget.

This file replaces the old milestone output dumps with stable repo guidance for future contributors and agents.

## Product Snapshot

- Platform: Android, Kotlin, Jetpack Compose
- Package: `com.rochiee.classsync`
- Core surfaces: onboarding, home, tasks, classroom, planner, study planner, exam mode, activity, settings, debug
- Data sources:
  - Google Classroom API
  - Optional Gmail sync
  - Android notification listener events
  - Manual task input
- Local-first behavior:
  - Room for persisted academic data
  - DataStore for preferences
  - WorkManager for background sync and widget refresh
  - Encrypted shared preferences for Google account identity metadata

## Current Architecture

- App entry: `app/src/main/java/com/rochiee/classsync/MainActivity.kt`
- Dependency graph: `app/src/main/java/com/rochiee/classsync/di/AppContainer.kt`
- Navigation: `app/src/main/java/com/rochiee/classsync/ui/navigation`
- State management:
  - `bloc/*` view models expose screen state and events
- Persistence:
  - `data/local/database`
  - `data/local/dao`
  - `data/local/entity`
  - `data/local/preferences`
- Remote integrations:
  - `data/remote/classroom`
  - `data/remote/gmail`
  - `auth/*`
- Domain logic:
  - `domain/usecase/*`
  - `planner/*`
  - `exam/*`
  - `eventengine/*`
  - `taskengine/*`
- Background work:
  - `worker/*`
  - `widget/*`
  - `reminder/*`
  - `digest/*`

## Working Rules

- Keep Google secrets local only.
  - The repo should never include client secret JSON files, refresh tokens, or local property secrets.
  - OAuth setup instructions live in `docs/GOOGLE_SETUP.md`.
- Prefer local-first changes.
  - Sync should enrich local data, not require a remote backend.
- Preserve the design language already introduced in the app.
  - Full-screen onboarding art
  - translucent controls
  - custom raster icons and launcher branding
- If you touch sync or auth, verify both:
  - onboarding flow
  - settings/auth screen flow
- If you touch widget data or task completion logic, verify widget refresh behavior too.

## Google Auth Notes

- `BuildConfig.GOOGLE_WEB_CLIENT_ID` is generated at build time from local configuration.
- Current sign-in flow is interactive and routed through Play Services sign-in result handling.
- Gmail and Classroom sync depend on a successful Google account selection plus correct OAuth project configuration.
- When sign-in fails, prefer explicit user-facing errors over silent fallback states.

## Validation Checklist

- Run `./gradlew assembleDebug`
- If auth changed:
  - test Google sign-in from onboarding
  - test Google sign-in from auth screen
  - test `Sync Classroom`
  - test `Sync Gmail`
- If planner/task logic changed:
  - verify home summary
  - verify task list state changes
  - verify widget updates
- If notification/reminder logic changed:
  - verify permission handling
  - verify reminder receiver behavior

## Documentation Standard

- Do not add milestone dump files again.
- Prefer updating:
  - `README.md` for product/setup/architecture
  - `AGENT.md` for contributor guidance
  - `docs/GOOGLE_SETUP.md` for OAuth-specific setup
  - focused docs under `docs/` when a subsystem needs deep instructions
