import java.util.ArrayList;
import java.util.List;

public class Scene {
    public final List<Entite> entites;
    public final LimitesSpatiales monde;
    public final int[][] grille;

    public Scene(List<Entite> entites, LimitesSpatiales monde, int[][] grille) {
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

            entite.positionX += entite.vitesseX * duree;
            entite.positionY += entite.vitesseY * duree;

            rebondirSurLesBordsDuMonde(entite);
        }
    }

    private boolean estImmobile(Entite entite) {
        return entite.vitesseX == 0 && entite.vitesseY == 0;
    }

    private void rebondirSurLesBordsDuMonde(Entite entite) {
        if (entite.positionX - entite.rayon < monde.minimumX) {
            entite.positionX = monde.minimumX + entite.rayon;
            entite.vitesseX = -entite.vitesseX;
        }

        if (entite.positionX + entite.rayon > monde.maximumX) {
            entite.positionX = monde.maximumX - entite.rayon;
            entite.vitesseX = -entite.vitesseX;
        }

        if (entite.positionY - entite.rayon < monde.minimumY) {
            entite.positionY = monde.minimumY + entite.rayon;
            entite.vitesseY = -entite.vitesseY;
        }

        if (entite.positionY + entite.rayon > monde.maximumY) {
            entite.positionY = monde.maximumY - entite.rayon;
            entite.vitesseY = -entite.vitesseY;
        }
    }
}