package sg.edu.sit.inf1009.p2team2.engine.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.ui.Score;

public class LeadershipBoardScene extends Scene {
    private final List<Score> topScores = new ArrayList<>();

    public LeadershipBoardScene(EngineContext context) {
        super(context);
    }

    @Override
    public void load() {
        // TODO(Ivan): load leaderboard data/resources.
        System.out.println("[LeadershipBoardScene] Loading leaderboard resources...");
        LeadershipBoard();
    }

    @Override
    public void unload() {
        // TODO(Ivan): unload leaderboard resources.
        System.out.println("[LeadershipBoardScene] Unloading leaderboard data.");
        topScores.clear();
    }

    @Override
    public void update() {
        // TODO(Ivan): update leaderboard (if dynamic). 
        // Logic for any dynamic elements (e.g., scrolling or animations)

    }

    @Override
    public void render() {
        // TODO(Ivan): render leaderboard entries.
        // Use the Renderer via EngineContext to display the scores
        var renderer = context.getOutputManager().getRenderer();
        
        renderer.drawText("LEADERBOARD", new Vector2(350, 50), "title_font", Color.YELLOW);
        
        int yOffset = 150;
        for (int i = 0; i < topScores.size(); i++) {
            Score score = topScores.get(i);
            String scoreLine = (i + 1) + ". " + score.getName() + " - " + score.getPoints();
            
            // Highlight top 3 in different colors
            Color textColor = (i < 3) ? Color.CYAN : Color.WHITE;
            renderer.drawText(scoreLine, new Vector2(250, yOffset), "score_font", textColor);
            yOffset += 40;
        }
    }

    private void LeadershipBoard() {
        // TODO(Ivan): populate topScores from a data source. (TESTING PURPOSES ONLY)
        topScores.clear();
        topScores.add(new Score("Ivan", 15000));
        topScores.add(new Score("Alvin", 12500));
        topScores.add(new Score("Hong Yih", 11000));
        topScores.add(new Score("Nat", 9000));
        topScores.add(new Score("Hasif", 8500));
        
        System.out.println("[LeadershipBoardScene] Populated " + topScores.size() + " scores.");
    }
}

