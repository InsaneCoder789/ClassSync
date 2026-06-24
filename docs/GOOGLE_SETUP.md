# Google Setup

ClassSync reads the Google web client ID from local developer configuration so it does not need to live in versioned resources.

## OAuth client setup

1. Create or reuse a Google Cloud project.
2. Configure the OAuth consent screen.
3. Add test users during development.
4. Enable these APIs:
   - Google Classroom API
   - Gmail API
5. Create an Android OAuth client for the app package `com.rochiee.classsync`.
6. Create a web client for Google Identity.
7. Add the web client ID to your local machine only, using one of these:
   - `local.properties`: `CLASSSYNC_GOOGLE_WEB_CLIENT_ID=...apps.googleusercontent.com`
   - shell environment: `export CLASSSYNC_GOOGLE_WEB_CLIENT_ID=...apps.googleusercontent.com`
   - Gradle property: `CLASSSYNC_GOOGLE_WEB_CLIENT_ID=...apps.googleusercontent.com`

## SHA fingerprints

Use these commands from the project machine:

```bash
./gradlew signingReport
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Collect both SHA-1 and SHA-256 fingerprints and add them to the Android OAuth client.

## Consent screen notes

- Explain that Classroom is the primary academic source.
- Explain that Gmail sync is optional.
- Explain that ClassSync stores synced data locally on device.

## Scopes

- Classroom read scopes: for courses, coursework, and future announcements/materials coverage.
- Gmail read/query scopes: only for optional academic reminder discovery.
- Announcement scope: `classroom.announcements.readonly`
- Coursework material scope: `classroom.courseworkmaterials.readonly`
- Coursework scope: `classroom.coursework.me.readonly`
- Courses scope: `classroom.courses.readonly`
- Gmail scope: `gmail.readonly`

Keep scope usage minimal and avoid unnecessary inbox access.

## App wiring notes

- `BuildConfig.GOOGLE_WEB_CLIENT_ID` is generated from local configuration at build time.
- The web client ID is not a secret, but keeping it out of committed resources prevents accidental repo leakage and simplifies per-developer setup.
- ClassSync stores only the selected Google account identity on-device, using encrypted local storage when available.
- Handle missing OAuth setup gracefully in debug/testing builds.
