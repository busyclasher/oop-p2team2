# INF1009 P2 Team 2 - Abstract Engine + Game Layer

Base package: `sg.edu.sit.inf1009.p2team2`

This repository now contains:
- a reusable abstract engine (`engine/*`), and
- the current game implementation for Silicon Sentinel (`game/*`).

## Tools Used
- Java 17
- Gradle Wrapper
- libGDX (`core` + `lwjgl3`)

## Project Structure
```text
oop-p2team2
|_ assets/                                      # Shared runtime assets (images/audio/config)
|_ core/
|  |_ src/main/java/sg/edu/sit/inf1009/p2team2/
|  |  |_ Main.java                               # App entrypoint; chooses startup scene
|  |  |_ engine/
|  |  |  |_ core/
|  |  |  |  |_ EngineContext.java                # Owns manager instances + lifecycle orchestration
|  |  |  |_ entity/
|  |  |  |  |_ EntityManager.java                # Entity lifecycle/query APIs
|  |  |  |  |_ Entity.java
|  |  |  |  |_ ComponentAdapter.java
|  |  |  |  |_ components/                       # Transform/Velocity/Renderable/Input/Collider components
|  |  |  |_ movement/
|  |  |  |  |_ MovementManager.java
|  |  |  |  |_ MovementSystem.java
|  |  |  |_ collision/
|  |  |  |  |_ CollisionManager.java
|  |  |  |  |_ CollisionDetector.java
|  |  |  |  |_ CollisionResolver.java
|  |  |  |  |_ Collision.java
|  |  |  |  |_ Shape.java / Rectangle.java / Circle.java / ShapeType.java
|  |  |  |_ io/
|  |  |  |  |_ InputManager.java
|  |  |  |  |_ OutputManager.java
|  |  |  |  |_ input/                            # Keyboard, Mouse, InputMap
|  |  |  |  |_ output/                           # Display, Renderer, Audio, SoundBuffer, MusicTrack
|  |  |  |  |_ ui/                               # Button, Slider, Toggle, Score
|  |  |  |_ scene/
|  |  |  |  |_ SceneManager.java
|  |  |  |  |_ Scene.java
|  |  |  |  |_ InputHandler.java
|  |  |  |  |_ SceneRenderer.java
|  |  |  |  |_ RenderLayer.java
|  |  |  |  |_ ResourceLoader.java
|  |  |  |  |_ MenuScene.java                    # Engine demo menu scene
|  |  |  |  |_ MainScene.java                    # Engine simulation demo scene
|  |  |  |  |_ SettingsScene.java                # Engine demo settings scene
|  |  |  |_ config/
|  |  |     |_ ConfigManager.java                # Singleton config facade
|  |  |     |_ ConfigRegistry.java
|  |  |     |_ ConfigLoader.java + interfaces/formats/parsers/listeners
|  |  |     |_ ConfigKeys.java / SimulationConfigKeys.java / ConfigKey.java / ConfigVar.java
|  |  |_ game/
|  |     |_ components/                          # Game-specific ECS components
|  |     |  |_ GameEntityComponent.java
|  |     |  |_ FallingComponent.java
|  |     |  |_ HealthComponent.java
|  |     |_ entities/                            # Game entity factory + enums
|  |     |  |_ EntityFactory.java
|  |     |  |_ EntityType.java
|  |     |  |_ CharacterType.java
|  |     |_ quiz/                                # Quiz domain model + manager
|  |     |  |_ QuizManager.java
|  |     |  |_ QuizBank.java
|  |     |  |_ QuizQuestion.java
|  |     |  |_ QuizResult.java
|  |     |_ leaderboard/
|  |     |  |_ LeaderboardManager.java
|  |     |  |_ LeaderboardEntry.java
|  |     |_ scenes/                              # Playable game flow scenes
|  |        |_ GameMenuScene.java
|  |        |_ StartGamePromptScene.java
|  |        |_ CharacterSelectScene.java
|  |        |_ GamePlayScene.java
|  |        |_ PauseScene.java
|  |        |_ SettingsScene.java                # Game-layer settings scene
|  |        |_ LeaderboardScene.java
|  |        |_ GameOverScene.java
|  |        |_ HowToPlayScene.java
|  |_ src/test/java/sg/edu/sit/inf1009/p2team2/engine/
|     |_ collision/                              # Collision unit tests
|     |_ config/                                 # Config unit tests
|     |_ ecs/                                    # Entity unit tests
|     |_ input/                                  # Input map unit tests
|     |_ managers/                               # Manager behavior tests
|     |_ scenes/tests/                           # Scene smoke + runtime/manual scene tests
|     |_ systems/                                # Movement system tests
|     |_ ui/                                     # UI model tests
|_ lwjgl3/
|  |_ src/main/java/sg/edu/sit/inf1009/p2team2/lwjgl3/
|     |_ Lwjgl3Launcher.java
|     |_ StartupHelper.java
```

## UML
<img width="8829" height="6116" alt="Abstract Engine UML Class Diagram (5)" src="https://github.com/user-attachments/assets/bb1c1d19-ddba-42ed-a65e-3c4731d2452f" />

## Build Commands
```bash
./gradlew :core:compileJava :lwjgl3:compileJava
```

## Run Commands (Desktop)
- Default startup (game menu flow):
```bash
./gradlew lwjgl3:run
```

- Start engine demo main scene directly:
```bash
./gradlew lwjgl3:run -Pscene=main
```

- Start engine demo menu scene directly:
```bash
./gradlew lwjgl3:run -Pscene=engine-menu
```

- Start runtime I/O scene test:
```bash
./gradlew lwjgl3:run -Pscene=io-test
```

- Start runtime complete I/O scene test:
```bash
./gradlew lwjgl3:run -Pscene=complete-io
```

## Game Controls (Current Flow)
- `GameMenuScene`: Arrow/WASD or mouse hover to select, `Enter` or click to open scene.
- `CharacterSelectScene`: select character, `Enter`/click confirm, `Esc` back.
- `GamePlayScene`:
  - `A` / `D` or Arrow Left/Right: move player
  - `Esc`: open pause menu
  - During quiz: `1`-`4` or mouse click to submit answer
- `PauseScene`: Resume, Settings, or Exit to Menu.
- `SettingsScene` (game layer): up/down select row, left/right adjust, mouse drag sliders, `Esc` save and return.

## JUnit Test Commands
- Run all core tests:
```bash
./gradlew :core:test
```

- Run one test class:
```bash
./gradlew :core:test --tests "sg.edu.sit.inf1009.p2team2.engine.scenes.tests.SceneSmokeTest"
```

- Open HTML report:
```bash
open core/build/reports/tests/test/index.html
```

What to look for:
- terminal shows `BUILD SUCCESSFUL`
- all tests green in the HTML report
- runtime scene transitions are responsive with no terminal exceptions
