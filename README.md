# INF1009 P2 Team 2 - Abstract Engine (Part 1)

This repository contains the Part 1 "Abstract Engine" codebase for INF1009 Object Oriented Programming.
The goal for Part 1 is to build a non-contextual engine (no simulation/game-specific logic) that can be reused for many simulations.

Base package: `sg.edu.sit.inf1009.p2team2`

## Tech Stack

- Java 17
- Gradle (wrapper included)
- libGDX (`core` + `lwjgl3`)

## Run (Desktop)

```bash
./gradlew lwjgl3:run
```

## Project Layout

- `core/`: engine code shared by all platforms
- `lwjgl3/`: desktop launcher (LWJGL3)

<img width="8708" height="5769" alt="Abstract Engine UML Class Diagram (1)" src="https://github.com/user-attachments/assets/61f424c5-291f-4663-ad29-7fd62a83b54b" />

Key entrypoints:

- `core/src/main/java/sg/edu/sit/inf1009/p2team2/Main.java` (placeholder application)
- `lwjgl3/src/main/java/sg/edu/sit/inf1009/p2team2/lwjgl3/Lwjgl3Launcher.java` (desktop launcher)

## Engine Skeleton (UML-Aligned)

This repo currently contains a skeleton implementation (class names + file structure + method signatures + TODOs).
Do not add context-specific simulation logic in Part 1.

Managers (must-have):

- Scene Management (Owner: Ivan)
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/managers/SceneManager.java`
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/scenes/Scene.java`
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/scenes/MenuScene.java`
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/scenes/SettingsScene.java`
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/scenes/LeadershipBoardScene.java`

- Entity Management (Owner: Nat)
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/managers/EntityManager.java`
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/ecs/Entity.java`
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/ecs/Component.java`
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/ecs/components/*`

- Movement Management (Owner: Hasif)
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/managers/MovementManager.java`
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/systems/MovementSystem.java`

- Input/Output Management (Owner: HongYih)
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/managers/InputManager.java`
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/input/*`
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/managers/OutputManager.java`
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/output/*`

- Collision Management (Owner: Cody)
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/collision/*`

- Configuration Management (Owner: Alvin)
  - `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/config/*`

Shared wiring:

- `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/core/EngineContext.java`
- `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/world/World.java`

UI models (used by scenes):

- `core/src/main/java/sg/edu/sit/inf1009/p2team2/engine/ui/*`

## Team Workflow (Reduce Merge Conflicts)

1. One owner per core file. Only the owner edits their file unless agreed.
2. Use feature branches: `feature/<name>-<area>`
3. Keep commits small and descriptive (avoid sweeping reformatting).
4. Before pushing/PR:

```bash
./gradlew :core:compileJava :lwjgl3:compileJava
```

## Next Steps (Implementation Phase)

- Ivan: implement scene stack behavior + transitions + UI input handling.
- Nat: extend ECS/entity lifecycle and provide safe iteration/query helpers.
- Hasif: integrate MovementManager/System with EntityManager queries and delta-time updates.
- HongYih: wire libGDX input + rendering + audio implementations behind the facades.
- Alvin: implement config load/save, observers, defaults, and typed accessors.
- Cody: implement collision detection/resolution + collision events.
