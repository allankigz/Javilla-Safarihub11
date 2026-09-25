# Fix 20 — UI/UX Stability, Backgrounds, Theme Contrast and Localization

Implemented without intentionally removing existing app functionality.

## Changes
1. Removed the Home Scaffold bottom-bar auto-hide behavior. The bottom navigation now remains fixed and visible, including at the bottom of long pages.
2. Made the Home Scaffold transparent and placed the content/pattern layer correctly so the Scaffold no longer appears to sit underneath the Home content.
3. Replaced the splash network image with `res/drawable/splash.jpg` (`R.drawable.splash`).
4. Replaced the onboarding network image with `res/drawable/safari.jpg` (`R.drawable.safari`). The supplied `safari2.jpg` asset was retained under the requested drawable resource name by renaming it to `safari.jpg`.
5. Added reusable African savannah-inspired zebra, leopard, giraffe, elephant and contour-line patterns. Patterns are subtle so they do not interfere with controls.
6. Applied different patterns to major screens and to Home's internal feature screens.
7. Fixed dark-mode contrast in Home feature cards and the navigation drawer by using theme-aware foreground colors instead of forcing SavannahGold on all titles.
8. Added persistent language selection using SharedPreferences and lightweight translations for the Home/navigation/settings UI. English remains the safe fallback for text that has no translation yet.
9. Fixed destination-link crashes: destination cards retain their existing website-opening behavior, but URI parsing/activity launch is now guarded and shows a Toast instead of crashing when no suitable browser/link handler is available.
10. Kept existing Firebase, Room, navigation, booking, reviews, safety, services and other app functionality intact.

## Validation
- Kotlin delimiter balance checked for modified key files.
- No Gradle compilation claim is made because this environment may not have network access to download the project's Gradle distribution.
