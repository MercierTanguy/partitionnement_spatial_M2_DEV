public class Entite {
    public static final int VIDE = 0, OBSTACLE = 1, JOUEUR = 2;

    public final int identifiant;
    public double x, y, vitesseX, vitesseY;
    public double rayon;
    public int type;

    public Entite(int identifiant, double x, double y, double rayon, int type) {
        this.identifiant = identifiant;
        this.x = x;
        this.y = y;
        this.rayon = rayon;
        this.type = type;
    }

    public BoiteEnglobante boiteEnglobante() {
        return new BoiteEnglobante(x - rayon, y - rayon, x + rayon, y + rayon);
    }

    public double distanceCarreeVers(double pointX, double pointY) {
        double ecartX = x - pointX;
        double ecartY = y - pointY;
        return ecartX * ecartX + ecartY * ecartY;
    }

    public boolean enCollisionAvec(Entite autre) {
        double sommeDesRayons = rayon + autre.rayon;
        return distanceCarreeVers(autre.x, autre.y) < sommeDesRayons * sommeDesRayons;
    }

    @Override
    public String toString() {
        return "Entite#" + identifiant + "(" + x + "," + y + ",rayon=" + rayon + ")";
    }
}