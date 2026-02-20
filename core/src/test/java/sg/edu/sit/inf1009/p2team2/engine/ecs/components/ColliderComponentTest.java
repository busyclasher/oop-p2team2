package sg.edu.sit.inf1009.p2team2.engine.ecs.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;
import sg.edu.sit.inf1009.p2team2.engine.collision.Circle;
import sg.edu.sit.inf1009.p2team2.engine.collision.Rectangle;

class ColliderComponentTest {

    @Test
    void getShapeReturnsAssignedShape() {
        ColliderComponent component = new ColliderComponent();
        Rectangle shape = new Rectangle(1f, 2f, 3f, 4f);

        component.setShape(shape);

        assertSame(shape, component.getShape());
    }

    @Test
    void updatePositionMovesRectangleShape() {
        ColliderComponent component = new ColliderComponent();
        Rectangle shape = new Rectangle(0f, 0f, 10f, 10f);
        component.setShape(shape);

        component.updatePosition(25f, 30f);

        assertEquals(25f, shape.getPosition().x, 0.0001f);
        assertEquals(30f, shape.getPosition().y, 0.0001f);
    }

    @Test
    void updatePositionMovesCircleFromTopLeftOrigin() {
        ColliderComponent component = new ColliderComponent();
        Circle shape = new Circle(new Vector2(10f, 10f), 5f);
        component.setShape(shape);

        component.updatePosition(40f, 50f);

        assertEquals(45f, shape.getCenter().x, 0.0001f);
        assertEquals(55f, shape.getCenter().y, 0.0001f);
    }
}
