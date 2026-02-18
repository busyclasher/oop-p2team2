package sg.edu.sit.inf1009.p2team2.engine.collision;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;

import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;

class CollisionResolverTest {

    @Test
    void resolveSeparatesAndAdjustsVelocity() {
        Entity a = new Entity(1);
        Entity b = new Entity(2);

        TransformComponent ta = new TransformComponent();
        ta.setPosition(new Vector2(0f, 0f));
        VelocityComponent va = new VelocityComponent();
        va.setVelocity(new Vector2(5f, 0f));

        TransformComponent tb = new TransformComponent();
        tb.setPosition(new Vector2(0.5f, 0f));
        VelocityComponent vb = new VelocityComponent();
        vb.setVelocity(new Vector2(-5f, 0f));

        a.add(ta);
        a.add(va);
        b.add(tb);
        b.add(vb);

        Collision collision = new Collision(a, b);
        collision.setContactNormal(new Vector2(1f, 0f));
        collision.setPenetrationDepth(1f);

        CollisionResolver resolver = new CollisionResolver();
        resolver.setRestitution(0.2f);
        resolver.resolve(collision);

        assertNotEquals(0f, ta.getPosition().x, 0.0001f);
        assertNotEquals(0.5f, tb.getPosition().x, 0.0001f);
        assertNotEquals(5f, va.getVelocity().x, 0.0001f);
        assertNotEquals(-5f, vb.getVelocity().x, 0.0001f);
    }
}
