# Design System: ClassSync

## 1. Visual Theme & Atmosphere
ClassSync should feel like an academic control deck with editorial restraint rather than a generic student app. The interface is balanced in density, asymmetrical in emphasis, and quietly kinetic through subtle gradients, priority tinting, and precise motion. The emotional tone is calm, serious, and reassuring, like a well-organized studio desk late at night with everything mapped clearly.

Density: 5/10 daily-app balanced.
Variance: 7/10 offset asymmetric.
Motion: 6/10 fluid and premium, never flashy.

## 2. Color Palette & Roles
- **Ivory Canvas** (`#F6F3ED`) — Primary light background, warm and paper-like rather than sterile white
- **Studio White** (`#FFFFFF`) — Primary surface fill for elevated cards and navigation shells
- **Graphite Ink** (`#14171A`) — Primary text, iconography, deep anchors
- **Archive Slate** (`#666055`) — Secondary text, supporting copy, metadata
- **Dust Line** (`#B6B1A8`) — Structural borders, dividers, low-contrast outlines
- **Sage Signal** (`#6E897B`) — Single accent for CTAs, active states, focus, selected navigation
- **Night Deck** (`#101316`) — Dark mode background
- **Night Surface** (`#1A1E22`) — Dark mode elevated surfaces
- **Soft Mint** (`#8CA893`) — Secondary support tint in dark mode and positive state range
- **Clay Alert** (`#BC6F62`) — Warning / overdue / destructive state

Rules:
- Only one true accent: Sage Signal.
- No purple, electric blue, or neon glow.
- No pure black.
- All shadows and overlays should inherit from the surrounding neutral temperature.

## 3. Typography Rules
- **Display:** Android sans-serif with tight tracking and strong weight. Headlines are large but controlled, never oversized for spectacle.
- **Body:** Android sans-serif with relaxed leading and high readability.
- **Mono:** Android monospace for timestamps, labels, operational metadata, counters, and status language.
- **Hierarchy rule:** Use weight and rhythm before using giant size jumps.
- **Dashboard rule:** Serif is banned. The product is operational software, not editorial prose.

## 4. Component Stylings
- **Buttons:** Rounded but not bubbly. Primary selected state uses solid Sage Signal fill with subtle tonal depth. Idle state uses ivory-to-surface gradient with thin dust border. Active press should feel tactile, slightly weighty.
- **Panels:** Large rounded containers with quiet vertical gradients, 1px borders, and low-contrast depth. Use panels to group workflows, not decorate empty space.
- **Stat Cards:** Emphasize a single number, a mono eyebrow, and a compact support line. Accent should appear as a restrained tint, not a full-color flood.
- **Chips:** Soft filled pills with no border and crisp mono labeling. Chips communicate categorization, not interactivity.
- **Inputs:** Label above or embedded clearly. No floating-label gimmicks. Focus ring should use Sage Signal.
- **Empty States:** Calm, informative, and composed. Avoid mascot energy or playful filler.
- **Glass Navigation:** The bottom navigation should be one floating translucent dock with compact internal item pills. It remains fixed to the bottom but visually detached from screen edges. The dock must feel like smoked glass, not a heavy toolbar. Unselected items should nearly disappear into the dock; the active item gets a soft internal highlight, not a loud fill.

## 5. Layout Principles
- Compose screens should feel like stacked workboards rather than centered marketing pages.
- First screen block on each page should establish context with one strong headline and one operational sentence.
- Avoid repeating equal-sized cards in rigid three-column rows. Prefer staggered emphasis, 2-up rows, or a strong lead card with secondary companions.
- Keep generous internal padding: most surfaces should breathe at `18dp` to `24dp`.
- Navigation should feel like a dock: compact, grounded, and persistent.
- No overlapping elements. Every element must occupy a clear spatial lane.

## 6. Motion & Interaction
- Default interaction feel: weighted spring, calm settle, no bouncy toy motion.
- Lists should appear intentionally, not snap in abruptly.
- Only animate opacity and transform-like effects where possible.
- Loading states should use skeletal or restrained progress indicators, never loud spinner-centric layouts.
- High-priority accents may pulse softly, but no halo glows.
- Liquid-glass surfaces must be visually light and technically cheap: avoid stacking multiple large gradients, overdraw-heavy backgrounds, or excessive shadow layers on every component.

## 7. Screen Intent
- **Home:** A live academic overview with one strong hero summary, then urgency, next actions, and digest layers.
- **Tasks:** A disciplined work desk. Manual creation and synced work should feel equally native.
- **Planner:** A broader map of time with lower emotional intensity than Tasks.
- **Classroom:** A data-rich schedule browser with a strong sense of structure and day-by-day filtering.
- **Settings:** A control room, not a utility dump. Group settings by operational purpose.
- **Auth:** Clear, trustworthy, and privacy-forward. Explain exactly what gets connected and why.
- **Widgets:** Compact command surfaces, not shrunk app screens. Widgets must prioritize one focal task, two supporting metrics, and a single status pill. The widget layout may only exist in `2x2` and `3x2` launcher footprints. `1x1` is explicitly banned.

## 8. Widget Rules
- Widget composition must avoid the generic three-equal-card strip. Use two supporting metric tiles plus one dominant focus card.
- Widget labels should use operational mono language such as `TODAY`, `URGENT`, `PRIMARY FOCUS`, and `UPDATED`.
- Widget typography should feel dense and premium: strong focal title, compressed support lines, minimal filler.
- Widget backgrounds should read as miniature product surfaces with quiet borders and one accent state, not colorful badges floating on the launcher.
- `2x2` is the minimum allowed size. `3x2` is the maximum allowed size. Height should remain locked at two rows.

## 9. Anti-Patterns (Banned)
- No emojis
- No Inter
- No serif fonts in app UI
- No pure black (`#000000`)
- No neon or outer glow shadows
- No oversaturated accents
- No giant gradient headlines
- No custom cursor metaphors
- No overlapping layers for decoration
- No equal three-card feature grids as a default pattern
- No filler copy like “Swipe down” or “Scroll to explore”
- No empty states that just say “No data”
- No toy-like glassmorphism that lowers contrast
- No `1x1` home-screen widget
- No widget layout that mirrors a generic three-column KPI dashboard
