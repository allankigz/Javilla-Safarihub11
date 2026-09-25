# Javilla Safari Hub — Production Improvements

## Release 1.2 (versionCode 3)

### Security
- Added Firebase App Check bootstrap: Play Integrity for release builds and Debug App Check for development.
- Tightened Realtime Database writes so normal users can create pending reviews/reports but cannot edit moderation fields or aggregate counters.
- Added one-user-one-vote records for review helpful votes, scam confirmations and safety confirmations.
- Added admin-only remote tourism content writes.
- Hardened booking creation/moderation boundaries.

### Reliability
- Enabled Firebase Realtime Database offline persistence.
- Added Crashlytics for release monitoring.
- Added backend Cloud Functions that rebuild aggregate counters idempotently.
- Added FCM safety-alert and booking-status server notifications.
- Added local-content fallback when remote tourism content is unavailable.

### Content management
- Destinations can now be overridden from `content/destinations` without an APK release.
- Activities can now be overridden from `content/activities` without an APK release.
- The local catalogue remains a fallback if Firebase has no active remote records.

### Validation performed in this environment
- Firebase rules JSON parses successfully.
- Android XML resources parse successfully.
- Kotlin source was passed through the Kotlin compiler frontend; no syntax/parse errors were reported.
- The Gradle test task could not run because the wrapper distribution could not be downloaded from `services.gradle.org` in this environment.
- Cloud Functions TypeScript was parsed by TypeScript; dependency resolution could not complete because npm dependency installation timed out in this environment.
