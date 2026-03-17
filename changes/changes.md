# CyberScouts - Changes & Improvements

All updates implemented across the codebase, organised by feature area.

---

## 1. Branding: Renamed to CyberScouts

Replaced all references to "Silicon Sentinel" with "CyberScouts" throughout the project.

| File | What changed |
|------|-------------|
| `GameMenuScene.java` | Title text changed from `"SILICON SENTINEL"` to `"CYBERSCOUTS"` |
| `LeaderboardScene.java` | Subtitle changed to `"Top Scores - CyberScouts"` |
| `EntityType.java` | Javadoc header updated |
| `QuizBank.java` | Javadoc header updated |
| `QuizManager.java` | Javadoc header updated |
| `GamePlayScene.java` | Javadoc header updated |

---

## 2. Player Name Input for Leaderboard

Previously all leaderboard entries were saved as `"PLAYER"`. A new name-entry screen was added so players can set a custom display name.

### New file: `NameEntryScene.java`
- Full on-screen keyboard UI for entering a player name
- Blinking cursor and text buffer display
- 12-character max length with input validation
- Shown automatically before character selection if no name is saved

### Changes to `LeaderboardManager.java`
- Added `playerName` field with `getPlayerName()`, `hasPlayerName()`, `setPlayerName()` methods
- Name is persisted via libGDX `Preferences` (key: `"player_name"`)
- `MAX_NAME_LENGTH = 12` caps the stored name

### Changes to `GameMenuScene.java`
- "Start Game" button now routes through `NameEntryScene` if no player name is set

### Changes to `GameOverScene.java`
- `addEntry()` now uses `leaderboard.getPlayerName()` instead of hardcoded `"PLAYER"`

---

## 3. Bottom Border Fix

Reduced the gap between the player character and the bottom of the screen.

| Constant | Before | After | File |
|----------|--------|-------|------|
| `WORLD_FLOOR` | `110f` | `30f` | `GamePlayScene.java` |

The player now renders much closer to the bottom edge while leaving room for the HUD controls hint at `y = 12f`.

---

## 4. UI Text Overflow Fixes

Fixed text clipping outside of UI card boundaries in two scenes.

### `CharacterSelectScene.java`
- Increased card dimensions from `240 x 360` to `300 x 380` (`CARD_W`, `CARD_H`)
- Adjusted card spacing logic in `cardRect()` to accommodate wider cards

### `StartGamePromptScene.java`
- Increased dialog card from `440 x 280` to `480 x 310` (`cw`, `ch`)
- Repositioned title and "Last played" text to render inside the card

---

## 5. Timer-Based Gameplay

Replaced the collection-count-based win/frenzy system with a countdown timer.

### New constants in `GamePlayScene.java`
| Constant | Value | Purpose |
|----------|-------|---------|
| `STANDARD_DURATION` | `60f` | Seconds of standard-mode play |
| `FRENZY_DURATION` | `8f` | Seconds of frenzy mode |
| `FRENZY_ORB_INTERVAL` | `20f` | Seconds between frenzy orb spawns |

### How it works
- **PLAYING mode**: A 60-second `roundTimer` counts down. When it hits 0, the player **wins**.
- **Frenzy mode**: Triggered by catching a **Frenzy Orb** (not by timer). Lasts 8 seconds with the `roundTimer` frozen. When frenzy expires, play returns to PLAYING mode and the timer resumes.
- `goodCollected` and `GOAL_COUNT` are now cosmetic counters only; they no longer trigger state transitions.

### HUD changes
- Timer displayed as `"TIME  Xs"` in PLAYING mode (turns red when <= 10s)
- Frenzy timer displayed as `"FRENZY  Xs"` in orange during frenzy

---

## 6. Entity Speed Variation

Each entity type now has its own speed multiplier and display size instead of using uniform values.

### `EntityType.java` - new fields added to every enum constant
| Field | Type | Purpose |
|-------|------|---------|
| `speedMultiplier` | `float` | Multiplied with global `fallSpeed` when spawning |
| `displaySize` | `float` | Per-type size in pixels (replaces uniform `ENTITY_SIZE`) |

### Per-type values
| Entity | Speed Multiplier | Display Size | Notes |
|--------|-----------------|-------------|-------|
| `GOOD_BYTE` | 0.85 | 54px | Slower - easier to catch |
| `SAFE_EMAIL` | 0.90 | 54px | Slightly slower |
| `GOLD_ENVELOPE` | 0.75 | 62px | Slowest & largest good entity |
| `PHISHING_HOOK` | 1.20 | 50px | Fast threat |
| `RANSOMWARE_LOCK` | 1.10 | 56px | Medium-fast threat |
| `MALWARE_SWARM` | 1.30 | 48px | Fastest standard threat |
| `ROOTKIT` | 1.35 | 50px | Frenzy-only, very fast |
| `SPYWARE` | 1.25 | 52px | Frenzy-only |
| `FRENZY_ORB` | 0.60 | 68px | Slow & large - easy to spot and catch |

### `GamePlayScene.java`
- `spawnRandomEntity()` now multiplies `fallSpeed * type.getSpeedMultiplier()` with +/- 10% random variation for organic feel
- `overlapsPlayer()` and entity drawing use `TransformComponent.getScale()` for width/height instead of static `ENTITY_SIZE`

---

## 7. Vertical Movement / Jumping

Added jump mechanics so the player can avoid threats or reach higher entities.

### Physics constants in `GamePlayScene.java`
| Constant | Value | Purpose |
|----------|-------|---------|
| `GRAVITY` | `-900f` | Downward acceleration (px/s^2) |

### Per-character jump strength in `CharacterType.java`
| Character | Jump Strength | Notes |
|-----------|--------------|-------|
| Specter | `420f` | High jump, complements speed |
| Guardian | `350f` | Lower jump, tankier build |
| Cipher | `380f` | Mid jump, glass-cannon scorer |

### `movePlayer()` changes
- **Jump trigger**: `SPACE`, `W`, or `UP` arrow (only when on ground)
- **Gravity**: Applied every frame while airborne; player falls back to `WORLD_FLOOR`
- **No double-jump**: `playerOnGround` flag prevents jumping while already airborne
- Collision detection continues to work while the player is mid-air

### HUD update
- Controls hint now reads: `"A/D Move  |  SPACE Jump  |  ESC Quit"`

---

## 8. Asset Size Adjustments

Increased player and entity sizes for better visibility at the 1280x720 viewport.

| Constant | Before | After | File |
|----------|--------|-------|------|
| `ENTITY_SIZE` | `44f` | `54f` | `EntityFactory.java` |
| `PLAYER_WIDTH` | `64f` | `80f` | `EntityFactory.java` |
| `PLAYER_HEIGHT` | `80f` | `100f` | `EntityFactory.java` |

Each `EntityType` also has its own `displaySize` (see section 6), so individual entities can be larger or smaller than the base `ENTITY_SIZE`. The `createFallingEntity()` method in `EntityFactory` now uses `type.getDisplaySize()` for scaling.

---

## 9. Frenzy Orb - New Entity

A special entity that triggers Frenzy Mode when caught.

### `EntityType.FRENZY_ORB`
- Color: bright magenta/purple `(0.90, 0.20, 0.95)`
- Speed multiplier: `0.60` (slow - easy to catch deliberately)
- Display size: `68px` (larger than all other entities)
- Not bad, no quiz trigger, no score value

### Spawning behaviour
- Spawns every 20 seconds during PLAYING mode via `frenzyOrbTimer`
- Only one orb on screen at a time (`frenzyOrbSpawned` flag)
- After catching an orb and completing frenzy, the timer resets for the next orb

### Rendering
- Drawn as a layered glowing circle (outer purple ring, bright inner, white outline)
- Visually distinct from all sprite-based entities

### Collision
- Catching the orb immediately triggers `TRANSITION_TO_FRENZY` (3-second countdown)
- No score added, no quiz triggered

---

## 10. Frenzy Mode Rework

Frenzy is now a timed bonus phase triggered by catching the Frenzy Orb, not by a round timer.

### Before
- Frenzy triggered when round timer (60s) expired
- Frenzy lasted 30 seconds
- Winning required surviving frenzy

### After
- Frenzy triggered **only** by catching a `FRENZY_ORB`
- Frenzy lasts **8 seconds** (`FRENZY_DURATION = 8f`)
- `roundTimer` is **frozen** during frenzy (only decremented in PLAYING branch)
- When frenzy expires, game **returns to PLAYING mode** with pre-frenzy speed/interval restored
- **WIN** condition: `roundTimer` hits 0 while in PLAYING mode
- Multiple frenzy phases possible in a single game

### State flow
```
PLAYING  -->  catch FRENZY_ORB  -->  TRANSITION_TO_FRENZY (3s)  -->  FRENZY (8s)  -->  PLAYING
PLAYING  -->  roundTimer hits 0  -->  WIN
PLAYING / FRENZY  -->  0 lives  -->  GAME_OVER
```

---

## 11. How-To-Play Pre-Intro Screen

The How-To-Play screen now appears automatically before the player's first game.

### `LeaderboardManager.java`
- Added `seenTutorial` boolean field with `hasSeenTutorial()` and `setSeenTutorial()` methods
- Persisted via Preferences (key: `"seen_tutorial"`)

### `CharacterSelectScene.java` - `startGame()`
- If `!leaderboard.hasSeenTutorial()`: pushes `GamePlayScene`, then pushes `HowToPlayScene` on top
- Sets `seenTutorial` flag so the tutorial only shows once

### `StartGamePromptScene.java` - `activate(0)` (Continue path)
- Same first-time tutorial check as `CharacterSelectScene`

### `HowToPlayScene.java` - updated text
- Objective: *"Survive until the timer hits 0 to win!"* / *"Catch a Frenzy Orb to freeze the timer!"*
- Frenzy section: *"Catch a glowing Frenzy Orb to trigger!"* / *"Lasts 8 seconds - timer is frozen."* / *"Faster entities + Rootkit & Spyware."*
- Existing menu "How to Play" button still works for revisiting

---

## 12. Card System (Scaffolding)

Basic model and manager classes have been created as placeholders. Full implementation requires design specification.

| New file | Purpose |
|----------|---------|
| `Card.java` | Card model with `CardType` enum and basic properties |
| `CardManager.java` | Manages player's card hand and active effects |

---

## 13. Two Player Mode (Scaffolding)

Basic player-slot class has been created as a placeholder. Full implementation requires design specification.

| New file | Purpose |
|----------|---------|
| `PlayerSlot.java` | Per-player state management (key bindings, entities, score) |

---

## Files Modified (Summary)

| File | Changes |
|------|---------|
| `GameMenuScene.java` | Branding, name entry routing |
| `LeaderboardManager.java` | Player name, tutorial flag, preferences persistence |
| `LeaderboardScene.java` | Branding |
| `EntityType.java` | Speed multiplier, display size, `FRENZY_ORB`, branding |
| `EntityFactory.java` | Increased sizes, per-type display size |
| `CharacterType.java` | Jump strength field |
| `GamePlayScene.java` | Timer gameplay, jump physics, frenzy orb, speed variation, asset sizes, HUD, frenzy rework |
| `GameOverScene.java` | Player name from leaderboard |
| `CharacterSelectScene.java` | Card size fix, tutorial check |
| `StartGamePromptScene.java` | Card size fix, text position, tutorial check |
| `HowToPlayScene.java` | Updated objective, frenzy, and control text |
| `QuizBank.java` | Branding |
| `QuizManager.java` | Branding |

## New Files

| File | Purpose |
|------|---------|
| `NameEntryScene.java` | Player name input screen |
| `Card.java` | Card system model (scaffold) |
| `CardManager.java` | Card hand management (scaffold) |
| `PlayerSlot.java` | Two-player state management (scaffold) |
