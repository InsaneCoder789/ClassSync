# Design System: ClassSync Privacy Command Deck

## 1. Visual Theme & Atmosphere
A premium privacy page that feels like a product briefing room instead of a legal template. Density sits in the "Daily App Balanced" range, variance is clearly asymmetric, and motion should stay restrained but tactile. The atmosphere should combine dark glass surfaces, cool academic lighting, and clear editorial structure so the page reads as trustworthy, branded, and deliberate.

## 2. Color Palette & Roles
- **Midnight Ledger** (`#070B12`) — Primary canvas background
- **Slate Engine** (`#0F1622`) — Supporting background tone and ambient depth
- **Frosted Deck** (`rgba(14,19,29,0.76)`) — Main shell fill with glass effect
- **Steel Whisper** (`rgba(170,190,216,0.16)`) — Structural borders and separators
- **Signal Blue** (`#8DB6FF`) — Single accent for navigation anchors, focus states, and active policy markers
- **Cold Paper** (`#F2F5FB`) — Light mode canvas
- **Archive Ink** (`#0D1422`) — Light mode primary text

Only one accent color is permitted. No purple gradients, no neon blues, and no pure black surfaces.

## 3. Typography Rules
- **Display:** `Outfit` — track-tight, low line-height, balanced multi-line wrapping, built for strong hero statements
- **Body:** `Outfit` — readable line length capped near `65ch`, medium and semibold weights used for hierarchy
- **Mono:** `JetBrains Mono` — labels, indices, timestamps, and technical metadata
- **Banned:** `Inter`, browser default serif fonts, generic legal-template typography

## 4. Component Stylings
- **Top Bar:** Floating glass utility row with brand lockup and compact legal/support links
- **Hero Banner:** Full-width branded image with a dark veil overlay; text never sits directly on raw artwork
- **Policy Navigator:** Sticky aside made of stacked navigation rows rather than default pills or tabs
- **Quick Summary Cards:** Compact briefing cards that surface key facts such as publisher, contact, and launch posture
- **Policy Sections:** Mixed-width editorial panels arranged on a 12-column grid; avoid equal towers
- **Footer Actions:** Tertiary capsule links for back-to-top and source navigation

## 5. Layout Principles
- Use a split hero: main narrative panel on the left, policy map on the right
- Break the legal content into a staggered 12-column grid, not a uniform list of same-size cards
- Keep strong max-width containment around `1320px`
- Preserve clear spatial separation between media, headline, summary, and legal text
- Collapse to a clean single-column stack below `1024px`, with no horizontal overflow at any breakpoint

## 6. Motion & Interaction
- Hover and focus states should use `transform` and `opacity` only
- Links and cards may lift by `1px` for tactile feedback
- Navigation rows should feel interactive through border and background shifts, not glow
- Preserve smooth anchor scrolling and obvious keyboard focus visibility

## 7. Anti-Patterns (Banned)
- No emojis
- No `Inter`
- No pure black `#000000`
- No neon outer glows or purple AI gradients
- No centered, generic privacy hero
- No three-equal-card legal rows
- No plain white legal document styling with a single heading and wall of text
- No overlapping image/text gimmicks or inaccessible decorative clutter
