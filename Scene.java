import java.util.ArrayList;
import java.util.List;

public class Scene {
    public final List<Entite> entites;
    public final BoiteEnglobante monde;
    public final int[][] grille;

    public Scene(List<Entite> entites, BoiteEnglobante monde, int[][] grille) {
        this.entites = entites;
        this.monde = monde;
        this.grille = grille;
    }

    public List<Entite> joueurs() {
        List<Entite> joueurs = new ArrayList<>();
        for (Entite entite : entites) {
            if (entite.type == Entite.JOUEUR) {
                joueurs.add(entite);
            }
        }
        return joueurs;
    }

    public void avancer(double duree) {
        for (Entite entite : entites) {
            if (estImmobile(entite)) continue;

            entite.x += entite.vitesseX * duree;
            entite.y += entite.vitesseY * duree;

            rebondirSurLesBordsDuMonde(entite);
        }
    }

    private boolean estImmobile(Entite entite) {
        return entite.vitesseX == 0 && entite.vitesseY == 0;
    }

    private void rebondirSurLesBordsDuMonde(Entite entite) {
        if (entite.x - entite.rayon < monde.xMin) {
            entite.x = monde.xMin + entite.rayon;
            entite.vitesseX = -entite.vitesseX;
        }
        if (entite.x + entite.rayon > monde.xMax) {
            entite.x = monde.xMax - entite.rayon;
            entite.vitesseX = -entite.vitesseX;
        }
        if (entite.y - entite.rayon < monde.yMin) {
            entite.y = monde.yMin + entite.rayon;
            entite.vitesseY = -entite.vitesseY;
        }
        if (entite.y + entite.rayon > monde.yMax) {
            entite.y = monde.yMax - entite.rayon;
            entite.vitesseY = -entite.vitesseY;
        }
    }
}