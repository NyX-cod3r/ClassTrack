---
name: Academic Attendance & Timetable M3
colors:
  surface: '#fbf8fe'
  surface-dim: '#dcd9de'
  surface-bright: '#fbf8fe'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f2f8'
  surface-container: '#f0edf2'
  surface-container-high: '#eae7ed'
  surface-container-highest: '#e4e1e7'
  on-surface: '#1b1b1f'
  on-surface-variant: '#454652'
  inverse-surface: '#303034'
  inverse-on-surface: '#f3f0f5'
  outline: '#757684'
  outline-variant: '#c5c5d4'
  surface-tint: '#4355b9'
  primary: '#24389c'
  on-primary: '#ffffff'
  primary-container: '#3f51b5'
  on-primary-container: '#cacfff'
  inverse-primary: '#bac3ff'
  secondary: '#5b5d72'
  on-secondary: '#ffffff'
  secondary-container: '#dddef7'
  on-secondary-container: '#5f6176'
  tertiary: '#004a53'
  on-tertiary: '#ffffff'
  tertiary-container: '#006470'
  on-tertiary-container: '#91deec'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dee0ff'
  primary-fixed-dim: '#bac3ff'
  on-primary-fixed: '#00105c'
  on-primary-fixed-variant: '#293ca0'
  secondary-fixed: '#e0e1fa'
  secondary-fixed-dim: '#c4c5dd'
  on-secondary-fixed: '#181a2c'
  on-secondary-fixed-variant: '#434559'
  tertiary-fixed: '#a2effd'
  tertiary-fixed-dim: '#85d2e0'
  on-tertiary-fixed: '#001f24'
  on-tertiary-fixed-variant: '#004f58'
  background: '#fbf8fe'
  on-background: '#1b1b1f'
  surface-variant: '#e4e1e7'
typography:
  display-lg:
    fontFamily: Roboto Flex
    fontSize: 57px
    fontWeight: '400'
    lineHeight: 64px
    letterSpacing: -0.25px
  display-md:
    fontFamily: Roboto Flex
    fontSize: 45px
    fontWeight: '400'
    lineHeight: 52px
    letterSpacing: 0px
  display-sm:
    fontFamily: Roboto Flex
    fontSize: 36px
    fontWeight: '400'
    lineHeight: 44px
    letterSpacing: 0px
  headline-lg:
    fontFamily: Roboto Flex
    fontSize: 32px
    fontWeight: '400'
    lineHeight: 40px
    letterSpacing: 0px
  headline-md:
    fontFamily: Roboto Flex
    fontSize: 28px
    fontWeight: '400'
    lineHeight: 36px
    letterSpacing: 0px
  headline-sm:
    fontFamily: Roboto Flex
    fontSize: 24px
    fontWeight: '400'
    lineHeight: 32px
    letterSpacing: 0px
  title-lg:
    fontFamily: Roboto Flex
    fontSize: 22px
    fontWeight: '500'
    lineHeight: 28px
    letterSpacing: 0px
  title-md:
    fontFamily: Roboto Flex
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
    letterSpacing: 0.15px
  title-sm:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.1px
  body-lg:
    fontFamily: Roboto Flex
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.5px
  body-md:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.25px
  body-sm:
    fontFamily: Roboto Flex
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0.4px
  label-lg:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-md:
    fontFamily: Roboto Flex
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
  label-sm:
    fontFamily: Roboto Flex
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-sm: 0.5rem
  gutter-lg: 1.5rem
  margin: 1rem
  margin-tablet: 1.5rem
  margin-expanded: 2rem
  space-xxs: 0.125rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
  space-xxl: 3rem
---

## Brand & Style

This design system delivers a purposeful, focused Android experience grounded in Google’s Material 3 (Material You) design philosophy, tailored specifically for offline-first academic management. The visual tone is structured, calm, and dependable—evoking the disciplined atmosphere of higher education while mitigating the anxiety associated with attendance tracking and tight schedule deadlines.

The aesthetic fuses Material 3's tonal surface hierarchy with high-clarity status signifiers. It deliberately avoids high-gloss effects, skeumorphism, or distracting decorative elements. Instead, it prioritizes clarity, rapid thumb-driven micro-interactions, and instant visual comprehension through color-coded attendance states (Present, Shortage, Borderline). Built natively for Jetpack Compose, the UI establishes an intuitive, calm environment where academic obligations feel organized, transparent, and actionable.

## Colors

The color palette adheres strictly to Material Design 3 dynamic color guidelines while locking in academic-specific semantic roles. Primary Deep Academic Indigo anchor primary interactive targets, active tabs, and primary action buttons. The Slate Blue secondary family manages secondary supporting controls, category chips, and filter elements. Deep Teal functions as an intellectual focus accent, reserved for upcoming timetable slots, laboratory periods, and study timer states.

Attendance states rely on high-clarity semantic pairing:
- **Present / Safe Attendance**: Dark Emerald Green (`#2E7D32`) paired with light container `#C8E6C9` for attendance percentages meeting the academic target (typically ≥ 75% or 85%).
- **Warning / Critical Shortage**: Crimson Red (`#D32F2F`) paired with container `#FFDAD6` for sessions falling beneath statutory cutoffs.
- **Caution / Borderline State**: Amber Orange (`#ED6C02`) paired with container `#FFE082` indicating courses within 1–2 missed lectures of threshold violation.

Surfaces employ M3 tonal tiers (`surface-container-low` through `surface-container-high`) rather than stark drop shadows to articulate structure, card groups, and offline persistent banners.

## Typography

The type system adopts Google's systematic Material 3 scale realized via `Roboto Flex`. Weight distribution provides immediate glanceable information hierarchy essential for mobile campus use: bold percentage numbers on schedule overviews, crisp medium-weight labels for lecture codes and hall rooms, and neutral body scales for course descriptions and offline sync metadata.

- **Display & Headline**: Used on the attendance dashboard for aggregate percentage totals (e.g. "84%") and day views ("Wednesday, Oct 24").
- **Title**: Anchors course cards (e.g., "CS-301: Algorithms") and Bottom Sheet sheet titles.
- **Body**: Applies to class descriptions, attendance calculation formulas, and note snippets.
- **Label**: Applied to timetable time slots ("09:00 - 10:30"), status pill chips ("Present", "Absent", "Cancelled"), and M3 Navigation Bar text labels.

## Layout & Spacing

Layouts follow Android Jetpack Compose Window Size Classes (Compact &lt; 600dp, Medium 600dp–840dp, Expanded &gt; 840dp).

- **Compact (Mobile)**: 4-column layout model with `16dp` (`1rem`) outer margins and `16dp` gutters. The Timetable utilizes a vertical timeline track; schedule cards span full width.
- **Medium (Foldables & Small Tablets)**: 8-column layout with `24dp` margins. Timetable planner switches to a 3-day multi-column view with a persistent side rail navigation.
- **Expanded (Tablets & Desktop)**: 12-column layout with `32dp` margins. Shows a complete 5-to-7-day week schedule view side-by-side with an attendance breakdown inspector sheet.

Vertical rhythm adheres strictly to 8dp increments (using `4dp` strictly for tight micro-spacing such as between course codes and section badges). List padding and card insets consistently measure `16dp`.

## Elevation & Depth

This system implements native Material 3 Tonal Elevation where elevation levels tint surfaces with the Primary Academic Indigo color instead of casting harsh shadows:

- **Level 0 (Flat)**: `0dp` elevation. Surface color `#FAF9FE`. Used for the main canvas background and plain list views.
- **Level 1 (Resting Cards & App Bar)**: `1dp` tonal tint via `surface-container-low` (`#F4F3F8`). Used for standard course cards and Scaffolds. A `1dp` border using `outline-variant` (`#C7C5D0`) is applied for crisp card delineation.
- **Level 2 (Floating Cards & Quick-Action Triggers)**: `3dp` tonal tint via `surface-container` (`#EEEEF3`). Applied during card drag-and-drop in timetable rearrangement.
- **Level 3 (FAB, Dropdown Menus, Navigation Bar)**: `6dp` tonal tint via `surface-container-high` (`#E8E7ED`). Minimal ambient blur shadow: `y: 4dp`, `blur: 8dp`, color `rgba(27, 27, 31, 0.08)`.
- **Level 4 (Modal Bottom Sheets & Dialogs)**: `8dp` tonal tint using `surface-container-highest` (`#E2E1E7`) with a 32% black scrim backdrop (`rgba(0, 0, 0, 0.32)`).

## Shapes

The design system applies the standard Material 3 corner smoothing hierarchy:

- **Extra-Small (4dp)**: Micro badges, offline status indicators, and timetable timeline indicator pips.
- **Small (8dp)**: Form input fields, snackbars, and attendance quick-stepper count buttons.
- **Medium (12dp)**: Timetable class event blocks, course cards, and attendance warning banners.
- **Large (16dp)**: Navigation drawer container, dialog boxes, and floating hero metrics.
- **Extra-Large (28dp)**: Modal Bottom Sheet top corners, Floating Action Buttons (FAB), and search bars.
- **Full / Pill (9999px)**: Navigation Bar active indicator pills, filter chips, attendance status pills ("Present", "Absent", "Excused"), and circular avatars.

## Components

### Buttons
- **Filled Button**: Primary Indigo (`#3F51B5`) fill, white text, height 40dp, `corner-full` (pill) shape. Used for major commitments (e.g., "Mark All Present", "Save Timetable").
- **Tonal Button**: Secondary Container (`#E0E1F9`) fill, On Secondary Container (`#181A2C`) text. Used for intermediate actions ("Add Extra Class", "Export PDF").
- **Outlined Button**: 1dp `outline-variant` border, transparent background, `corner-full`. Used for secondary dismissals or filters.

### Chips
- **Attendance Status Chips**: `corner-full`, 32dp height. 
  - *Present*: Fill `#C8E6C9`, Text `#0A3812`.
  - *Absent*: Fill `#FFDAD6`, Text `#410002`.
  - *Excused/Late*: Fill `#FFE082`, Text `#2A1200`.
- **Filter Chips**: 32dp height, stroked with `outline-variant` when unselected; filled with `secondary-container` with an active checkmark when selected.

### Cards
- **Timetable Slot Card**: `corner-medium` (12dp) with surface container low background (`#F4F3F8`), framed with 1dp `outline-variant` (`#C7C5D0`). Displays class title in `title-md`, room/professors in `body-sm`, and an interactive check/cross attendance toggle.
- **Attendance Summary Card**: `corner-large` (16dp) featuring a circular percentage progress indicator color-coded via the semantic palette (Green &gt; target, Amber near target, Red below target).

### Navigation Bar
- Height 80dp, surface container background (`#EEEEF3`).
- Active item displays an Indigo pill active indicator (`#DEE0FF`) sized 64dp wide by 32dp tall, enclosing the active icon in On Primary Container (`#00105C`). Typography uses `label-md` with `fontWeight: 500`.

### Floating Action Button (FAB)
- Standard FAB: 56dp × 56dp, shape `corner-large` (16dp), filled with Primary Container (`#DEE0FF`) with On Primary Container (`#00105C`) icon. Used strictly for "Add Class / Schedule Event".
- Extended FAB: 56dp height, shape `corner-large` (16dp), containing icon and text label for rapid log sessions.

### Modal Bottom Sheet
- Top corners styled with `corner-extra-large` (28dp).
- Surface container highest (`#E2E1E7`) with a centered Drag Handle: width 32dp, height 4dp, `corner-full`, colored with `outline` (`#777680`). Used for "Quick Attendance Log" and "Course Details".

### Lists & Steppers
- Single-line and two-line list items have min-height 56dp and 72dp respectively.
- Interactive attendance steppers allow swift "+1 Attended" or "+1 Missed" logging directly from the schedule feed without navigating into nested screens.