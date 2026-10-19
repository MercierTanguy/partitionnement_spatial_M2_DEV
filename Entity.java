public class Entity {
    public static final int EMPTY = 0, OBSTACLE = 1, PLAYER = 2;

    public final int id;
    public double x, y, vx, vy;
    public double radius;
    public int type;

    public Entity(int id, double x, double y, double radius, int type) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.type = type;
    }

    public AABB bounds() {
        return new AABB(x - radius, y - radius, x + radius, y + radius);
    }

    public double distSq(double px, double py) {
        double dx = x - px, dy = y - py;
        return dx * dx + dy * dy;
    }


    public boolean collidesWith(Entity o) {
        double r = radius + o.radius;
        return distSq(o.x, o.y) < r * r;
    }

    @Override
    public String toString() {
        return "Entity#" + id + "(" + x + "," + y + ",r=" + radius + ")";
    }
}