package sg.edu.sit.inf1009.p2team2.engine.ecs.components;

import sg.edu.sit.inf1009.p2team2.engine.ecs.Component;

public class TagComponent implements Component {
    private String tag;

    public TagComponent() {
        this.tag = "";
    }

    public TagComponent(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean hasTag(String tag) {
        return this.tag != null && this.tag.equals(tag);
    }
}

