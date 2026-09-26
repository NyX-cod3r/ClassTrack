---
name: Kinetic Campus
colors:
  surface: '#121316'
  surface-dim: '#121316'
  surface-bright: '#38393c'
  surface-container-lowest: '#0d0e11'
  surface-container-low: '#1b1b1f'
  surface-container: '#1f1f23'
  surface-container-high: '#292a2d'
  surface-container-highest: '#343538'
  on-surface: '#e3e2e6'
  on-surface-variant: '#c6c5d0'
  inverse-surface: '#e3e2e6'
  inverse-on-surface: '#2f3034'
  outline: '#90909a'
  outline-variant: '#46464f'
  surface-tint: '#bac3ff'
  primary: '#dee0ff'
  on-primary: '#232c5e'
  primary-container: '#bac3ff'
  on-primary-container: '#464f83'
  inverse-primary: '#525b90'
  secondary: '#44efd8'
  on-secondary: '#003731'
  secondary-container: '#00d2bc'
  on-secondary-container: '#00554b'
  tertiary: '#ffddb4'
  on-tertiary: '#452b00'
  tertiary-container: '#ffb954'
  on-tertiary-container: '#734a00'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#dee0ff'
  primary-fixed-dim: '#bac3ff'
  on-primary-fixed: '#0c1649'
  on-primary-fixed-variant: '#3a4376'
  secondary-fixed: '#55fbe3'
  secondary-fixed-dim: '#25dec7'
  on-secondary-fixed: '#00201c'
  on-secondary-fixed-variant: '#005047'
  tertiary-fixed: '#ffddb4'
  tertiary-fixed-dim: '#ffb954'
  on-tertiary-fixed: '#291800'
  on-tertiary-fixed-variant: '#633f00'
  background: '#121316'
  on-background: '#e3e2e6'
  surface-variant: '#343538'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 57px
    fontWeight: '700'
    lineHeight: 64px
    letterSpacing: -0.25px
  display-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 45px
    fontWeight: '700'
    lineHeight: 52px
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 32px
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  title-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  title-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: 0.15px
  title-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.1px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.5px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.25px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0.4px
  label-lg:
    fontFamily: JetBrains Mono
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-md:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
  label-sm:
    fontFamily: JetBrains Mono
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.5px
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1rem
  gutter-tablet: 1.5rem
  margin-tablet: 2rem
  gutter-desktop: 2rem
  margin-desktop: 3rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style
The design system delivers an energetic, high-precision academic utility aesthetic inspired by modern Android Jetpack Compose and Material 3 principles. It is engineered for students managing dynamic schedules, attendance margins, and lecture tracking under low-light or night environments.

The visual style blends deep-space OLED backdrops with high-potency electric luminescence. The interface conveys structural discipline and instant legibility:
- **Style:** Contemporary Material 3 Expressive Dark Mode with high-contrast functional color accents.
- **Audience:** Higher-education students, collegiate staff, and academic trackers demanding quick glanceability during transitions between lectures and study sessions.
- **Emotional Response:** Controlled, focused, capable, and modern. It trades mundane institutional drabness for vibrant, electric confidence.

## Colors
The palette leverages high-luminance pastels against layered deep neutral surfaces to maintain contrast ratios exceeding WCAG AAA standards on dark displays.

### Role Tokens & Color Architecture
- **Primary (`#BAC3FF`)**: Soft electric indigo tint used for high-emphasis controls, active navigation nodes, primary FABs, and selected timetable modules. The deep container tone (`#4F5BCF`) grounds active selections without overpowering viewports.
- **Secondary (`#00D4BE`)**: Electric cyan/teal accent reserved for live telemetry, positive attendance thresholds (e.g., &gt;75% required attendance), checked status badges, and active class countdown timers. Subdued container pairings utilize `#26A69A`.
- **Tertiary / Warning Amber (`#FFB74D`)**: Warm amber utilized for conditional attendance risk states, upcoming assignment alerts, and schedule shifts.
- **Critical / Error (`#FFB4AB`)**: High-contrast coral tone for attendance deficits, cancelled sessions, or room collisions, balanced by container base `#FF5449`.
- **Surface Hierarchy**:
  - `surface-dim`: `#0e0f11`
  - `surface` (Base canvas): `#121316`
  - `surface-container-low`: `#1a1b20`
  - `surface-container`: `#202126`
  - `surface-container-high`: `#2b2c32`
  - `surface-bright`: `#37383f`
- **Typography & Icons**:
  - `on-surface`: `#e3e2e8` (Primary typography)
  - `on-surface-variant`: `#a5a6b0` (Metadata, inactive timestamps, room numbers)
  - `outline`: `#44464f`
  - `outline-variant`: `#282930`

## Typography
The system uses **Plus Jakarta Sans** for expressive, geometric headers and clean screen legibility, paired with **JetBrains Mono** for all quantitative and time-critical tracking components.

- **Plus Jakarta Sans**: Drives interface structure, day-of-week headers, course titles, lecturer identities, and modal headlines. Its rounded, open geometry prevents fatigue in dense schedule matrices.
- **JetBrains Mono**: Assigned to temporal markers, start/end timestamps, seat designations, attendance percentages (`92.4%`), lecture hall coordinates (`LH-302`), and progress ratios (`14/16 sessions`). The tabular figures prevent layout jank during animated counts and real-time updates.

## Layout & Spacing
The layout uses an adaptive responsive grid based on an 8pt architectural rhythm, with a supplementary 4pt sub-grid for dense information blocks (attendance metric badges, micro badges, and chip groups).

### Grid and Form Factors
- **Mobile (0 - 599dp)**: 4-column fluid grid. Screen margins are `1rem` (16px), column gutters `1rem` (16px). Daily timeline agenda view with vertically stacked course cards and horizontally scrollable day pickers.
- **Tablet (600 - 839dp)**: 8-column fluid grid. Margins expand to `2rem` (32px), gutters `1.5rem` (24px). Dual-pane configuration: full-week mini-matrix on the left, active day detail schedule on the right.
- **Desktop / Large Foldables (840dp+)**: 12-column layout. Margins set to `3rem` (48px), gutters to `2rem` (32px). 5-day or 7-day expansive spreadsheet timetable layout with fixed sidebar navigation rail.

All cards and container blocks pad internal content using `space-md` (16px) for standard items and `space-sm` (8px) for compact widgets.

## Elevation & Depth
In alignment with modern Material 3 Dark specifications, elevation is defined strictly through **tonal surface elevation** rather than drop shadows. Shadow dispersion on deep OLED backgrounds causes muddy rendering; tonal layering preserves crisp visual boundaries.

### Tonal Hierarchy
- **Level 0 (Base / Canvas)**: `surface` (`#121316`) — Screen background, canvas behind scrollable feeds.
- **Level 1 (Structural Tiling)**: `surface-container-low` (`#1A1B20`) — Calendar day tracks, background lane of timetable columns.
- **Level 2 (Standard Units)**: `surface-container` (`#202126`) — Course agenda cards, attendance record items, dialog backgrounds.
- **Level 3 (Interactive Floating Panels)**: `surface-container-high` (`#2B2C32`) — Bottom navigation bars, modal sheets, filter chips, contextual flyouts.
- **Level 4 (Prominent Overlays)**: `surface-bright` (`#37383F`) — Floating Action Buttons (when unfilled), active search surfaces, active course cards currently in progress.

### Border Outlines
To delineate adjacent cards of similar elevation, a hairline ghost border (`1px solid #282930`) is applied to cards, increasing to `1px solid #44464F` for interactive text fields and unfocused selection states.

## Shapes
The system applies Material 3 Expressive pill and high-curvature geometric radii. Curved shapes visually counter-balance the rigid, modular nature of schedules and academic data tables.

- **Full / Pill (9999px)**: Floating action buttons, primary action buttons, filter tags, attendance stat badges, search input bars, and current-time markers.
- **Extra Large (`rounded-xl` / 48px)**: Bottom sheets, modal bottom dialogs, and large navigation drawer panels.
- **Large (`rounded-lg` / 32px)**: Timetable lecture cards, summary metric dashboards, and attendance projection cards.
- **Medium (16px)**: Nested card panels, status dialogs, and classroom floor-plan preview containers.

## Components

### Buttons & Interactive Controls
- **Primary Filled Button**: Height 48px, pill-shaped (`rounded-full`), background `#BAC3FF` with label text `#1E2978` (Plus Jakarta Sans, 14px SemiBold). Pressed state shifts to overlay `rgba(255, 255, 255, 0.12)`.
- **Tonal Button**: Background `#202126`, border `1px solid #44464F`, text `#BAC3FF`.
- **Floating Action Button (FAB)**: 56x56px pill/squircle hybrid (`rounded-2xl`), container `#4F5BCF`, icon and foreground `#FFFFFF`.

### Course & Timetable Cards
- **Base Card**: Rounded 24px (`rounded-lg`), background `#202126`, border `1px solid #282930`, padding 16px.
- **Active / Ongoing Lecture Card**: Background `#2B2C32`, left border indicator `4px solid #00D4BE` (Secondary), with glowing teal time indicator badge using JetBrains Mono typography.
- **Card Hierarchy**: Upper-right contains attendance badge; main body shows course code in JetBrains Mono (`CS-402`) above course title in Plus Jakarta Sans (`Distributed Systems`); bottom metadata displays room tag and lecturer name.

### Attendance Metric Badges & Chips
- **Status Pills**: Height 28px, pill-shaped (`rounded-full`), horizontal padding 12px.
  - Safe (&gt;80%): Background `rgba(0, 212, 190, 0.12)`, text `#00D4BE`.
  - Warning (75% - 80%): Background `rgba(255, 183, 77, 0.12)`, text `#FFB74D`.
  - Critical (&lt;75%): Background `rgba(255, 180, 171, 0.12)`, text `#FFB4AB`.
- **Filter Chips**: Height 32px, pill-shaped, surface `#1A1B20`, border `1px solid #282930`. Selected state transitions background to `#BAC3FF`, border to transparent, and text to `#1E2978`.

### Lists & Timelines
- Items display alternating vertical connection tracks in `surface-container-high` (`#2B2C32`).
- Check-in list rows use surface `#1A1B20`, separating elements via 8px gaps instead of divider lines to retain modular card appearance.

### Form Inputs & Text Fields
- Filled container style with background `#202126`, corner radius 16px on top/bottom or full pill for single-line search inputs.
- Resting border `1px solid #44464F`, active focus border `2px solid #BAC3FF`.
- Floating label in `on-surface-variant` (`#A5A6B0`), moving to `#BAC3FF` when focused.

### Attendance Sliders / Steppers
- Segmented counter for calculating required future attendances: pill-container `#1A1B20`, with increment/decrement circle buttons `#2B2C32` and active number displayed in `JetBrains Mono` Title-Large.