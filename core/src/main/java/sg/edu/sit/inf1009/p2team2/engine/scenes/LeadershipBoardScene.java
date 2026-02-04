package sg.edu.sit.inf1009.p2team2.engine.scenes;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Override
    public void unload() {
        // TODO(Ivan): unload leaderboard resources.
    }

    @Override
    public void update() {
        // TODO(Ivan): update leaderboard (if dynamic).
    }

    @Override
    public void render() {
        // TODO(Ivan): render leaderboard entries.
    }

    private void LeadershipBoard() {
        // TODO(Ivan): populate topScores from a data source.
    }
}

