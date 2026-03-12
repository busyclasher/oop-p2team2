# Changes Summary

## Modified Files

### `Main.java`
- Replaced `MenuScene` with `GameMenuScene` as the default startup scene
- Added `LeaderboardManager` instance, initialized at startup
- Added `"engine-menu"` scene key to still reach the original `MenuScene` if needed
- Removed unused `lastDeltaTime` field

---

## New Files Added

### `game/components/`

Added `FallingComponent.java` - ECS component tracking fall speed and active state for dropping entities
Added `GameEntityComponent.java` - ECS component classifying entities (good/bad, quiz trigger, score value, collected state)
Added `HealthComponent.java` - ECS component tracking the player's remaining lives

### `game/entities/`

Added `EntityType.java` - Enum of all entity types — good items (`GOOD_BYTE`, `SAFE_EMAIL`, `GOLD_ENVELOPE`), bad items (`PHISHING_HOOK`, `RANSOMWARE_LOCK`, `MALWARE_SWARM`, etc.), and `PLAYER`
Added `EntityFactory.java` - Factory that builds fully-assembled `Entity` objects with the right components attached

### `game/leaderboard/`

Added `LeaderboardEntry.java` - Immutable record holding a player name, score, and timestamp; sortable by score
Added `LeaderboardManager.java` - In-memory top-scores list; capped and sorted in descending order

### `game/quiz/`

Added `QuizQuestion.java` - Immutable 4-option MCQ with answer-checking logic
Added `QuizBank.java` - Pool of 15 cybersecurity-themed questions; cycles through all before repeating
Added `QuizManager.java` - Manages quiz state (IDLE/ACTIVE), triggers on collision, returns `CORRECT`/`WRONG`
Added `QuizResult.java` - Enum: `CORRECT` or `WRONG`

### `game/scenes/`

Added `GameMenuScene.java` - Main menu with Start Game, Leaderboard, and Exit options
Added `GamePlayScene.java` - Core gameplay - falling entity spawning, player movement, collision, scoring, quiz triggers, Standard/Frenzy modes (when yall have sprites and shi just lmk or u can add it to this file after u add to assets, i type halfway i lazy alr just follow all the cases are the same) (audio is line 266 273)
Added `GameOverScene.java` - End screen showing final score; auto-saves to leaderboard; offers Retry/Menu/Leaderboard
Added `LeaderboardScene.java` - Displays ranked top scores with gold/silver/bronze highlights; ESC to menu, R to restart

---

## Overview

The main addition is a full game layer built on top of the existing engine — a cybersecurity-themed catching game where the player collects good items and avoids bad ones. Key features include:

- **Two play modes** - Standard and Frenzy
- **Quiz mechanic** - triggered on certain entity collisions, with 15 cybersecurity-themed questions
- **Leaderboard system** - in-memory top scores saved at end of each game
- **Health/lives system** - player loses lives on bad entity collisions
