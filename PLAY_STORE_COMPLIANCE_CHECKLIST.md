# Play Store Compliance Checklist

Last updated: July 2, 2026

This checklist is tailored to the current ClassSync codebase. It is a suggested launch checklist, not legal advice. Review every item again at release time, especially if app behavior changes.

## 1. Must-Have Before Submission

- Add a publicly accessible privacy policy URL in Play Console.
- Keep the same privacy policy reachable from inside the app.
- Complete the Google Play Data safety form.
- Complete the App content declarations.
- Complete the Content rating questionnaire.
- Declare whether the app contains ads.
- Provide App access instructions for reviewers.
- Upload a signed release AAB.
- Test the release build, not only debug.
- Verify that the final policy answers match the shipped build.

## 2. Current Codebase Summary

Based on the current repository:

- Uses Google sign-in.
- Uses Gmail API read-only access when the user enables Gmail sync.
- Uses Google Classroom API read-only access.
- Stores synced tasks, events, and settings locally on-device.
- Exports task data to local CSV and JSON files on user request.
- Uses `POST_NOTIFICATIONS`.
- Does not currently integrate ads SDKs.
- Does not currently integrate payments or subscriptions.
- Does not request contacts, location, microphone, camera, SMS, call log, or photo/media permissions.
- Appears to use no custom backend for synced academic content.

## 3. Privacy Policy Checklist

- Privacy policy exists in Play Console field.
- Privacy policy is reachable on a public non-PDF URL.
- In-app policy screen or link exists.
- Policy names the actual developer or company from the Play listing.
- Policy includes a real monitored contact email.
- Policy explains:
  - Google sign-in
  - Gmail sync
  - Classroom sync
  - local storage
  - exports
  - retention/deletion
  - no sale of personal data

## 4. Suggested App Content Answers

These are suggested answers based on the current codebase and should be rechecked before production release.

### Ads

- Suggested answer: `No`
- Reason: no ads SDK or ad surfaces are visible in the code review.

### App access

- Suggested answer: `Yes, app access instructions are needed`
- Reason: core features depend on Google sign-in and optional Gmail/Classroom authorization.

Suggested reviewer notes:

- Use a test Google account that can access Google Classroom data.
- Gmail sync is optional and can remain disabled during review if needed.
- Classroom sync is read-only.
- No paid features or special hardware are required for review.

### Target audience and content

- Suggested answer: general audience, likely teens and adults depending on your intended distribution.
- Action: choose the real intended audience in Play Console and complete the questionnaire honestly.

### News app

- Suggested answer: `No`

### Health app / financial app / VPN / device admin

- Suggested answer: `No`

## 5. Suggested Data Safety Answers

Google Play wording can change, so treat this as a draft mapping for the current implementation.

### Does the app collect data?

- Suggested answer: `Yes`
- Reason: Gmail/Classroom content, account email, local app state, and exports are handled by the app.

### Is data shared with third parties?

- Suggested answer: likely `No` for third-party sharing outside service providers, but carefully review Play definitions.
- Important nuance: data is transmitted to Google services because the app uses Google sign-in, Gmail API, and Classroom API. In Play terminology, you must distinguish service-provider processing from “sharing.”

### What data types likely apply

- Personal info
  - Email address
  - Suggested answer: `Collected`
- App activity or in-app interactions
  - Settings, sync actions, task state
  - Suggested answer: usually `Collected` if stored/processed as app functionality
- Messages or email content
  - Gmail-derived academic message content
  - Suggested answer: `Collected`
- Files and docs
  - Exported task files and linked material references may need review
  - Suggested answer: review carefully before final submission

### Purpose tags likely to apply

- App functionality
- Account management
- Developer communications
  - only if you actually contact users through the app or account flow

### Is data processed ephemerally?

- Suggested answer: `No` for most synced content
- Reason: tasks, events, and settings are stored locally on-device.

### Is data required or optional?

- Google account / Classroom data: optional from a product perspective, but required for the sync features.
- Gmail data: optional.
- Notifications: optional, but required for reminder delivery.

### Is data encrypted in transit?

- Suggested answer: `Yes`
- Reason: Google API traffic uses HTTPS/TLS.

### Can users request deletion?

- Suggested answer: `Yes` for local app data deletion if your in-app flow exists and works.
- If there is no backend account, explain that deleting local app data, signing out, or uninstalling removes local storage.

## 6. Reviewer-Facing Compliance Notes

- Make sure the app description does not overclaim privacy or ML accuracy.
- Do not claim “100% accurate AI.”
- If the app uses ML classification, position it as on-device assistance with fallback rules.
- If Gmail sync is optional, say that clearly in the store listing and onboarding.

## 7. Code/Policy Gaps Still To Finish

- Replace placeholder public privacy policy URL in the app resources.
- Replace placeholder privacy email with a real monitored address.
- Host the privacy policy on a public URL before submission.
- Reconfirm Data safety answers after any code changes to sync, exports, or ML logging.
- Review release build configuration, minification, and final manifest before upload.

## 8. Final Pre-Upload Release Check

- Privacy policy URL works in a browser.
- In-app privacy policy entry opens correctly.
- Gmail sync can be disabled.
- Classroom sync can be disabled.
- Sign-out works.
- Local data clearing flow works.
- Notification permission handling works.
- No debug-only copy, diagnostics, or internal-only content is visible in release.
