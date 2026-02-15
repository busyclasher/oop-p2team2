package sg.edu.sit.inf1009.p2team2.engine.collision;

/**
 * Represents a collision between two colliders.
 */
public class Collision {
    private final Collider a;
    private final Collider b;
    private final Contact contact;

    public Collision(Collider a, Collider b, Contact contact) {
        this.a = a;
        this.b = b;
        this.contact = contact;
    }

    public Collider getA() {
        return a;
    }

    public Collider getB() {
        return b;
    }

    public Contact getContact() {
        return contact;
    }
}

