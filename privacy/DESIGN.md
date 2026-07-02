# Design System: ClassSync Privacy Surface

## 1. Visual Theme & Atmosphere
A restrained, premium control-deck interface with dark glass surfaces, cool academic blue signal color, and offset composition. Density is balanced rather than sparse, variance is intentionally asymmetric, and motion should feel fluid but never noisy. The page should read like a polished product brief rather than a generic legal document.

## 2. Color Palette & Roles
- **Night Canvas** (`#06080D`) — Primary dark background
- **Control Deck Slate** (`#11151F`) — Secondary background tone and soft depth zones
- **Glass Surface** (`rgba(10,14,22,0.82)`) — Main card and panel fill
- **Whisper Border** (`rgba(169,188,212,0.16)`) — Structural borders and glass edges
- **Signal Blue** (`#8BB8FF`) — Single accent for active states, privacy links, section markers
- **Cold Paper** (`#F2F5FB`) — Light mode canvas
- **Ink Text** (`#0D1422`) — Light mode primary text

Only one accent color is allowed. No purple, neon cyan, or glowing gradients.

## 3. Typography Rules
- **Display:** `Outfit` — tight tracking, bold but calm, used for the hero headline and section titles
- **Body:** `Outfit` — relaxed leading, maximum readable line length, neutral tone
- **Mono:** `JetBrains Mono` — metadata, section indices, short technical labels
- **Banned:** `Inter`, generic serif fonts, pure system-default typography stacks for hero surfaces

## 4. Component Stylings
- **Hero Banner:** Wide branded visual with an overlay veil, never used as a background behind text without separation
- **Logo Tile:** Rounded glass capsule that frames the ClassSync mark with subtle inner highlight
- **Meta Cards:** Rounded glass blocks with mono labels and short factual answers
- **Policy Sections:** Two-column staggered panels on desktop, single column on mobile, never three equal columns
- **Links:** Accent blue, no underglow, no novelty hover tricks

## 5. Layout Principles
- Split hero with main narrative panel on the left and summary/meta aside on the right
- Text never overlaps imagery; every element gets its own zone
- Maintain max width containment around `1180px`
- Collapse to single column below `768px`
- Policy content should feel editorial and modular, not one continuous wall of text

## 6. Motion & Interaction
- Any future motion should use spring-like `transform` and `opacity` transitions only
- Hover states may lift by `1px` with subtle timing
- No pulsing legal panels, no spinning loaders, no animated glows

## 7. Anti-Patterns (Banned)
- No emojis
- No `Inter`
- No pure black `#000000`
- No neon outer glows
- No centered generic hero
- No equal three-card feature rows
- No legal-template blandness with only plain text on white
- No overlapping logo and headline content
