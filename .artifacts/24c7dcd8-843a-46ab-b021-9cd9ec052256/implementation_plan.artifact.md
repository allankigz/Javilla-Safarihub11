# Implementation Plan - Fix Background Images and Visibility

Improve the background images for the Splash and Onboarding screens by using more iconic photography and adjusting overlays for better visibility.

## User Review Required

> [!IMPORTANT]
> - The Splash screen background will be updated to an iconic savannah landscape.
> - Image visibility will be improved by removing the direct alpha on the images and using lighter, more balanced gradient overlays.

## Proposed Changes

### [Visual Enhancements]

#### [MODIFY] [SplashScreen.kt](file:///C:/Users/Administrator/Documents/javilla%20safarihub6/app/src/main/java/com/kigz/javillasafarihub/splash/SplashScreen.kt)
- Update the background image URL to an iconic Kenyan savannah landscape (Acacia tree).
- Set `alpha` to `1.0f` (full opacity) on the image.
- Lighten the black gradient overlay (`0.3f` to `0.5f`) to ensure the image is clearly visible while text remains readable.

#### [MODIFY] [OnboardingScreen.kt](file:///C:/Users/Administrator/Documents/javilla%20safarihub6/app/src/main/java/com/kigz/javillasafarihub/onboarding/OnboardingScreen.kt)
- Set `alpha` to `1.0f` on the giraffe pattern background.
- Adjust the gradient overlay to be less aggressive, allowing the pattern to be seen more clearly.

## Verification Plan

### Manual Verification
- Launch the app and verify the Splash screen background looks like a majestic savannah.
- Verify the image is bright and clear, not hidden by a dark overlay.
- Proceed to Onboarding and verify the giraffe pattern is distinct and attractive.
