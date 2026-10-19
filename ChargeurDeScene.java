import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ChargeurDeScene {
    private static final double DEMI_CASE = 0.5;
    private static final double RAYON_OBSTACLE = 0.5;
    private static final double RAYON_JOUEUR = 0.4;

    private ChargeurDeScene() {}

    public static Scene depuisFichier(Path chemin) throws IOException {
        List<String> lignes = Files.readAllLines(chemin);
        return depuisGrille(lignes.toArray(new String[0]));
    }

    public static Scene depuisGrille(String... lignes) {
        int hauteur = lignes.length;
        int largeur = largeurMaximale(lignes);

        int[][] grille = new int[hauteur][largeur];
        List<Entite> entites = new ArrayList<>();
        int prochainIdentifiant = 0;

        for (int ligne = 0; ligne < hauteur; ligne++) {
            for (int colonne = 0; colonne < lignes[ligne].length(); colonne++) {
                int typeDeCase = lireTypeDeCase(lignes[ligne].charAt(colonne));
                grille[ligne][colonne] = typeDeCase;

                Entite entite = creerEntite(prochainIdentifiant, colonne, ligne, typeDeCase);
                if (entite != null) {
                    entites.add(entite);
                    prochainIdentifiant++;
                }
            }
        }
        return new Scene(entites, new BoiteEnglobante(0, 0, largeur, hauteur), grille);
    }

    private static int largeurMaximale(String[] lignes) {
        int largeur = 0;
        for (String ligne : lignes) {
            largeur = Math.max(largeur, ligne.length());
        }
        return largeur;
    }

    private static int lireTypeDeCase(char caractere) {
        int type = caractere - '0';
        boolean typeConnu = type >= Entite.VIDE && type <= Entite.JOUEUR;
        return typeConnu ? type : Entite.VIDE;
    }

    private static Entite creerEntite(int identifiant, int colonne, int ligne, int typeDeCase) {
        double centreX = colonne + DEMI_CASE;
        double centreY = ligne + DEMI_CASE;

        if (typeDeCase == Entite.OBSTACLE) {
            return new Entite(identifiant, centreX, centreY, RAYON_OBSTACLE, Entite.OBSTACLE);
        }
        if (typeDeCase == Entite.JOUEUR) {
            return new Entite(identifiant, centreX, centreY, RAYON_JOUEUR, Entite.JOUEUR);
        }
        return null;
    }
}