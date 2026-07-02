# Design System: ClassSync Privacy Editorial Surface

## 1. Visual Theme & Atmosphere
A premium privacy page that feels closer to a launch briefing than a legal template. Density should sit in the "Daily App Balanced" range, variance should be confidently asymmetric, and motion should remain restrained but tactile. The page should feel trustworthy, product-led, and considered rather than clinical or corporate.

## 2. Color Palette & Roles
- **Midnight Ledger** (`#081018`) — Primary page canvas
- **Slate Core** (`#111B28`) — Deeper ambient field and secondary background tone
- **Frosted Panel** (`rgba(12,18,28,0.82)`) — Main card and shell fill
- **Steel Line** (`rgba(172,191,218,0.16)`) — Structural border and divider tone
- **Signal Blue** (`#8CB4FF`) — Single accent for navigation anchors, calls to action, focus states, and metadata highlights
- **Cold Paper** (`#F2F5FB`) — Light mode canvas
- **Archive Ink** (`#0E1523`) — Light mode primary text

Only one accent color is allowed. No purple gradients, neon glows, or pure black surfaces.

## 3. Typography Rules
- **Display:** `Outfit` — track-tight, low line-height, high presence, balanced wrapping
- **Body:** `Outfit` — readable line length near `60–65ch`, medium and semibold used for hierarchy
- **Mono:** `JetBrains Mono` — labels, indices, timestamps, and technical metadata
- **Banned:** `Inter`, generic serif fonts, and default document typography

## 4. Component Stylings
- **Top Bar:** Compact glass utility row with brand lockup and legal/support actions
- **Navigation Rail:** Sticky vertical section navigator, not tabs or pills-only navigation
- **Hero Banner:** 16:9 abstract branded field with centered ClassSync logo and wordmark, no device mockups
- **Summary Cards:** Tight briefing cards for publisher, contact, and update state
- **Policy Cards:** Mixed-width editorial cards in a 12-column layout, never equal legal tiles across the whole page
- **Footer Panel:** Separate support surface with a clear top divider and action pills

## 5. Layout Principles
- Use an asymmetric split between a sticky left rail and a larger right hero surface
- Preserve max-width containment around `1380px`
- Use a 12-column editorial grid for the detailed policy body
- Avoid equal-width rows when information density varies
- Keep major brand surfaces at a 16:9 rhythm where appropriate
- Collapse to a single-column stack below `1100px` with no horizontal overflow

## 6. Motion & Interaction
- Hover and focus states use `transform`, background, and border changes only
- Links and action pills may lift by `1px`
- Navigation rows should feel active through subtle structural shifts, not glow
- Smooth anchor scrolling and obvious keyboard focus are required

## 7. Anti-Patterns (Banned)
- No emojis
- No `Inter`
- No pure black `#000000`
- No neon outer glows or purple AI gradients
- No phone mockups or inline device imagery
- No centered generic hero composition
- No three-equal-card legal rows as the dominant pattern
- No plain document-style privacy page with one heading and a giant text wall
- No overlapping decorative gimmicks that reduce clarity or accessibility
