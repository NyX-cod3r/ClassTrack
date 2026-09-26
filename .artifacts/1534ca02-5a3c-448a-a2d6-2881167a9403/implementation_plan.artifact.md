# Fix Gradle Sync Error: Cannot add extension with name 'kotlin'

The error `Cannot add extension with name 'kotlin', as there is an extension already registered with that name` typically occurs when the Kotlin Gradle plugin is applied multiple times or conflicts with another plugin that also tries to register the `kotlin` extension (like Room or KSP) when they are not managed centrally in the root `build.gradle.kts`.

## Proposed Changes

### Root Project Configuration
#### [MODIFY] [build.gradle.kts](file:///C:/Users/satya/AndroidStudioProjects/Attendanceschedulemanager/build.gradle.kts)
- Add the Room Gradle plugin to the root `plugins` block with `apply false` to ensure its version is managed centrally and it shares the same classloader as other plugins.

### App Module Configuration
#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/satya/AndroidStudioProjects/Attendanceschedulemanager/app/build.gradle.kts)
- Ensure the `plugins` block is correctly ordered.
- Use `id("...")` syntax for the Kotlin plugin to avoid potential version conflicts with `alias()` in subprojects when the plugin is already defined in the root.

## Verification Plan
### Manual Verification
- Run a Gradle sync to verify the error is resolved.
- Verify that Kotlin compilation and Room code generation (via KSP) still work.
