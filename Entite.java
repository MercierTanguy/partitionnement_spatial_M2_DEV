public class Entite {
    public static final int VIDE = 0, OBSTACLE = 1, JOUEUR = 2;

    public final int id;
    public double positionX, positionY, vitesseX, vitesseY;
    public double rayon;
    public int type;

    public Entite(int id, double positionX, double positionY, double rayon, int type) {
        this.id = id;
        this.positionX = positionX;
        this.positionY = positionY;
        this.rayon = rayon;
        this.type = type;
    }

    public LimitesSpatiales calculerLimites() {
        return new LimitesSpatiales(
                positionX - rayon,
                positionY - rayon,
                positionX + rayon,
                positionY + rayon);
    }

    public double calculerDistanceCarree(double pointX, double pointY) {
        double ecartX = positionX - pointX;
        double ecartY = positionY - pointY;
        return ecartX * ecartX + ecartY * ecartY;
    }

    public boolean estEnCollisionAvec(Entite autreEntite) {
        double rayonTotal = rayon + autreEntite.rayon;
        return calculerDistanceCarree(autreEntite.positionX, autreEntite.positionY)
                < rayonTotal * rayonTotal;
    }

    @Override
    public String toString() {
        return "Entite#" + id + "(" + positionX + "," + positionY
                + ",rayon=" + rayon + ")";
    }
}