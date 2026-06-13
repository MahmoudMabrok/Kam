# Handoff: Kam — Cost-of-Living Comparison App Revamp

## Overview
A complete visual + UX revamp of **Kam** (cost-of-living / salary comparison app, currently on Google Play as `tools.mo3ta.kam`). The user picks an origin city + their monthly salary, a destination city, and optionally a real salary offer in the destination. The app returns: cost difference %, breakeven salary, purchasing-power change, a lifestyle tier (Basic → Middle → Comfortable → Luxury), an offer-vs-breakeven verdict, and a per-category cost breakdown.

## About the Design Files
The files in this bundle are **design references created in HTML** — interactive prototypes showing intended look and behavior, NOT production code to copy directly. The task is to **recreate these designs in the target codebase's existing environment** (the existing Android app — Kotlin/Compose or Flutter — or whatever stack the team chooses) using its established patterns and libraries. If no environment exists yet, choose the most appropriate mobile framework and implement the designs there. The math in `data.js` IS intended as a reference implementation of the comparison logic.

## Fidelity
**High-fidelity.** Colors, typography, spacing, radii, copy and interactions are final design intent. Recreate pixel-perfectly, substituting platform-native equivalents where appropriate (e.g. system bottom-sheet, native keyboard haptics).

## Design Tokens

### Colors
| Token | Value | Use |
|---|---|---|
| `--paper` | `#F4F0E6` | App background (warm cream) |
| `--paper-2` | `#ECE7DA` | Secondary background |
| `--card` | `#FCFAF5` | Card surfaces |
| `--card-2` | `#F3EEE2` | Inset/pressed surfaces, bar tracks |
| `--ink` | `#1A1815` | Primary text, dark surfaces, CTA |
| `--ink-2` | `#6E695D` | Secondary text |
| `--ink-3` | `#A8A192` | Tertiary text, labels |
| `--line` | `rgba(26,24,21,0.10)` | Borders |
| `--green` | `oklch(0.57 0.11 155)` ≈ `#2E8B57` | Positive: cheaper, gains, accent |
| `--green-soft` | green @ 12% alpha | Positive backgrounds |
| `--terra` | `oklch(0.6 0.14 40)` | Negative: pricier, losses |
| `--terra-soft` | terra @ 12% alpha | Negative backgrounds |
| `--tier-0..3` | `oklch(0.6 0.035 80)`, `oklch(0.62 0.09 130)`, `oklch(0.57 0.11 155)`, `oklch(0.7 0.12 85)` | Tier colors Basic→Luxury |
| Dark gradient | `linear-gradient(165deg,#211E19,#14120F)` | Hero result card |
| Salary field | `linear-gradient(180deg,#1A1815,#26231e)` | Dark inset field |

### Typography
- **Display:** Space Grotesk (600/700) — headlines, numbers, buttons, city names. Always `font-variant-numeric: tabular-nums` on amounts.
- **Body/UI:** Hanken Grotesk (400–700) — labels, descriptions.
- Scale: hero h1 33px/-1px tracking; verdict 27px; breakeven amount 46px/-1.5px; salary field 30px; card section labels 11px/600/+1.3px letterspacing UPPERCASE; body 13.5–15.5px; micro-labels 10.5–11.5px.

### Spacing & Shape
- Screen padding: 20px horizontal; 60px top (below status bar); 46px bottom.
- Card radius 22–26px; fields 16–18px; chips 9–14px; full-round pills.
- Card shadow: `0 1px 2px rgba(0,0,0,.04)`; hero/CTA: `0 8–18px 22–40px -8..-18px rgba(26,24,21,.5)`.
- Gaps: cards stack with 14px; grid gaps 9–13px. All rows use flex/grid + gap.

## Screens / Views

### 1. Home (compare setup)
- **Top bar:** wordmark "Kam" (Space Grotesk 700, 23px) with a 12px green dot (4px soft-green halo) + circular settings icon button (40px, card bg).
- **Hero:** "How far does your salary *travel?*" — "travel?" in green. Sub: "Compare living costs and real purchasing power between any two cities." (ink-2, 15.5px, max 31ch).
- **Compare card** (card bg, 26px radius, 7px inner padding):
  1. **FROM city selector** — label `FROM` (11px caps), region chip (28px rounded square showing 2-letter country code), city name 21px + country 13px, right chevron. Tap → city picker sheet.
  2. **Salary field** — dark gradient inset, label "YOUR MONTHLY SALARY", amount 30px tabular + currency code, green "Edit" pill top-right. Tap → keypad sheet.
  3. **Divider with swap button** — 42px square (13px radius) floating right of a 1px divider; swaps cities; rotates 180° on press.
  4. **TO city selector** — same as FROM.
  5. **Offer affordance** — if no offer: dashed-border button "+ Compare a salary offer in {dest}". If offer set: inset field (card-2 bg) with label "Salary offer in {dest}", amount 24px, and a 34px circular ✕ clear button beside it.
  6. **Live strip** — soft green/terra pill row. No offer: "↓ {dest} is **32% cheaper**". With offer: "↑ Offer is **26% above** your breakeven".
- **CTA:** full-width dark button "See full comparison →" (18px padding, 18px radius).

### 2. Results
- **Top bar:** circular back button + mini route "chip {origin} → chip {dest}" (14.5px, 22px chips).
- **Verdict headline:** "{dest} is *NN% cheaper/more expensive* than {origin}." — % phrase colored green/terra. 27px.
- **Hero breakeven card** (dark gradient, cream text): tag "BREAKEVEN SALARY", amount 46px + "/mo", description "earn this in {dest} to keep the lifestyle your **{salary}** buys in {origin}." Footer row (top border 14% white): "Keep your salary {converted}/mo" + pill "+NN% buying power" (green/terra tinted).
- **Your offer card** (only when offer set): head "YOUR OFFER" + badge "+NN% vs breakeven"; amount 36px; copy "Worth **{equivalent}** in {origin} terms — your lifestyle would improve/decline by about **NN%**."
- **Purchasing power card:** head + badge ("goes further"/"goes less far"); headline "Your money goes **+NN%** further"; **diverging bar**: track 14px tall (card-2), fill from a zero-marker (2px ink line at the −80..+160% scale's zero point) extending green right / terra left; labels "Less far / Same / Further".
- **Lifestyle level card:** tier name 24px in its tier color + one-line blurb; **4-segment meter** (7px tall segments, 4px gap, active segment tinted in tier color, labels under each); 15px ink marker dot with 3px card-color ring at the salary's position. With an offer: hollow ghost marker = current lifestyle, solid = with offer; legend "○ now ● with offer".
- **Cost breakdown card:** legend "{origin city} (gray) {dest city} (green)". 5 rows (Rent, Groceries, Dining out, Transport, Leisure): label 74px column, two stacked 7px bars (origin = gray 55% opacity, dest = green if cheaper / terra if pricier, widths proportional to index), right column ±NN% diff (tabular, colored).
- **Footnote:** "Indexed to New York = 100. Currency converted at 1 {cur} ≈ N.NN {cur}. Estimates for guidance only." (11.5px, ink-3).

### 3. City picker (bottom sheet)
Title "Where do you live now?" / "Compare with…". Search field (card bg, 14px radius, magnifier icon, placeholder "Search city or country", filters city+country, case-insensitive). Rows: region chip, city 16.5px + country 12.5px, cost-index badge (card-2 pill, tabular), green check on selected. Picking the other side's city swaps them. Empty state: "No cities match “{q}”."

### 4. Salary keypad (bottom sheet)
Title "Your monthly salary" or "Salary offer in {dest}". Display: currency symbol 26px + amount 48px tabular + currency code. 3×4 grid keys 58px tall (16px radius, card bg, 24px Space Grotesk): 1-9, ".", 0, backspace icon. Max 9 digits, single decimal point, leading-zero replaced. Dark "Done" button.

## Interactions & Behavior
- **Screen transition:** home ↔ results is a horizontal slide, 420ms `cubic-bezier(.7,0,.2,1)` (200%-wide flex slider, translateX(-50%)). Results scrolls to top on entry.
- **Bottom sheets:** slide up 400ms `cubic-bezier(.6,0,.2,1)`, scrim `rgba(20,18,15,.42)` fading 320ms; grip bar 38×5px; tap scrim to dismiss. Max height 88%.
- **Live updates:** the live strip, all results numbers, bars and markers recompute instantly on any change (salary keystroke, city pick, swap). Bars/markers animate to new values (400–500ms, same easing).
- **Press states:** CTA/Done scale .98; keypad keys scale .96 + bg change; list rows tint card-2; swap rotates 180°.
- **Offer rules:** entered in DEST currency; cleared via ✕ (reverts all offer UI); swap does NOT carry the offer sensibly — acceptable to clear offer on swap in production.
- **Hit targets:** all ≥ 44px.

## State Management
- `originId`, `destId` (city ids), `salaryStr` (origin currency, string for keypad editing), `offerStr` (dest currency, '' = none), `screen` ('home'|'result'), `picker` (null|'origin'|'dest'), `keypad` (null|'salary'|'offer').
- Selecting a city equal to the other side's city swaps them.
- Production: persist last route + salary; fetch live indices/FX (e.g. Numbeo + FX API) instead of the bundled table.

## Core Math (see data.js — reference implementation)
- Each city: overall cost index (NYC=100), rent index, currency + USD rate.
- `salaryUSD = salary / origin.perUSD`
- `breakeven = salaryUSD × destIndex/originIndex × dest.perUSD`
- `powerPct = (originIndex/destIndex − 1) × 100`; `costDeltaPct = (destIndex−originIndex)/originIndex × 100`
- Lifestyle tier from NYC-adjusted salary `salaryUSD × 100/destIndex`: <2500 Basic, <5000 Middle, <9000 Comfortable, else Luxury. Meter position = adjusted/13000 clamped to 4–96%.
- Offer: `vsBreakevenPct = (offerUSD − breakevenUSD)/breakevenUSD × 100`; home-equivalent = `offerUSD × originIndex/destIndex × origin.perUSD`.
- Category indices: rent uses explicit rent index; others use affine curve `base + (index − base) × slope` — Groceries (40, 0.78), Dining (22, 1.25), Transport (35, 0.62), Leisure (30, 1.05) — so category deltas differ realistically.

## Assets
- No raster assets. Icons are inline SVG strokes (1.6–2.2px, round caps): settings gear, chevrons, swap arrows, search, backspace, check, ✕. Region "flags" are intentionally 2-letter country-code chips — replace with real flag assets if available in the codebase.
- Fonts from Google Fonts: Space Grotesk, Hanken Grotesk.

## Files
- `Kam Revamp.html` — entry point + full stylesheet (design tokens at `:root`)
- `app.jsx` — screens (Home, Results), sheets, app state
- `components.jsx` — RegionChip, PowerBar, TierMeter, Breakdown, Keypad, Sheet
- `data.js` — city dataset + all comparison math (reference implementation)
- `ios-frame.jsx`, `tweaks-panel.jsx` — prototype scaffolding only; ignore for implementation
