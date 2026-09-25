# Javilla Safari Hub — Production Hardening

## Included

- Firebase App Check: Play Integrity in release builds and Debug App Check in debug builds.
- Firebase Crashlytics for release crash/error monitoring.
- Realtime Database offline persistence.
- Idempotent review helpful votes, scam confirmations and safety confirmations.
- Firebase Cloud Functions 2nd gen backend for aggregate counters.
- Approved safety alerts delivered through the `safety_alerts` FCM topic.
- Booking status notifications to the traveller's registered FCM tokens.
- Cloud-managed destinations and activities under `content/destinations` and `content/activities`, with local catalogue fallback.
- Admin-only writes for tourism content.
- Hardened booking, expense and notification-token validation.

## Before production release

1. In Firebase Console > App Check, register the Android app using the release SHA-256 certificate fingerprint and enable Play Integrity. Monitor metrics before enabling enforcement.
2. Register the debug App Check token when developing locally/emulating. Never commit a debug token to source control.
3. In Firebase Console > Crashlytics, enable Crashlytics and verify the first release reports an intentional test crash from a non-production build only.
4. Ensure the Firebase project uses the Blaze plan before deploying Cloud Functions.
5. From the project root, install the Firebase CLI and deploy:
   - `firebase login`
   - `firebase use javilla-safarihub`
   - `firebase deploy --only database,functions`
6. Seed approved tourism content under:
   - `content/destinations/{destinationId}`
   - `content/activities/{activityId}`
7. Create the admin UID entry under `admins/{uid} = true` using a trusted administrative process.
8. Verify FCM topic delivery and booking status notifications with a release-like build.
9. Enable Realtime Database App Check enforcement only after confirming legitimate app requests are receiving valid App Check tokens.

## Cloud Function responsibilities

- `syncReviewHelpfulCount`
- `syncScamConfirmationCount`
- `syncSafetyConfirmationCount`
- `notifyApprovedSafetyIncident`
- `notifyBookingStatus`

The Android client no longer directly increments aggregate counters. Each user can create at most one confirmation/helpful vote per record, while Cloud Functions rebuild the aggregate count from the unique per-user set. This makes retries idempotent.
