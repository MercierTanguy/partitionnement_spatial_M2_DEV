import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BenchmarkQuadtree {

    static final int[] NOMBRES_ENTITES = { 500, 5_000, 20_000 };
    static final int NOMBRE_REQUETES = 100;
    static final int K = 10;
    static final double RAYON_REFERENCE = 3.0;
    static final double VITESSE_MAX = 5.0;
    static final double DUREE_ETAPE = 0.1;

    public static void main(String[] arguments) {
        for (int i = 0; i < 3; i++) {
            mesurer(2_000, GenerateurNiveau.Repartition.REGROUPEE,
                    GenerateurNiveau.Tailles.VARIABLES, false);
        }

        System.out.printf("%-9s %-9s %7s | %9s %9s | %9s %9s | %9s %9s | %9s %9s %9s | %6s%n",
                "repart.", "tailles", "n",
                "build(ms)", "noeuds",
                "knnB(ms)", "knnQ(ms)",
                "colB(ms)", "colQ(ms)",
                "upd(ms)", "rebuild", "", "ok");

        for (GenerateurNiveau.Repartition repartition : GenerateurNiveau.Repartition.values()) {
            for (GenerateurNiveau.Tailles tailles : GenerateurNiveau.Tailles.values()) {
                for (int n : NOMBRES_ENTITES) {
                    mesurer(n, repartition, tailles, true);
                }
            }
        }
    }

    static void mesurer(int n, GenerateurNiveau.Repartition repartition,
                        GenerateurNiveau.Tailles tailles, boolean afficher) {
        double largeurMonde = Math.sqrt(n) * 10.0;
        Scene scene = GenerateurNiveau.generer(
                n, largeurMonde, repartition, tailles, RAYON_REFERENCE, VITESSE_MAX, 1L);
        Random hasard = new Random(42);

        // --- construction ---
        long debut = System.nanoTime();
        ArbreQuaternaire arbre = new ArbreQuaternaire(scene.monde, 8, 8);
        for (Entite entite : scene.entites) {
            arbre.inserer(entite);
        }
        double tempsConstruction = ms(debut);

        double[][] points = new double[NOMBRE_REQUETES][2];
        for (double[] p : points) {
            p[0] = hasard.nextDouble() * largeurMonde;
            p[1] = hasard.nextDouble() * largeurMonde;
        }

        boolean coherent = true;

        debut = System.nanoTime();
        List<List<Entite>> reponsesBrute = new ArrayList<>();
        for (double[] p : points) {
            reponsesBrute.add(kPlusProchesBrute(scene.entites, p[0], p[1], K));
        }
        double tempsKnnBrute = ms(debut) / NOMBRE_REQUETES;

        debut = System.nanoTime();
        List<List<Entite>> reponsesArbre = new ArrayList<>();
        for (double[] p : points) {
            reponsesArbre.add(arbre.kPlusProches(p[0], p[1], K));
        }
        double tempsKnnArbre = ms(debut) / NOMBRE_REQUETES;

        for (int i = 0; i < NOMBRE_REQUETES; i++) {
            for (int j = 0; j < K; j++) {
                double a = reponsesBrute.get(i).get(j).calculerDistanceCarree(points[i][0], points[i][1]);
                double b = reponsesArbre.get(i).get(j).calculerDistanceCarree(points[i][0], points[i][1]);
                if (a != b) {
                    coherent = false;
                }
            }
        }
        debut = System.nanoTime();
        List<Entite[]> collisionsBrute = collisionsBrute(scene.entites);
        double tempsCollisionsBrute = ms(debut);

        debut = System.nanoTime();
        List<Entite[]> collisionsArbre = arbre.collisions();
        double tempsCollisionsArbre = ms(debut);

        coherent &= ensemblePaires(collisionsBrute).equals(ensemblePaires(collisionsArbre));

        scene.avancer(DUREE_ETAPE);

        debut = System.nanoTime();
        for (Entite entite : scene.entites) {
            arbre.mettreAJour(entite);
        }
        double tempsMiseAJour = ms(debut);

        scene.avancer(DUREE_ETAPE);

        debut = System.nanoTime();
        arbre.reconstruire(scene.entites);
        double tempsReconstruction = ms(debut);

        coherent &= ensemblePaires(collisionsBrute(scene.entites))
                .equals(ensemblePaires(arbre.collisions()));

        if (afficher) {
            System.out.printf("%-9s %-9s %7d | %9.2f %9d | %9.3f %9.3f | %9.1f %9.1f | %9.2f %9.2f %9s | %6s%n",
                    repartition, tailles, n,
                    tempsConstruction, arbre.compterNoeuds(),
                    tempsKnnBrute, tempsKnnArbre,
                    tempsCollisionsBrute, tempsCollisionsArbre,
                    tempsMiseAJour, tempsReconstruction, "",
                    coherent ? "OK" : "ERREUR");
        }
    }


    static List<Entite> kPlusProchesBrute(List<Entite> entites, double x, double y, int k) {
        List<Entite> copie = new ArrayList<>(entites);
        Collections.sort(copie, Comparator.comparingDouble(e -> e.calculerDistanceCarree(x, y)));
        return new ArrayList<>(copie.subList(0, Math.min(k, copie.size())));
    }

    static List<Entite[]> collisionsBrute(List<Entite> entites) {
        List<Entite[]> resultat = new ArrayList<>();
        for (int i = 0; i < entites.size(); i++) {
            for (int j = i + 1; j < entites.size(); j++) {
                if (entites.get(i).estEnCollisionAvec(entites.get(j))) {
                    resultat.add(new Entite[] { entites.get(i), entites.get(j) });
                }
            }
        }
        return resultat;
    }

    static Set<Long> ensemblePaires(List<Entite[]> paires) {
        Set<Long> ensemble = new HashSet<>();
        for (Entite[] paire : paires) {
            long petit = Math.min(paire[0].id, paire[1].id);
            long grand = Math.max(paire[0].id, paire[1].id);
            ensemble.add((petit << 32) | grand);
        }
        return ensemble;
    }

    static double ms(long debutNanos) {
        return (System.nanoTime() - debutNanos) / 1e6;
    }
}
