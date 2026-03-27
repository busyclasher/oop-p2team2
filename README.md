# INF1009 P2 Team 2 - Abstract Engine + CyberScouts Game

Base package: `sg.edu.sit.inf1009.p2team2`

This repository contains both parts of the project in one submission-ready codebase:
- a reusable abstract engine in `engine/*`
- an engine demonstration layer from Part 1 in `demo/*`
- the final CyberScouts game implementation in `game/*`

The playable game is the default startup path. The demo scenes are still included on purpose because they showcase the abstract engine systems independently from the final game flow.

## Tech Stack
- Java 17
- Gradle Wrapper
- libGDX (`core` + `lwjgl3`)

## High-Level Architecture
The codebase is organized into three clear layers:

### 1. `engine/*` - Abstract Engine
The engine layer contains reusable systems and infrastructure:
- scene stack and lifecycle orchestration
- entity/component model
- movement and collision systems
- input/output managers
- renderer, display, and audio services
- configuration management
- storage abstraction for persistence-backed features

### 2. `demo/*` - Part 1 Engine Demonstration
The demo package contains standalone scenes used to demonstrate the engine independently:
- engine demo menu
- engine simulation scene
- engine demo settings scene

These remain in the repository because they help show the abstract engine features directly.

### 3. `game/*` - CyberScouts
The game layer contains the final playable submission:
- game-specific components and entity types
- gameplay scenes and UI flow
- quiz system
- leaderboard system
- save/resume run support
- game-specific audio and visual theme utilities

## Main Features
### Engine Features
- ECS-style entity architecture with reusable engine components
- scene-based architecture with input/render/resource hooks
- movement and collision subsystems
- configuration system with listener support
- storage abstraction for persisted data
- reusable renderer, audio, and display managers

### CyberScouts Features
- multiple playable characters
- score-based vertical-drop gameplay
- quiz rewards and penalties
- frenzy mode with dedicated visuals and music
- buff/upgrade selection
- leaderboard persistence
- save-and-resume run flow from the pause menu
- responsive settings with audio, resolution, and fullscreen support

## UML
![Screenshot 2026-03-27 163246](https://github.com/user-attachments/assets/bf9bb437-b3f0-4df4-b9a3-31f4219c4f6e)

## Project Structure
```text
oop-p2team2
|_ assets/                                            # Shared runtime assets (images, audio, fonts, config)
|_ core/
|  |_ src/main/java/sg/edu/sit/inf1009/p2team2/
|  |  |_ Main.java                                     # Application entrypoint
|  |  |_ engine/
|  |  |  |_ core/                                      # EngineContext + lifecycle orchestration
|  |  |  |_ entity/                                    # Entity, ComponentAdapter, EntityManager
|  |  |  |_ entity/components/                         # Transform, Velocity, Renderable, Input, Collider
|  |  |  |_ movement/                                  # MovementManager, MovementSystem
|  |  |  |_ collision/                                 # CollisionManager, Detector, Resolver, shapes
|  |  |  |_ io/
|  |  |  |  |_ input/                                  # Keyboard, Mouse, InputMap, engine Keys wrapper
|  |  |  |  |_ output/                                 # Renderer, Display, Audio, color wrapper
|  |  |  |  |_ storage/                                # StorageProvider abstraction + GDX implementation
|  |  |  |  |_ ui/                                     # Reusable UI models
|  |  |  |_ scene/                                     # Scene base classes and scene services
|  |  |  |_ config/                                    # ConfigManager, config vars, listeners, loader
|  |  |_ demo/                                         # Part 1 engine demonstration scenes
|  |  |  |_ MenuScene / MainScene / SettingsScene
|  |  |  |_ corresponding input handlers and renderers
|  |  |_ game/
|  |     |_ audio/                                     # Game audio IDs and load/play helpers
|  |     |_ components/                                # Game-specific ECS components
|  |     |_ entities/                                  # EntityFactory, CharacterType, EntityType, BuffType
|  |     |_ leaderboard/                               # Leaderboard persistence and entries
|  |     |_ quiz/                                      # Quiz domain and management
|  |     |_ save/                                      # Saved-run persistence
|  |     |_ scenes/                                    # Final playable scene flow
|  |     |_ ui/                                        # Game visual theme utilities
|  |_ src/test/java/sg/edu/sit/inf1009/p2team2/
|     |_ engine/                                       # Engine-focused unit and smoke tests
|     |_ game/                                         # Game component tests
|_ lwjgl3/
|  |_ src/main/java/sg/edu/sit/inf1009/p2team2/lwjgl3/
|     |_ Lwjgl3Launcher.java
|     |_ StartupHelper.java
|_ gradle/                                             # Gradle wrapper support
|_ build.gradle
|_ settings.gradle
|_ README.md
```

## Startup Modes
### Default Game Startup
Runs the final CyberScouts menu flow:
```bash
./gradlew lwjgl3:run
```

### Engine Demo Startup
These startup modes are still available for showcasing the abstract engine separately.

Run the engine demo main scene:
```bash
./gradlew lwjgl3:run -Pscene=main
```

Run the engine demo menu scene:
```bash
./gradlew lwjgl3:run -Pscene=engine-menu
```

### Runtime Test Scenes
Run the input/output runtime test scene:
```bash
./gradlew lwjgl3:run -Pscene=io-test
```

Run the complete I/O runtime test scene:
```bash
./gradlew lwjgl3:run -Pscene=complete-io
```

Note: the desktop launcher maps `-Pscene=...` into the `engine.scene` system property at runtime.

## CyberScouts Game Flow

![photo_2026-03-27_16-04-15](https://github.com/user-attachments/assets/c11062b9-1398-4d90-ae41-24190a3ebf03)

![photo_2026-03-27_16-04-15 (2)](https://github.com/user-attachments/assets/6d51ad1d-a18d-435d-adb0-633672aeb8fe)

The final playable flow is:
1. `GameMenuScene`
2. `CharacterSelectScene` or `StartGamePromptScene` if a saved run exists
3. `HowToPlayScene` tutorial for a fresh new game
4. `GamePlayScene`
5. `PauseScene`, `SettingsScene`, `LeaderboardScene`, or `GameOverScene` depending on flow

## Save and Resume Flow
CyberScouts supports game-layer run persistence.

How it works:
- if the player exits to menu from `PauseScene`, the current run is saved
- pressing `Start Game` later will open a continue prompt if a saved run exists
- `Continue Run` restores the in-progress session
- `New Game` clears the old save and starts fresh
- winning or losing clears the saved run automatically

This persistence is implemented in the game layer using the engine storage abstraction.

## Controls
### Game Menu
- `W/S` or `Up/Down`: move selection
- `Enter` / `Space`: confirm
- mouse hover + click: supported

### Character Select
- `A/D` or `Left/Right`: switch character
- `Enter` / `Space`: confirm
- `Esc`: back
- mouse click: supported

### Gameplay
- `A/D` or `Left/Right`: move
- `Space` / `W` / `Up`: jump
- `Esc`: pause
- during quiz: `1`-`4` or mouse click to answer

### Pause Menu
- resume game
- open settings
- exit to menu and save current run

### Settings
- `Up/Down`: choose row
- `Left/Right`: adjust values
- `Enter` / `Space`: toggle/select
- mouse drag: sliders
- mouse click: resolution/fullscreen/save controls
- `Esc`: quick save and return

### How To Play
- `Left` / `A` / `Esc`: previous page or close
- `Right` / `D` / `Enter` / `Space`: next page or finish
- mouse click: supported for navigation buttons

## Build Commands
Compile the project:
```bash
./gradlew :core:compileJava :lwjgl3:compileJava
```

Run all core tests:
```bash
./gradlew :core:test
```

Run the default desktop game:
```bash
./gradlew lwjgl3:run
```

## Test Commands
Run all core tests:
```bash
./gradlew :core:test
```

Run a single test class:
```bash
./gradlew :core:test --tests "sg.edu.sit.inf1009.p2team2.engine.scenes.tests.SceneSmokeTest"
```

Open the HTML test report:
```bash
open core/build/reports/tests/test/index.html
```

