# NC Music Player – Android APK

- Android 8.0+ (API 26), optimized for Android phones and tablets, portrait + landscape.
- WebViewAssetLoader bundles the live web player's HTML as local offline assets.
- Includes local Font Awesome so transport controls work without network.
- The file upload control uses Android's Storage Access Framework to select multiple audio files; the existing IndexedDB library stores the selected files inside **this Android app**.
- Audio transitions, fade/crossfade, volume, rename, reorder and trim logic come from the same HTML codebase.
- The web version's library is **not** automatically transferred to Android. Import the songs again using **+**.
- The app keeps the screen on while it is open so it can be used for music events.
- It does not require broad storage permissions. Removing the app removes its local music data.

## Build

The workflow `.github/workflows/build-android.yml` bundles the source, builds with Android Gradle Plugin and publishes `NC-Music-Player-v1.0.0.apk`.

The first build is an Android **debug-signed** APK intended for direct personal installation, not for Google Play. Debug signing keys are runner-generated and may change between CI runs; **do not rely on debug builds for in-place upgrades**. For future distribution, configure a private, persistent release signing key in GitHub Actions.

## APK

GitHub Release: `android-v1.0.0` on `NMCDesignapp/Player`.

For installation, download the APK on Android and permit installation from the browser/file manager if Android prompts. Keep the APK in a trusted location.
