import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class LecteurGrille {
    private static final double MOITIE_CASE = 0.5;
    private static final double RAYON_MUR = 0.5;
    private static final double RAYON_PERSONNAGE = 0.4;

    private LecteurGrille() {}

    public static Scene lireFichier(Path cheminFichier) throws IOException {
        List<String> contenuFichier = Files.readAllLines(cheminFichier);
        return lireGrille(contenuFichier.toArray(new String[0]));
    }

    public static Scene lireGrille(String... lignesGrille) {
        int nombreLignes = lignesGrille.length;
        int nombreColonnes = calculerLargeurMaximale(lignesGrille);

        int[][] grille = new int[nombreLignes][nombreColonnes];
        List<Entite> listeEntites = new ArrayList<>();
        int identifiantSuivant = 0;

        for (int indiceLigne = 0; indiceLigne < nombreLignes; indiceLigne++) {
            for (int indiceColonne = 0; indiceColonne < lignesGrille[indiceLigne].length(); indiceColonne++) {
                int typeEntite = determinerTypeCase(lignesGrille[indiceLigne].charAt(indiceColonne));
                grille[indiceLigne][indiceColonne] = typeEntite;

                Entite nouvelleEntite = construireEntite(
                        identifiantSuivant, indiceColonne, indiceLigne, typeEntite);

                if (nouvelleEntite != null) {
                    listeEntites.add(nouvelleEntite);
                    identifiantSuivant++;
                }
            }
        }

        return new Scene(
                listeEntites,
                new LimitesSpatiales(0, 0, nombreColonnes, nombreLignes),
                grille);
    }

    private static int calculerLargeurMaximale(String[] lignesGrille) {
        int largeurMaximale = 0;

        for (String ligneGrille : lignesGrille) {
            largeurMaximale = Math.max(largeurMaximale, ligneGrille.length());
        }

        return largeurMaximale;
    }

    private static int determinerTypeCase(char symboleCase) {
        int typeEntite = symboleCase - '0';
        boolean typeValide = typeEntite >= Entite.VIDE && typeEntite <= Entite.JOUEUR;

        return typeValide ? typeEntite : Entite.VIDE;
    }

    private static Entite construireEntite(
            int identifiant,
            int indiceColonne,
            int indiceLigne,
            int typeEntite) {

        double positionX = indiceColonne + MOITIE_CASE;
        double positionY = indiceLigne + MOITIE_CASE;

        if (typeEntite == Entite.OBSTACLE) {
            return new Entite(
                    identifiant, positionX, positionY, RAYON_MUR, Entite.OBSTACLE);
        }

        if (typeEntite == Entite.JOUEUR) {
            return new Entite(
                    identifiant, positionX, positionY, RAYON_PERSONNAGE, Entite.JOUEUR);
        }

        return null;
    }
}