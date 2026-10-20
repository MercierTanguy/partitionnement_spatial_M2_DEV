import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Recherche spatiale par force brute.
 *
 * Utilise les classes existantes Entite et Scene.
 * Le rapport est genere dans rapport_force_brute.txt.
 */
public class MethodeBrute {

    private static final String FICHIER_RAPPORT =
            "rapport_force_brute.txt";

    private MethodeBrute() {
        // Classe utilitaire : pas d'instance necessaire.
    }

    /**
     * Associe une entite a sa distance au point recherche.
     */
    private static class ResultatProximite {
        Entite entite;
        double distanceCarree;

        ResultatProximite(Entite entite, double distanceCarree) {
            this.entite = entite;
            this.distanceCarree = distanceCarree;
        }
    }

    /**
     * Lance les recherches et genere le rapport complet.
     *
     * @param scene scene contenant les entites
     * @param pointX abscisse du point de recherche
     * @param pointY ordonnee du point de recherche
     * @param k nombre d'entites les plus proches a rechercher
     */
    public static void executer(
            Scene scene,
            double pointX,
            double pointY,
            int k) throws IOException {

        if (scene == null || scene.entites == null) {
            throw new IllegalArgumentException(
                    "La scene et sa liste d'entites sont obligatoires.");
        }

        if (k < 0) {
            throw new IllegalArgumentException(
                    "Le nombre k doit etre positif ou nul.");
        }

        List<Entite> entites = scene.entites;
        int nombreEntites = entites.size();

        // Informations sur la memoire du processus Java.
        Runtime runtime = Runtime.getRuntime();

        long memoireDebut = memoireUtilisee(runtime);

        // =====================================================
        // 1. RECHERCHE DES K ENTITES LES PLUS PROCHES
        // =====================================================

        long debutProximite = System.nanoTime();

        List<ResultatProximite> resultats = new ArrayList<>();

        for (Entite entite : entites) {

            double distanceCarree =
                    entite.calculerDistanceCarree(pointX, pointY);

            resultats.add(
                    new ResultatProximite(entite, distanceCarree));
        }

        // Tri de toutes les entites par distance croissante.
        Collections.sort(resultats,
                Comparator.comparingDouble(
                        resultat -> resultat.distanceCarree));

        int nombreResultats = Math.min(k, resultats.size());

        List<ResultatProximite> plusProches =
                new ArrayList<>(resultats.subList(0, nombreResultats));

        long tempsProximite = System.nanoTime() - debutProximite;

        long memoireApresProximite = memoireUtilisee(runtime);

        // =====================================================
        // 2. DETECTION DE TOUTES LES COLLISIONS
        // =====================================================

        long debutCollisions = System.nanoTime();

        List<String> collisions = new ArrayList<>();
        long nombreComparaisons = 0;

        // Chaque paire est examinee une seule fois.
        for (int i = 0; i < nombreEntites; i++) {

            Entite premiere = entites.get(i);

            for (int j = i + 1; j < nombreEntites; j++) {

                Entite seconde = entites.get(j);
                nombreComparaisons++;

                if (premiere.estEnCollisionAvec(seconde)) {

                    collisions.add(
                            premiere + " <--> " + seconde);
                }
            }
        }

        long tempsCollisions = System.nanoTime() - debutCollisions;

        long memoireApresCollisions = memoireUtilisee(runtime);

        // =====================================================
        // 3. GENERATION DU RAPPORT
        // =====================================================

        long memoireFin = memoireUtilisee(runtime);

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FICHIER_RAPPORT))) {

            writer.write("========================================");
            writer.newLine();
            writer.write(" RAPPORT DE FORCE BRUTE");
            writer.newLine();
            writer.write("========================================");
            writer.newLine();

            writer.newLine();
            writer.write("1. TAILLE DE LA SCENE");
            writer.newLine();
            writer.write("Nombre d'entites : " + nombreEntites);
            writer.newLine();
            writer.write("Nombre de cellules de la grille : "
                    + nombreCellules(scene));
            writer.newLine();

            if (scene.monde != null) {
                writer.write("Limites X : "
                        + scene.monde.minimumX + " a "
                        + scene.monde.maximumX);
                writer.newLine();

                writer.write("Limites Y : "
                        + scene.monde.minimumY + " a "
                        + scene.monde.maximumY);
                writer.newLine();

                writer.write("Largeur du monde : "
                        + (scene.monde.maximumX
                        - scene.monde.minimumX));
                writer.newLine();

                writer.write("Hauteur du monde : "
                        + (scene.monde.maximumY
                        - scene.monde.minimumY));
                writer.newLine();
            }

            writer.newLine();
            writer.write("2. RECHERCHE DES K PLUS PROCHES");
            writer.newLine();
            writer.write("Point recherche : ("
                    + pointX + ", " + pointY + ")");
            writer.newLine();
            writer.write("K demande : " + k);
            writer.newLine();
            writer.write("Nombre de resultats : "
                    + nombreResultats);
            writer.newLine();
            writer.write("Temps : "
                    + tempsProximite + " ns ("
                    + tempsProximite / 1_000_000.0 + " ms)");
            writer.newLine();
            writer.write("Variation memoire observee : "
                    + differenceMemoire(
                            memoireDebut, memoireApresProximite));
            writer.newLine();

            for (int i = 0; i < plusProches.size(); i++) {
                ResultatProximite resultat = plusProches.get(i);

                writer.write((i + 1) + ". "
                        + resultat
                        + " | distance = "
                        + Math.sqrt(resultat.distanceCarree));
                writer.newLine();
            }

            writer.newLine();
            writer.write("3. DETECTION DES COLLISIONS");
            writer.newLine();
            writer.write("Paires examinees : "
                    + nombreComparaisons);
            writer.newLine();
            writer.write("Collisions detectees : "
                    + collisions.size());
            writer.newLine();
            writer.write("Temps : "
                    + tempsCollisions + " ns ("
                    + tempsCollisions / 1_000_000.0 + " ms)");
            writer.newLine();
            writer.write("Variation memoire observee : "
                    + differenceMemoire(
                            memoireApresProximite,
                            memoireApresCollisions));
            writer.newLine();

            for (String collision : collisions) {
                writer.write("- " + collision);
                writer.newLine();
            }

            writer.newLine();
            writer.write("4. BILAN MEMOIRE");
            writer.newLine();
            writer.write("Memoire utilisee avant : "
                    + memoireDebut + " octets");
            writer.newLine();
            writer.write("Memoire utilisee apres : "
                    + memoireFin + " octets");
            writer.newLine();
            writer.write("Variation globale observee : "
                    + differenceMemoire(memoireDebut, memoireFin));
            writer.newLine();
            writer.write("Attention : mesure indicative du tas Java, "
                    + "sensible au ramasse-miettes.");
            writer.newLine();

            writer.newLine();
            writer.write("5. COMPLEXITE THEORIQUE");
            writer.newLine();
            writer.write("Recherche des k plus proches : O(n log n)");
            writer.newLine();
            writer.write("Detection des collisions : O(n^2)");
            writer.newLine();
            writer.write("n = nombre d'entites de la scene");
            writer.newLine();
        }
    }

    /**
     * Retourne une estimation de la memoire occupee dans le tas Java.
     */
    private static long memoireUtilisee(Runtime runtime) {
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static String differenceMemoire(long debut, long fin) {
        long difference = fin - debut;
        return difference + " octets ("
                + difference / 1024.0 + " Ko)";
    }

    /**
     * Compte les cellules de la grille existante.
     */
    private static long nombreCellules(Scene scene) {
        if (scene.grille == null) {
            return 0;
        }

        long total = 0;

        for (int[] ligne : scene.grille) {
            if (ligne != null) {
                total += ligne.length;
            }
        }

        return total;
    }
}