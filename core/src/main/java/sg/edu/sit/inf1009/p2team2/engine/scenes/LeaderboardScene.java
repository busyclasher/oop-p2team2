package sg.edu.sit.inf1009.p2team2.engine.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.ui.Score;

public class LeaderboardScene extends Scene {
    private final List<Score> topScores = new ArrayList<>();

    public LeaderboardScene(EngineContext context) {
        super(context);
    }

    @Override
    public void load() {
        loadScores();
    }

    @Override
    public void unload() {
        topScores.clear();
    }

    @Override
    public void update(float dt) {
        // Scene is static in skeleton mode.
    }

    @Override
    public void render() {
        var renderer = context.getOutputManager().getRenderer();

        renderer.clear();
        renderer.begin();
        renderer.drawText("LEADERBOARD", new Vector2(350, 700), "default", Color.YELLOW);

        int yOffset = 620;
        for (int i = 0; i < topScores.size(); i++) {
            Score score = topScores.get(i);
            String scoreLine = (i + 1) + ". " + score.getName() + " - " + score.getPoints();
            Color textColor = (i < 3) ? Color.CYAN : Color.WHITE;
            renderer.drawText(scoreLine, new Vector2(250, yOffset), "default", textColor);
            yOffset -= 40;
        }

        renderer.drawText("Press ESC to return", new Vector2(20, 30), "default", Color.LIGHT_GRAY);
        renderer.end();
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().getKeyboard().isKeyPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            context.getSceneManager().pop();
        }
    }

    private void loadScores() {
        topScores.clear();
        topScores.add(new Score("Ivan", 15000));
        topScores.add(new Score("Alvin", 12500));
        topScores.add(new Score("Hong Yih", 11000));
        topScores.add(new Score("Nat", 9000));
        topScores.add(new Score("Hasif", 8500));
    }
}
