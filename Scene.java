import java.util.ArrayList;
import java.util.List;


public class Scene {
    public final List<Entity> entities;
    public final AABB world;
    public final int[][] grid;

    public Scene(List<Entity> entities, AABB world, int[][] grid) {
        this.entities = entities;
        this.world = world;
        this.grid = grid;
    }

    public List<Entity> players() {
        List<Entity> l = new ArrayList<>();
        for (Entity e : entities) if (e.type == Entity.PLAYER) l.add(e);
        return l;
    }

    public void step(double dt) {
        for (Entity e : entities) {
            if (e.vx == 0 && e.vy == 0) continue;
            e.x += e.vx * dt;
            e.y += e.vy * dt;
            if (e.x - e.radius < world.minX) { e.x = world.minX + e.radius; e.vx = -e.vx; }
            if (e.x + e.radius > world.maxX) { e.x = world.maxX - e.radius; e.vx = -e.vx; }
            if (e.y - e.radius < world.minY) { e.y = world.minY + e.radius; e.vy = -e.vy; }
            if (e.y + e.radius > world.maxY) { e.y = world.maxY - e.radius; e.vy = -e.vy; }
        }
    }
}