# Implementation Plan - Fix all compilation errors and run the app

This plan addresses all identified compilation errors across multiple files in the project to ensure the application can be built and run.

## Proposed Changes

### 1. Data Layer & ViewModel
#### [MODIFY] [AnalyticsViewModel.kt](file:///C:/Users/satya/AndroidStudioProjects/Attendanceschedulemanager/app/src/main/java/com/example/attendanceschedulemanager/viewmodel/AnalyticsViewModel.kt)
- Fix non-exhaustive `when` expression for `AttendanceStatus.EXCUSED` in `generateShareReport`.

### 2. UI Components
#### [MODIFY] [DesignSystem.kt](file:///C:/Users/satya/AndroidStudioProjects/Attendanceschedulemanager/app/src/main/java/com/example/attendanceschedulemanager/ui/components/DesignSystem.kt)
- Remove the redundant and unused `goal` parameter from `AttendanceRingIndicator`.
- Add `AttendanceStatusType` constants to facilitate cleaner status chip implementation.

#### [MODIFY] [Color.kt](file:///C:/Users/satya/AndroidStudioProjects/Attendanceschedulemanager/app/src/main/java/com/example/attendanceschedulemanager/ui/theme/Color.kt)
- Add missing `LightSurfaceVariant` and `DarkSurfaceVariant` definitions.

#### [MODIFY] [Theme.kt](file:///C:/Users/satya/AndroidStudioProjects/Attendanceschedulemanager/app/src/main/java/com/example/attendanceschedulemanager/ui/theme/Theme.kt)
- (Already mostly correct, but ensure the new colors from `Color.kt` are correctly referenced).

### 3. UI Screens
#### [MODIFY] [AnalyticsScreen.kt](file:///C:/Users/satya/AndroidStudioProjects/Attendanceschedulemanager/app/src/main/java/com/example/attendanceschedulemanager/ui/screens/analytics/AnalyticsScreen.kt)
- (Already partially fixed, but ensure it's fully aligned with the final component signatures).

#### [MODIFY] [TimetableScreen.kt](file:///C:/Users/satya/AndroidStudioProjects/Attendanceschedulemanager/app/src/main/java/com/example/attendanceschedulemanager/ui/screens/timetable/TimetableScreen.kt)
- Fix `AppTopHeader` call by providing missing `screenTitle`, `title`, `subtitle`, and `onNotificationClick`.
- Replace unresolved `primaryFixed` etc. color references with `primaryContainer` etc.

#### [MODIFY] [TodayScreen.kt](file:///C:/Users/satya/AndroidStudioProjects/Attendanceschedulemanager/app/src/main/java/com/example/attendanceschedulemanager/ui/screens/today/TodayScreen.kt)
- Fix `AppTopHeader` call.
- Fix `AttendanceRingIndicator` call by removing `goal` (following the definition change).
- Fix `StatusPillChip` call by providing missing `text` parameter.

#### [MODIFY] [MaterialsScreen.kt](file:///C:/Users/satya/AndroidStudioProjects/Attendanceschedulemanager/app/src/main/java/com/example/attendanceschedulemanager/ui/screens/materials/MaterialsScreen.kt)
- Fix `AppTopHeader` call.
- Fix `horizontalScroll` and `verticalScroll` implementation by using proper Modifier extension calls and adding missing imports.
- Fix `@Composable` invocation outside of a composable context by moving `rememberScrollState()` inside the `Row`.
- Replace unresolved `*Fixed` color references.

## Verification Plan
- Run `gradle_build(app:assembleDebug)` to verify that all compilation errors are resolved.
- Deploy the app to the device using `deploy` and verify the UI loads correctly.
