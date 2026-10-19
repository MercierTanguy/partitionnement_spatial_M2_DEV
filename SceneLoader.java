import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public final class SceneLoader {
    private SceneLoader() {}

    public static Scene fromFile(Path path) throws IOException {
        return fromGrid(Files.readAllLines(path).toArray(new String[0]));
    }

    public static Scene fromGrid(String... rows) {
        int h = rows.length;
        int w = 0;
        for (String r : rows) w = Math.max(w, r.length());
        int[][] grid = new int[h][w];
        List<Entity> entities = new ArrayList<>();
        int id = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < rows[y].length(); x++) {
                int v = rows[y].charAt(x) - '0';
                if (v < 0 || v > 2) v = Entity.EMPTY;
                grid[y][x] = v;
                if (v == Entity.OBSTACLE) {
                    entities.add(new Entity(id++, x + 0.5, y + 0.5, 0.5, Entity.OBSTACLE));
                } else if (v == Entity.PLAYER) {
                    entities.add(new Entity(id++, x + 0.5, y + 0.5, 0.4, Entity.PLAYER));
                }
            }
        }
        return new Scene(entities, new AABB(0, 0, w, h), grid);
    }
}