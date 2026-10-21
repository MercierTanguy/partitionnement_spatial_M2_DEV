import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MethodeQuadtree {

    private static final String FICHIER_RAPPORT = "rapport_quadtree.txt";
    private static final int CAPACITE = 8;
    private static final int PROFONDEUR_MAXIMALE = 8;

    private MethodeQuadtree() {
    }

    public static void executer(
            Scene scene,
            double pointX,
            double pointY,
            int k) throws IOException {

        if (scene == null || scene.entites == null || scene.monde == null) {
            throw new IllegalArgumentException(
                    "La scene, sa liste d'entites et son monde sont obligatoires.");
        }

        if (k < 0) {
            throw new IllegalArgumentException(
                    "Le nombre k doit etre positif ou nul.");
        }

        int nombreEntites = scene.entites.size();
        Runtime runtime = Runtime.getRuntime();

        long memoireDebut = memoireUtilisee(runtime);

        long debut = System.nanoTime();
        ArbreQuaternaire arbre =
                new ArbreQuaternaire(scene.monde, CAPACITE, PROFONDEUR_MAXIMALE);
        for (Entite entite : scene.entites) {
            arbre.inserer(entite);
        }
        long tempsConstruction = System.nanoTime() - debut;
        long memoireApresConstruction = memoireUtilisee(runtime);

        debut = System.nanoTime();
        List<Entite> plusProches = arbre.kPlusProches(pointX, pointY, k);
        long tempsProximite = System.nanoTime() - debut;

        debut = System.nanoTime();
        List<Entite[]> collisions = arbre.collisions();
        long tempsCollisions = System.nanoTime() - debut;
        long testsEffectues = arbre.obtenirTestsDeCollision();

        long memoireFin = memoireUtilisee(runtime);

        long pairesBrute = (long) nombreEntites * (nombreEntites - 1) / 2;

        StringBuilder rapport = new StringBuilder();
        ligne(rapport, "========================================");
        ligne(rapport, " RAPPORT QUADTREE");
        ligne(rapport, "========================================");
        ligne(rapport, "");
        ligne(rapport, "1. TAILLE DE LA SCENE");
        ligne(rapport, "Nombre d'entites : " + nombreEntites);
        ligne(rapport, "Limites du monde : " + scene.monde);
        ligne(rapport, "Capacite par noeud : " + CAPACITE
                + " | profondeur maximale autorisee : " + PROFONDEUR_MAXIMALE);

        ligne(rapport, "");
        ligne(rapport, "2. CONSTRUCTION");
        ligne(rapport, "Temps : " + tempsConstruction + " ns ("
                + tempsConstruction / 1_000_000.0 + " ms)");
        ligne(rapport, "Nombre de noeuds : " + arbre.compterNoeuds());
        ligne(rapport, "Profondeur atteinte : " + arbre.calculerProfondeur());
        ligne(rapport, "Variation memoire observee : "
                + differenceMemoire(memoireDebut, memoireApresConstruction));

        ligne(rapport, "");
        ligne(rapport, "3. RECHERCHE DES K PLUS PROCHES");
        ligne(rapport, "Point recherche : (" + pointX + ", " + pointY + ")");
        ligne(rapport, "K demande : " + k);
        ligne(rapport, "Nombre de resultats : " + plusProches.size());
        ligne(rapport, "Temps : " + tempsProximite + " ns ("
                + tempsProximite / 1_000_000.0 + " ms)");
        for (int i = 0; i < plusProches.size(); i++) {
            Entite entite = plusProches.get(i);
            ligne(rapport, (i + 1) + ". " + entite + " | distance = "
                    + Math.sqrt(entite.calculerDistanceCarree(pointX, pointY)));
        }

        ligne(rapport, "");
        ligne(rapport, "4. DETECTION DES COLLISIONS");
        ligne(rapport, "Paires examinees : " + testsEffectues
                + " (force brute : " + pairesBrute + ")");
        ligne(rapport, "Collisions detectees : " + collisions.size());
        ligne(rapport, "Temps : " + tempsCollisions + " ns ("
                + tempsCollisions / 1_000_000.0 + " ms)");
        for (Entite[] paire : collisions) {
            ligne(rapport, "- " + paire[0] + " <--> " + paire[1]);
        }

        ligne(rapport, "");
        ligne(rapport, "5. BILAN MEMOIRE");
        ligne(rapport, "Memoire utilisee avant : " + memoireDebut + " octets");
        ligne(rapport, "Memoire utilisee apres : " + memoireFin + " octets");
        ligne(rapport, "Variation globale observee : "
                + differenceMemoire(memoireDebut, memoireFin));
        ligne(rapport, "Attention : mesure indicative du tas Java, "
                + "sensible au ramasse-miettes.");

        ligne(rapport, "");
        ligne(rapport, "6. COMPLEXITE THEORIQUE");
        ligne(rapport, "Construction : O(n log n) en moyenne");
        ligne(rapport, "Recherche des k plus proches : O(log n + k) en moyenne");
        ligne(rapport, "Requete de zone : O(log n + m), m = nombre de resultats");
        ligne(rapport, "Detection des collisions : O(n log n) en moyenne, O(n^2) au pire");
        ligne(rapport, "Mise a jour d'une entite : O(1) si elle reste dans son noeud, "
                + "sinon O(profondeur)");
        ligne(rapport, "n = nombre d'entites de la scene");

        Files.writeString(Path.of(FICHIER_RAPPORT), rapport.toString());
    }

    private static void ligne(StringBuilder rapport, String texte) {
        rapport.append(texte).append(System.lineSeparator());
    }

    private static long memoireUtilisee(Runtime runtime) {
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static String differenceMemoire(long debut, long fin) {
        long difference = fin - debut;
        return difference + " octets (" + difference / 1024.0 + " Ko)";
    }
}
