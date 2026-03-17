# CyberScouts - Improvement Requirements

## Table of Contents

1. [Branding: Rename to CyberScouts](#1-branding-rename-to-cyberscouts)
2. [Player Name Input for Leaderboard](#2-player-name-input-for-leaderboard)
3. [Fix Invisible Border at Bottom of Player](#3-fix-invisible-border-at-bottom-of-player)
4. [UI Text Overflow Fixes](#4-ui-text-overflow-fixes)
5. [Timer-Based Gameplay](#5-timer-based-gameplay)
6. [Entity Speed Variation](#6-entity-speed-variation)
7. [Vertical Movement / Jumping](#7-vertical-movement--jumping)
8. [Asset Size Adjustments](#8-asset-size-adjustments)
9. [Card System](#9-card-system)
10. [Two Player Mode](#10-two-player-mode)

---

## 1. Branding: Rename to CyberScouts

### Current State

The title screen in `GameMenuScene.java` (line 124) displays `"SILICON SENTINEL"` with a
tagline `"Protect the Network - Catch Good Data, Neutralize Threats"`. The `EntityType` enum
javadoc also references "Silicon Sentinel".

### Affected Files

- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/GameMenuScene.java` (title + tagline text)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/entities/EntityType.java` (javadoc)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/GamePlayScene.java` (javadoc header)
- Any other references to "Silicon Sentinel" across the codebase

### Requirements

- [ ] Replace `"SILICON SENTINEL"` with `"CYBERSCOUTS"` on the title screen
- [ ] Update the tagline if needed to match CyberScouts branding
- [ ] Update all javadoc/comments referencing "Silicon Sentinel"

---

## 2. Player Name Input for Leaderboard

### Current State

The player name is hardcoded as `"PLAYER"` in three places:

- `GameOverScene.java` line 38: `leaderboard.addEntry("PLAYER", finalScore)`
- `GamePlayScene.java` line 443: `leaderboard.addEntry("PLAYER", score)`
- `LeaderboardManager.java` line 78: `prefs.getString("name_" + i, "PLAYER")` (default on load)

`LeaderboardEntry` stores `playerName` and `score`, so the data model already supports
custom names. The leaderboard is persisted via libGDX `Preferences` in `LeaderboardManager`.

Currently there is no UI for entering a player name. The flow is:
`GameMenuScene` → `CharacterSelectScene` → `GamePlayScene` → `GameOverScene` → `LeaderboardScene`

### Affected Files

- **New file**: `NameEntryScene.java` (or equivalent name input UI)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/GameMenuScene.java` (route to name entry)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/GameOverScene.java` (use stored name)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/GamePlayScene.java` (use stored name)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/leaderboard/LeaderboardManager.java` (store/retrieve name)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/LeaderboardScene.java` (display names)

### Requirements

- [ ] Add a name entry screen or dialog where the player can type/choose a display name
- [ ] Persist the player name in `LeaderboardManager` (via libGDX `Preferences`)
- [ ] Replace all hardcoded `"PLAYER"` strings with the stored player name
- [ ] Show the player name on the leaderboard so different players can be differentiated
- [ ] Pre-fill the name field if a name was previously saved (returning player flow)
- [ ] Set a max character limit (e.g., 12 characters) and validate input

---

## 3. Fix Invisible Border at Bottom of Player

### Current State

The player is positioned at `WORLD_FLOOR = 110f` on the Y axis (`GamePlayScene.java` line 62).
The world coordinate system uses a `FitViewport(1280, 720)` defined in `Renderer.java`.

There is visible empty space between the player sprite and the bottom of the window. Likely
causes:

1. `WORLD_FLOOR = 110f` places the player 110 pixels above world-origin (y=0), leaving a gap
2. The HUD controls hint is drawn at `y = 12f` (bottom of screen), pushing content up
3. `FitViewport` may add letterbox bars when the window aspect ratio differs from 16:9
4. The player sprite origin may not align with the visual bottom of the character

Player dimensions: `PLAYER_WIDTH = 64f`, `PLAYER_HEIGHT = 80f` (in `EntityFactory.java`).
The sprite is drawn at the player's transform position.

### Affected Files

- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/GamePlayScene.java` (`WORLD_FLOOR` constant)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/io/output/Renderer.java` (viewport setup, sprite drawing origin)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/entities/EntityFactory.java` (player size constants)

### Requirements

- [ ] Reduce `WORLD_FLOOR` value to bring the player closer to the bottom of the screen
- [ ] Verify the player sprite's draw origin aligns with the visual bottom of the character asset
- [ ] Test in both windowed and fullscreen mode to confirm no gap remains
- [ ] Ensure the HUD/controls text does not overlap with the player at the new position
- [ ] Check that `FitViewport` letterboxing does not create additional perceived spacing

---

## 4. UI Text Overflow Fixes

### Current State

Text is rendered with hardcoded pixel offsets relative to background boxes. The `Renderer.drawText()`
method uses a `BitmapFont` positioned at a `Vector2` (bottom-left of text). There is no automatic
text wrapping or box-clipping.

Known overflow issues:

**Character Select Scene** (`CharacterSelectScene.java`):
- Character cards are 240x360 pixels. Perk descriptions and stat text may extend outside the card
  boundaries, especially for characters with longer text (e.g., Cipher's "Ultra-fast but risky.\nMassive score multiplier per catch.")

**Welcome Back Scene** (`StartGamePromptScene.java`):
- The dialog card is 440x280. The `"Last played: " + lastCharacter.getName()` text is positioned
  with fixed offsets and may appear behind/outside the card box depending on name length and
  font rendering

### Affected Files

- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/CharacterSelectScene.java`
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/StartGamePromptScene.java`
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/io/output/Renderer.java` (potential text-measurement utility)

### Requirements

- [ ] Audit all scenes for text that overflows its bounding box
- [ ] Fix text positioning in `CharacterSelectScene` so stats/perks stay within card boundaries
- [ ] Fix `"Last played"` text positioning in `StartGamePromptScene` to render inside the dialog card
- [ ] Consider adding a `measureText(String)` utility to `Renderer` for dynamic text centering
- [ ] Increase box sizes or reduce font sizes where text consistently overflows
- [ ] Test with all three character names/descriptions to confirm nothing clips

---

## 5. Timer-Based Gameplay

### Current State

The game currently uses a **collection counter** system:

- **Standard mode**: collect `FRENZY_COUNT = 10` good entities to trigger the frenzy transition
- **Frenzy mode**: collect `GOAL_COUNT = 100` good entities to win
- Progress is displayed as `"NORMAL  X / 10"` and `"FRENZY Xs  X / 100"` in the HUD

Score is a separate unbounded integer that accumulates from collecting good entities (5-10 base
points multiplied by character multiplier) and quiz bonuses (100 base points).

Frenzy mode already has a 30-second timer (`FRENZY_DURATION = 30f`) that counts down, but it runs
alongside the collection goal, not as the primary win/lose condition.

### Affected Files

- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/GamePlayScene.java` (game loop, win/lose conditions, HUD text)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/GamePlayScene.java` inner class `GamePlayRenderer` (HUD rendering)

### Requirements

- [ ] Define the new timer-based win/lose condition (e.g., survive for X seconds, or score as high as possible within a time limit)
- [ ] Add a countdown timer visible in the HUD
- [ ] Replace or augment the `goodCollected >= GOAL_COUNT` win condition with the timer
- [ ] Decide if the frenzy transition still uses collection count or switches to time-based
- [ ] Update HUD rendering to display remaining time prominently
- [ ] Keep score as a secondary metric (for leaderboard ranking) even if the timer is the primary mechanic

---

## 6. Entity Speed Variation

### Current State

All falling entities share the **same `fallSpeed`** regardless of type. The speed is set globally
in `GamePlayScene`:

- Standard mode: starts at `200f` px/s, ramps by `20f` every `15s`, capped at `300f`
- Frenzy mode: starts at `320f`, ramps by `20f` every `6s`, capped at `600f`

Every entity spawned via `entityFactory.createFallingEntity(type, spawnX, SPAWN_Y, fallSpeed)` gets
the same speed. There is no per-type speed modifier in `EntityType`.

### Affected Files

- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/entities/EntityType.java` (add speed modifier field)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/GamePlayScene.java` (apply per-type speed when spawning)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/entities/EntityFactory.java` (pass modified speed to `FallingComponent`)

### Requirements

- [ ] Add a `speedMultiplier` field to `EntityType` (e.g., `GOOD_BYTE = 1.0`, `PHISHING_HOOK = 1.3`, `GOLD_ENVELOPE = 0.8`)
- [ ] Multiply the global `fallSpeed` by each entity's `speedMultiplier` when spawning
- [ ] Tune multipliers so good entities are slightly slower (easier to catch) and bad entities are faster (harder to avoid), or vice versa for strategic gameplay
- [ ] Ensure difficulty ramping still works correctly with the per-type modifiers
- [ ] Consider adding slight random variation (e.g., +/- 10%) to make entity motion feel less uniform

---

## 7. Vertical Movement / Jumping

### Current State

Player movement is **horizontal only**. In `GamePlayScene.movePlayer()` (lines 277-291), only
`LEFT`/`RIGHT`/`A`/`D` keys are handled. The player's Y position is fixed at `WORLD_FLOOR = 110f`.

The engine's `MovementSystem` already supports full 2D physics (gravity, friction, Euler
integration), but `GamePlayScene` does not use `MovementManager` or `MovementSystem`. It manually
updates position with `tf.getPosition().x = newX`.

An `InputMap` action `"jump"` bound to `SPACE` exists in the engine but is unused in game scenes.

### Affected Files

- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/GamePlayScene.java` (movement logic, jump trigger)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/entities/EntityFactory.java` (player entity creation, add `VelocityComponent`)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/entities/CharacterType.java` (optional: per-character jump height)

### Requirements

- [ ] Add jump input (SPACE or W / UP arrow) to `movePlayer()` or a new `handleJump()` method
- [ ] Apply an upward velocity impulse when jump is triggered and the player is on the ground
- [ ] Apply gravity each frame to bring the player back down to `WORLD_FLOOR`
- [ ] Prevent double-jumping (only allow jump when `position.y <= WORLD_FLOOR` or similar ground check)
- [ ] Optionally vary jump height per character (e.g., Specter jumps higher, Guardian jumps lower)
- [ ] Ensure collision detection with falling entities still works while the player is airborne
- [ ] Update the HUD / How To Play scene to document the jump control

---

## 8. Asset Size Adjustments

### Current State

- **Player sprite**: `PLAYER_WIDTH = 64f`, `PLAYER_HEIGHT = 80f` (defined in `EntityFactory.java`)
- **Falling entities**: `ENTITY_SIZE = 44f` (uniform square for all entity types)
- **Character sprites**: `char-1.png` (Specter), `char-2.png` (Guardian), `char-3.png` (Cipher)
- **Entity sprites**: `laptop.png`, `shield.png`, `phone.png`, `fraud.png`, `hoax.png`, `virus.png`, `old-pc.png`, `magnifiying-glass.png`

All falling entities use the same 44x44 size. The world viewport is 1280x720, so at the
default resolution the player occupies roughly 5% of the screen width and entities are about 3.4%.

### Affected Files

- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/entities/EntityFactory.java` (`ENTITY_SIZE`, `PLAYER_WIDTH`, `PLAYER_HEIGHT`)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/entities/EntityType.java` (optional: per-type size)
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/game/scenes/GamePlayScene.java` (collision overlap calculations use these sizes)
- `assets/` folder (sprite image files)

### Requirements

- [ ] Review player sprite size relative to the game world — increase if the character feels too small
- [ ] Review falling entity size — consider making important entities (e.g., `GOLD_ENVELOPE`) slightly larger
- [ ] Consider per-type entity sizes in `EntityType` or `EntityFactory` instead of a single `ENTITY_SIZE`
- [ ] Update collision detection in `overlapsPlayer()` if entity sizes change
- [ ] Replace or resize sprite assets in `assets/` if the current images are too low-resolution for larger display sizes
- [ ] Test at multiple window sizes / fullscreen to confirm sprites look good at different scales

---

## 9. Card System

### Current State

No card system exists. The term "card" is currently used in `CharacterSelectScene` (character
selection cards) and `StartGamePromptScene` (dialog card), but these are UI layout elements, not
gameplay cards.

### Requirements

This feature needs a design specification. Key decisions to make:

- [ ] Define what "cards" represent (power-ups? abilities? collectible items? quiz buffs?)
- [ ] Define how cards are obtained (random drops? score milestones? post-quiz rewards?)
- [ ] Define when/how cards are played (automatic? player-activated? between rounds?)
- [ ] Define card effects (e.g., slow all entities, shield for X seconds, double points, extra life)
- [ ] Design the card UI (hand display, card selection overlay, visual card design)
- [ ] Define card inventory and limits (max hand size, one-time use vs. reusable)

### Implementation Scope (once designed)

- **New files**: `Card.java` (model), `CardManager.java` (deck/hand logic), `CardOverlay` (UI rendering)
- **Modified files**: `GamePlayScene.java` (integrate card triggers), `EntityFactory.java` (card drop entities), `GamePlayRenderer` (render card HUD)
- **New assets**: card artwork, card frame, card effect animations

---

## 10. Two Player Mode

### Current State

No multiplayer support exists. The game is entirely single-player. There is one player entity,
one set of input bindings (LEFT/RIGHT/A/D), and one leaderboard.

### Requirements

This feature needs a design specification. Key decisions to make:

- [ ] Define two-player mode type: split-screen, shared screen, or turn-based
- [ ] Define input mapping: Player 1 uses A/D, Player 2 uses LEFT/RIGHT (or controller support?)
- [ ] Define win/lose condition: cooperative (shared score) or competitive (separate scores)?
- [ ] Define screen layout: side-by-side, or both players on the same screen catching entities?
- [ ] Define how lives work: shared pool or independent?
- [ ] Define leaderboard integration: separate two-player leaderboard or combined?

### Implementation Scope (once designed)

- **New/modified files**: `GamePlayScene.java` (second player entity, input routing, scoring), `EntityFactory.java` (create second player), `CharacterSelectScene.java` (two-character selection flow)
- **Engine changes**: `InputManager` may need to support multiple `InputMap` instances simultaneously
- **New assets**: second player indicator, split-screen divider (if applicable)
- **Modified files**: `LeaderboardManager.java` / `LeaderboardScene.java` (two-player entries), `GameOverScene.java` (two-player results)

---

## Priority Matrix

| # | Requirement | Priority | Effort | Dependencies |
|---|-------------|----------|--------|--------------|
| 1 | Rename to CyberScouts | High | Low | None |
| 2 | Player Name Input | High | Medium | None |
| 3 | Bottom Border Fix | High | Low | None |
| 4 | UI Text Overflow | High | Low-Medium | None |
| 5 | Timer-Based Gameplay | High | Medium | Team decision on design |
| 6 | Entity Speed Variation | Medium | Low | None |
| 7 | Vertical Movement / Jumping | Medium | Medium | Req #3 (floor position) |
| 8 | Asset Size Adjustments | Medium | Low | Art assets |
| 9 | Card System | Low | High | Design specification needed |
| 10 | Two Player Mode | Low | High | Design specification needed |
