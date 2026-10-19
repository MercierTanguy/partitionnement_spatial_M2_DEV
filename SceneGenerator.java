import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public final class SceneGenerator {
    private SceneGenerator() {}

    public enum Distribution { UNIFORM, CLUSTERED }

    public enum Sizes { HOMOGENEOUS, VARIABLE }

    /**
     * @param n          nombre d'objets
     * @param width      largeur du monde (hauteur = width)
     * @param dist       répartition uniforme ou en amas gaussiens
     * @param sizes      rayons identiques, ou log-uniformes entre baseRadius/4 et 8*baseRadius
     * @param baseRadius rayon de référence
     * @param speed      vitesse max par axe (0 = scène statique)
     * @param seed       graine du générateur aléatoire
     */
    public static Scene generate(int n, double width, Distribution dist, Sizes sizes,
                                 double baseRadius, double speed, long seed) {
        Random rng = new Random(seed);
        AABB world = new AABB(0, 0, width, width);
        int nClusters = 10;
        double[][] centers = new double[nClusters][2];
        for (double[] c : centers) {
            c[0] = rng.nextDouble() * width;
            c[1] = rng.nextDouble() * width;
        }
        double sigma = width / 30.0;

        List<Entity> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            double r = sizes == Sizes.HOMOGENEOUS
                    ? baseRadius
                    : baseRadius * 0.25 * Math.pow(32.0, rng.nextDouble());
            double x, y;
            if (dist == Distribution.UNIFORM) {
                x = rng.nextDouble() * width;
                y = rng.nextDouble() * width;
            } else {
                double[] c = centers[rng.nextInt(nClusters)];
                x = c[0] + rng.nextGaussian() * sigma;
                y = c[1] + rng.nextGaussian() * sigma;
            }
            x = Math.min(Math.max(x, r), width - r);
            y = Math.min(Math.max(y, r), width - r);
            Entity e = new Entity(i, x, y, r, Entity.OBSTACLE);
            e.vx = (rng.nextDouble() * 2 - 1) * speed;
            e.vy = (rng.nextDouble() * 2 - 1) * speed;
            list.add(e);
        }
        return new Scene(list, world, null);
    }
}