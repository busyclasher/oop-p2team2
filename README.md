# INF1009 P2 Team 2 - Abstract Engine (Part 1)

Base package: `sg.edu.sit.inf1009.p2team2`

This repository contains the UML-aligned abstract engine, simulation scenes, and unit/runtime tests for Part 1.

## Tools Used
- Java 17
- Gradle Wrapper
- libGDX (`core` + `lwjgl3`)

## Project Structure
```text
oop-p2team2
|_ build.gradle
|_ settings.gradle
|_ assets/
|  |_ background_menu.png             # Menu scene background
|  |_ mainscene.png                   # Main simulation background
|  |_ setting.png                     # Settings scene background
|  |_ engine.properties               # Default/persisted engine config
|_ core/
|  |_ build.gradle
|  |_ src/main/java/sg/edu/sit/inf1009/p2team2/
|  |  |_ Main.java                    # Application entry; resolves startup scene
|  |  |_ engine/
|  |     |_ core/
|  |     |  |_ EngineContext.java     # Owns and coordinates managers
|  |     |_ managers/
|  |     |  |_ SceneManager.java
|  |     |  |_ EntityManager.java
|  |     |  |_ MovementManager.java
|  |     |  |_ InputManager.java
|  |     |  |_ OutputManager.java
|  |     |_ scenes/
|  |     |  |_ Scene.java
|  |     |  |_ MenuScene.java
|  |     |  |_ MainScene.java
|  |     |  |_ SettingsScene.java
|  |     |  |_ SimulationConfigKeys.java
|  |     |_ ecs/
|  |     |  |_ ComponentAdapter.java
|  |     |  |_ Entity.java
|  |     |  |_ components/
|  |     |     |_ TransformComponent.java
|  |     |     |_ VelocityComponent.java
|  |     |     |_ RenderableComponent.java
|  |     |     |_ InputComponent.java
|  |     |     |_ ColliderComponent.java
|  |     |_ systems/
|  |     |  |_ MovementSystem.java
|  |     |_ collision/
|  |     |  |_ Shape.java
|  |     |  |_ ShapeType.java
|  |     |  |_ Rectangle.java
|  |     |  |_ Circle.java
|  |     |  |_ Collision.java
|  |     |  |_ CollisionDetector.java
|  |     |  |_ CollisionResolver.java
|  |     |  |_ CollisionManager.java
|  |     |_ config/
|  |     |  |_ ConfigManager.java
|  |     |  |_ ConfigRegistry.java
|  |     |  |_ ConfigLoader.java
|  |     |  |_ ConfigDispatcher.java
|  |     |  |_ ConfigListener.java
|  |     |  |_ ConfigVar.java
|  |     |  |_ ConfigKey.java
|  |     |  |_ ConfigKeys.java
|  |     |  |_ ConfigValueParser.java
|  |     |  |_ AudioConfigListener.java
|  |     |  |_ DisplayConfigListener.java
|  |     |  |_ IConfigStore.java
|  |     |  |_ IConfigLoader.java
|  |     |  |_ IConfigDispatcher.java
|  |     |  |_ IConfigFormat.java
|  |     |  |_ JsonConfigFormat.java
|  |     |  |_ PropertiesConfigFormat.java
|  |     |_ input/
|  |     |  |_ Keyboard.java
|  |     |  |_ Mouse.java
|  |     |  |_ InputMap.java
|  |     |_ output/
|  |     |  |_ Display.java
|  |     |  |_ Renderer.java
|  |     |  |_ Audio.java
|  |     |  |_ SoundBuffer.java
|  |     |  |_ MusicTrack.java
|  |     |_ ui/
|  |        |_ Button.java
|  |        |_ Slider.java
|  |        |_ Toggle.java
|  |        |_ Score.java
|  |_ src/test/java/sg/edu/sit/inf1009/p2team2/engine/
|     |_ ecs/
|     |  |_ EntityTest.java
|     |  |_ EntityUmlApiTest.java
|     |  |_ components/
|     |     |_ ColliderComponentTest.java
|     |_ managers/
|     |  |_ EntityManagerTest.java
|     |  |_ EntityManagerUmlApiTest.java
|     |  |_ MovementManagerTest.java
|     |  |_ MovementManagerConfigTest.java
|     |  |_ SceneManagerTest.java
|     |_ systems/
|     |  |_ MovementSystemTest.java
|     |_ collision/
|     |  |_ CollisionDetectorTest.java
|     |  |_ CollisionResolverTest.java
|     |_ config/
|     |  |_ ConfigManagerTest.java
|     |  |_ ConfigLoaderTest.java
|     |  |_ ConfigVarTest.java
|     |_ input/
|     |  |_ InputMapTest.java
|     |_ ui/
|     |  |_ UiModelsTest.java
|     |_ scenes/tests/
|        |_ SceneSmokeTest.java        # JUnit scene smoke checks
|        |_ InputOutputTestScene.java  # Runtime/manual scene
|        |_ CompleteIOTest.java        # Runtime/manual comprehensive scene
|_ lwjgl3/
|  |_ build.gradle
|  |_ src/main/java/sg/edu/sit/inf1009/p2team2/lwjgl3/
|     |_ Lwjgl3Launcher.java
|     |_ StartupHelper.java
```

## UML
<img width="8708" height="5769" alt="Abstract Engine UML Class Diagram (1)" src="https://github.com/user-attachments/assets/61f424c5-291f-4663-ad29-7fd62a83b54b" />

## Build Commands
```bash
./gradlew :core:compileJava :lwjgl3:compileJava
```

## Run Commands (Desktop)
- Default start (menu):
```bash
./gradlew lwjgl3:run
```

- Start directly in `MainScene` simulation:
```bash
./gradlew lwjgl3:run -Pscene=main
```

- Start runtime I/O scene test:
```bash
./gradlew lwjgl3:run -Pscene=io-test
```

- Start runtime complete I/O scene test:
```bash
./gradlew lwjgl3:run -Pscene=complete-io
```

## Simulation Controls (`MainScene`)
- `WASD` / Arrow keys: move player entity
- `Left Click`: spawn one NPC at cursor
- `Right Click`: remove one NPC
- `Mouse Scroll`: increase/decrease player speed
- `1`-`5`: switch render demo modes (`INTERACTIVE`, `SHAPES`, `COLORS`, `TEXT`, `STRESS`)
- `SPACE`: spawn 10 NPCs and cycle background color
- `BACKSPACE`: remove 10 NPCs
- `P`: cycle entity preset (`20`, `100`, `400`)
- `C`: toggle collision manager processing
- `[` / `]`: decrease/increase friction
- `F`: toggle fullscreen
- `M`: toggle music
- `+` / `-`: increase/decrease master volume
- `TAB`: pause/resume simulation update
- `ENTER`: rebuild simulation entities
- `ESC`: return to menu scene

## JUnit Test Commands
- Run all tests:
```bash
./gradlew :core:test
```

- Run one class:
```bash
./gradlew :core:test --tests "sg.edu.sit.inf1009.p2team2.engine.scenes.tests.SceneSmokeTest"
```

- Open HTML test report:
```bash
open core/build/reports/tests/test/index.html
```

- Output:
<img width="1382" height="726" alt="image" src="https://github.com/user-attachments/assets/c9492396-d50e-458f-b1f5-1bc8771b26bc" />

## Test Coverage Guide
- `EntityTest`, `EntityUmlApiTest`: ECS entity storage and UML API methods.
- `ColliderComponentTest`: shape assignment and collider position updates.
- `EntityManagerTest`, `EntityManagerUmlApiTest`: entity manager behavior and UML API compatibility.
- `MovementSystemTest`, `MovementManagerTest`, `MovementManagerConfigTest`: integration math and gravity/friction behavior.
- `CollisionDetectorTest`, `CollisionResolverTest`: detection and response for rectangle/circle collisions.
- `SceneManagerTest`: scene stack push/pop/update lifecycle.
- `SceneSmokeTest`: scene load safety and required background assets.
- `ConfigManagerTest`, `ConfigLoaderTest`, `ConfigVarTest`: config validation, persistence formats, and value conversion.
- `InputMapTest`: action-binding behavior.
- `UiModelsTest`: button/toggle/slider/score model behavior.
- `InputOutputTestScene`, `CompleteIOTest`: runtime/manual tests for visual/input/audio interaction.

What to look for:
- JUnit: `BUILD SUCCESSFUL` and all tests green in the HTML report.
- Runtime scenes: no exceptions in terminal and expected controls reacting in window.
