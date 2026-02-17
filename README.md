# INF1009 P2 Team 2 - Abstract Engine (Part 1)

Base package: `sg.edu.sit.inf1009.p2team2`

This repo contains the UML-aligned abstract engine skeleton and test harnesses for Part 1.

## Tech Stack
- Java 17
- Gradle Wrapper
- libGDX (`core` + `lwjgl3`)

## UML
<img width="8708" height="5769" alt="Abstract Engine UML Class Diagram (1)" src="https://github.com/user-attachments/assets/61f424c5-291f-4663-ad29-7fd62a83b54b" />

## Build Commands
```bash
./gradlew :core:compileJava :lwjgl3:compileJava
```

## Run Commands (Desktop)
- Default app start (menu):
```bash
./gradlew lwjgl3:run
```

- Start directly in simulation scene (`MainScene`):
```bash
./gradlew lwjgl3:run -Pscene=main
```

- Start Hong Yih runtime I/O test scene:
```bash
./gradlew lwjgl3:run -Pscene=io-test
```

- Start Hong Yih complete runtime I/O test scene:
```bash
./gradlew lwjgl3:run -Pscene=complete-io
```

## JUnit Test Commands
- Run all unit tests:
```bash
./gradlew :core:test
```

- Run one test class:
```bash
./gradlew :core:test --tests "sg.edu.sit.inf1009.p2team2.engine.config.ConfigManagerTest"
```

- Open HTML test report after running tests:
```bash
open core/build/reports/tests/test/index.html
```

## Test Coverage Guide
- `EntityTest` checks ECS entity add/get/remove/clear behavior.
- `EntityUmlApiTest` checks UML alias methods (`addComponent`, `getComponent`, etc.).
- `EntityManagerTest` checks creation IDs, filtering, and collection safety.
- `EntityManagerUmlApiTest` checks UML manager methods (`createEntity`, `getEntity`, string component query).
- `MovementSystemTest` checks integration math (velocity + position updates).
- `MovementManagerTest` checks manager-level movement pass over entities.
- `MovementManagerConfigTest` checks gravity/friction config APIs.
- `SceneManagerTest` checks push/pop lifecycle hooks and active scene behavior.
- `ConfigManagerTest` checks singleton, typed config access, observer callback flow.
- `ConfigVarTest` checks typed conversion/reset behavior.
- `ConfigLoaderTest` checks load/save config round-trip.
- `ConfigFileTest` checks file-layer reload/save behavior.
- `CollisionDetectorTest` checks overlap and non-overlap collision detection.
- `CollisionResolverTest` checks separation and velocity response path.
- `InputMapTest` checks action binding and keyboard-driven action states.
- `UiModelsTest` checks `Slider`, `Toggle`, `Button`, and `Score` model behavior.
- `InputOutputTestScene` + `CompleteIOTest` are runtime/manual scene tests (not JUnit).

What to look for:
- JUnit: `BUILD SUCCESSFUL` in terminal and all tests green in HTML report.
- Runtime tests: scene opens, controls respond, no exceptions in terminal.

## Project Structure (`|_` hierarchy + purpose)
```text
oop-p2team2
|_ build.gradle                         # Root Gradle config for all modules
|_ settings.gradle                      # Multi-module declaration (`core`, `lwjgl3`)
|_ assets/                              # Shared runtime assets used by libGDX
|_ core/
|  |_ build.gradle                      # Core module dependencies + JUnit setup
|  |_ src/main/java/sg/edu/sit/inf1009/p2team2/
|  |  |_ Main.java                      # App entry (startup scene selector + engine lifecycle)
|  |  |_ engine/
|  |     |_ core/
|  |     |  |_ EngineContext.java       # Central engine orchestrator and manager owner
|  |     |_ managers/
|  |     |  |_ SceneManager.java        # Scene stack management (push/pop/update/render)
|  |     |  |_ EntityManager.java       # Entity lifecycle + query/filter APIs
|  |     |  |_ MovementManager.java     # Movement pass over entity Transform/Velocity
|  |     |  |_ InputManager.java        # Keyboard/mouse + action-map routing
|  |     |  |_ OutputManager.java       # Display/Renderer/Audio composition
|  |     |_ scenes/
|  |     |  |_ Scene.java               # Abstract base scene interface
|  |     |  |_ MenuScene.java           # Main menu scene and scene navigation
|  |     |  |_ MainScene.java           # Empty simulation skeleton (logic deferred)
|  |     |  |_ SettingsScene.java       # Settings scene with load/save hooks
|  |     |  |_ LeaderboardScene.java    # Leaderboard scene skeleton
|  |     |_ ecs/
|  |     |  |_ ComponentAdapter.java    # UML marker interface for components
|  |     |  |_ Component.java           # Compatibility marker extending ComponentAdapter
|  |     |  |_ Entity.java              # ECS entity storing component map
|  |     |  |_ components/
|  |     |     |_ TransformComponent.java   # Position/rotation/scale data
|  |     |     |_ VelocityComponent.java    # Velocity/acceleration data
|  |     |     |_ RenderableComponent.java  # Render metadata (sprite/color/visibility)
|  |     |     |_ InputComponent.java       # Input binding metadata
|  |     |     |_ ColliderComponent.java    # Collision bounds/layer/trigger data
|  |     |     |_ TagComponent.java         # Generic tag/label component
|  |     |_ systems/
|  |     |  |_ MovementSystem.java      # Integration math for movement components
|  |     |_ input/
|  |     |  |_ Keyboard.java            # Frame-based key state tracker
|  |     |  |_ Mouse.java               # Frame-based mouse state tracker
|  |     |  |_ InputMap.java            # Action-to-key bindings and action queries
|  |     |_ output/
|  |     |  |_ Display.java             # Window/fullscreen abstraction
|  |     |  |_ Renderer.java            # Drawing primitives + sprite/text/shape APIs
|  |     |  |_ Audio.java               # Sound/music load/play/volume APIs
|  |     |  |_ SoundBuffer.java         # Sound wrapper model
|  |     |  |_ MusicTrack.java          # Music wrapper model
|  |     |_ collision/
|  |     |  |_ Shape.java               # Abstract collision shape base
|  |     |  |_ ShapeType.java           # Shape enum (`CIRCLE`, `RECTANGLE`)
|  |     |  |_ Circle.java              # Circle collision primitive
|  |     |  |_ Rectangle.java           # Rectangle collision primitive
|  |     |  |_ Collision.java           # Collision event data between entities
|  |     |  |_ CollisionDetector.java   # Pairwise collision detection
|  |     |  |_ CollisionResolver.java   # Collision response/resolution operations
|  |     |  |_ CollisionManager.java    # Detect + resolve orchestration per frame
|  |     |_ config/
|  |     |  |_ ConfigManager.java       # Singleton config control facade
|  |     |  |_ ConfigRegistry.java      # Key/value config storage
|  |     |  |_ ConfigLoader.java        # Config load/save adapter (properties-based)
|  |     |  |_ ConfigDispatcher.java    # Observer dispatch for config changes
|  |     |  |_ ConfigListener.java      # Config change observer interface
|  |     |  |_ ConfigVar.java           # Typed config value wrapper
|  |     |  |_ ConfigurationManager.java# Backward-compatibility wrapper class
|  |     |  |_ ConfigFile.java          # File-layer model wrapping reload/save
|  |     |_ ui/
|  |        |_ Button.java              # UI button model
|  |        |_ Slider.java              # UI slider model
|  |        |_ Toggle.java              # UI toggle model
|  |        |_ Score.java               # Score value model
|  |_ src/test/java/sg/edu/sit/inf1009/p2team2/engine/
|     |_ ecs/
|     |  |_ EntityTest.java             # Unit tests for Entity component operations
|     |  |_ EntityUmlApiTest.java       # Unit tests for UML alias Entity API
|     |_ managers/
|     |  |_ EntityManagerTest.java      # Unit tests for EntityManager behavior
|     |  |_ EntityManagerUmlApiTest.java# Unit tests for UML alias manager API
|     |  |_ MovementManagerTest.java    # Unit tests for movement manager pass
|     |  |_ MovementManagerConfigTest.java # Unit tests for gravity/friction config API
|     |  |_ SceneManagerTest.java       # Unit tests for scene lifecycle stack behavior
|     |_ systems/
|     |  |_ MovementSystemTest.java     # Unit tests for integration math
|     |_ config/
|     |  |_ ConfigManagerTest.java      # Unit tests for singleton/config observer flow
|     |  |_ ConfigVarTest.java          # Unit tests for typed config value conversion
|     |  |_ ConfigLoaderTest.java       # Unit tests for config persistence adapter
|     |  |_ ConfigFileTest.java         # Unit tests for config file layer
|     |_ collision/
|     |  |_ CollisionDetectorTest.java  # Unit tests for AABB overlap detection
|     |  |_ CollisionResolverTest.java  # Unit tests for collision response
|     |_ input/
|     |  |_ InputMapTest.java           # Unit tests for action-binding logic
|     |_ ui/
|     |  |_ UiModelsTest.java           # Unit tests for UI model classes
|     |_ scenes/tests/
|        |_ InputOutputTestScene.java   # Runtime/manual test scene (I/O)
|        |_ CompleteIOTest.java         # Runtime/manual comprehensive I/O scene
|_ lwjgl3/
|  |_ build.gradle                      # Desktop run packaging and scene-property pass-through
|  |_ src/main/java/sg/edu/sit/inf1009/p2team2/lwjgl3/
|     |_ Lwjgl3Launcher.java            # Desktop entrypoint for libGDX
|     |_ StartupHelper.java             # Mac/JVM startup helper
```
