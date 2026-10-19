import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class GenerateurDeScene {
    private static final int NOMBRE_D_AMAS = 10;
    private static final double DIVISEUR_LARGEUR_ECART_TYPE_DES_AMAS = 30.0;
    private static final double FRACTION_RAYON_MINIMUM = 0.25;
    private static final double RAPPORT_RAYON_MAXIMUM_SUR_MINIMUM = 32.0;

    private GenerateurDeScene() {}

    public enum Repartition { UNIFORME, REGROUPEE }

    public enum Tailles { HOMOGENES, VARIABLES }

    public static Scene generer(int nombreEntites, double largeurMonde,
                                Repartition repartition, Tailles tailles,
                                double rayonDeReference, double vitesseMaximaleParAxe,
                                long graine) {
        Random hasard = new Random(graine);
        BoiteEnglobante monde = new BoiteEnglobante(0, 0, largeurMonde, largeurMonde);
        double[][] centresDesAmas = tirerCentresDesAmas(hasard, largeurMonde);
        double ecartTypeDesAmas = largeurMonde / DIVISEUR_LARGEUR_ECART_TYPE_DES_AMAS;

        List<Entite> entites = new ArrayList<>(nombreEntites);
        for (int identifiant = 0; identifiant < nombreEntites; identifiant++) {
            double rayon = tirerRayon(hasard, tailles, rayonDeReference);

            double[] position = repartition == Repartition.UNIFORME
                    ? tirerPositionUniforme(hasard, largeurMonde)
                    : tirerPositionDansUnAmas(hasard, centresDesAmas, ecartTypeDesAmas);

            double x = garderDansLeMonde(position[0], rayon, largeurMonde);
            double y = garderDansLeMonde(position[1], rayon, largeurMonde);

            Entite entite = new Entite(identifiant, x, y, rayon, Entite.OBSTACLE);
            entite.vitesseX = tirerVitesse(hasard, vitesseMaximaleParAxe);
            entite.vitesseY = tirerVitesse(hasard, vitesseMaximaleParAxe);
            entites.add(entite);
        }
        return new Scene(entites, monde, null);
    }

    private static double[][] tirerCentresDesAmas(Random hasard, double largeurMonde) {
        double[][] centres = new double[NOMBRE_D_AMAS][2];
        for (double[] centre : centres) {
            centre[0] = hasard.nextDouble() * largeurMonde;
            centre[1] = hasard.nextDouble() * largeurMonde;
        }
        return centres;
    }

    private static double tirerRayon(Random hasard, Tailles tailles, double rayonDeReference) {
        if (tailles == Tailles.HOMOGENES) {
            return rayonDeReference;
        }
        double facteurLogUniforme = Math.pow(RAPPORT_RAYON_MAXIMUM_SUR_MINIMUM, hasard.nextDouble());
        return rayonDeReference * FRACTION_RAYON_MINIMUM * facteurLogUniforme;
    }

    private static double[] tirerPositionUniforme(Random hasard, double largeurMonde) {
        double x = hasard.nextDouble() * largeurMonde;
        double y = hasard.nextDouble() * largeurMonde;
        return new double[] { x, y };
    }

    private static double[] tirerPositionDansUnAmas(Random hasard, double[][] centresDesAmas,
                                                    double ecartTypeDesAmas) {
        double[] centre = centresDesAmas[hasard.nextInt(NOMBRE_D_AMAS)];
        double x = centre[0] + hasard.nextGaussian() * ecartTypeDesAmas;
        double y = centre[1] + hasard.nextGaussian() * ecartTypeDesAmas;
        return new double[] { x, y };
    }

    private static double garderDansLeMonde(double coordonnee, double rayon, double largeurMonde) {
        return Math.min(Math.max(coordonnee, rayon), largeurMonde - rayon);
    }

    private static double tirerVitesse(Random hasard, double vitesseMaximaleParAxe) {
        return (hasard.nextDouble() * 2 - 1) * vitesseMaximaleParAxe;
    }
}