# Privacy Policy

Last updated: July 2, 2026

This Privacy Policy explains how ClassSync accesses, stores, uses, and handles information when you use the ClassSync Android application.

Before publishing, replace the publisher contact details and public policy URL placeholders below with your real production values.

## 1. Publisher

- App name: `ClassSync`
- Publisher: `[Replace with your legal developer or company name]`
- Privacy contact: `privacy@classsync.app`
- Public policy URL: `https://classsync.app/privacy`

## 2. What ClassSync Does

ClassSync is a local-first academic organization app for Android. It helps users combine:

- Google Classroom coursework, announcements, and materials
- optional Gmail-based academic reminder discovery
- manual tasks entered by the user
- reminders, planner views, widgets, exam planning, and study planning

ClassSync does not currently use a custom backend to centrally store synced academic content.

## 3. Information We Access

Depending on which features you enable, ClassSync may access:

- Google account email address and basic sign-in session details
- Google Classroom read-only academic data
  - courses
  - coursework
  - announcements
  - coursework materials
- Optional Gmail read-only message content that matches academic sync rules
- manually entered tasks and planner data
- local app preferences such as theme, reminders, sync settings, digest settings, and onboarding state
- generated local exports such as CSV and JSON task files when triggered by the user

## 4. How We Use Information

ClassSync uses this information to:

- sign you in with Google
- sync Classroom academic items into local events and tasks
- optionally detect academic reminders or updates from Gmail
- generate planner, study, exam, widget, digest, and reminder views
- support local ML-assisted and rule-based event classification
- save your app configuration and local academic workspace state

We use Gmail and Classroom data only to provide the academic organization functionality that the user enables.

## 5. Optional vs Required Data Access

- Google Classroom sync is optional app functionality, but it is a primary feature when enabled.
- Gmail sync is optional and can be turned off independently.
- Notifications are optional, but disabling them prevents reminders and digest alerts from being delivered.

If you do not enable a feature, ClassSync should not use the related data source for that feature.

## 6. Data Sharing

ClassSync does not sell personal data.

ClassSync does not operate a separate app backend that stores synced academic content for users.

Data may be transmitted to:

- Google sign-in services for authentication
- Google Classroom API for read-only academic sync
- Gmail API for optional read-only academic reminder discovery

If you export tasks, the resulting file is created on your device for your own use and may be shared by you using device-level sharing tools.

## 7. Local Storage

ClassSync stores app data locally on the device, including:

- synced tasks
- synced academic events
- reminder and digest preferences
- planner and exam-related state
- theme and onboarding settings

Google sign-in session details stored by the app are protected locally using encrypted preferences where supported by the device and runtime.

## 8. Security

We aim to protect personal and sensitive data by:

- using HTTPS/TLS when communicating with Google services
- storing sign-in session details in encrypted local preferences where supported
- keeping app data local-first by default
- not intentionally exposing private academic content through public endpoints

No system can be guaranteed perfectly secure, but ClassSync is designed to minimize unnecessary collection and sharing.

## 9. Permissions

ClassSync currently uses the following Android permissions:

- `INTERNET`
  - required to contact Google sign-in, Gmail, and Classroom services
- `ACCESS_NETWORK_STATE`
  - used to manage sync behavior based on connectivity
- `POST_NOTIFICATIONS`
  - used for reminders, digest notifications, and due-soon alerts

ClassSync does not currently request contacts, location, microphone, camera, SMS, call log, photo library, or background location permissions.

## 10. Machine Learning Features

ClassSync may use an on-device TensorFlow Lite model to help classify academic events into categories such as:

- task required
- due date task
- announcement only
- material only
- exam or test information
- submission instruction
- grade or feedback

This classification is performed locally within the app runtime. The model is used together with deterministic rule-based logic and fallback policies.

## 11. Data Retention

ClassSync retains locally stored data on the device until:

- you delete specific tasks or local academic data
- you sign out and clear local academic state
- you clear app data
- you uninstall the app

If publication later introduces a backend or support workflow that stores user data outside the device, this section must be updated before release.

## 12. Your Choices

You can:

- sign out of Google
- disable Gmail sync
- disable Classroom sync
- disable background sync
- disable notifications
- delete local academic data through app flows when available
- uninstall the app to remove local app storage from the device

## 13. Children

ClassSync is intended as a general student productivity app and is not designed as a child-directed service under Google Play Families policies unless explicitly declared otherwise in the Play Console.

## 14. Changes to This Policy

We may update this Privacy Policy as the app changes. If publishing changes introduce new data handling, permissions, or sharing behavior, this policy must be updated before those changes are released.

## 15. Contact

For privacy questions, requests, or support:

- Email: `privacy@classsync.app`

Before release, replace this contact with your real monitored support or privacy address.
