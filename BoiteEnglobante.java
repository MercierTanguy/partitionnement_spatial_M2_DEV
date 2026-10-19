public final class BoiteEnglobante {
    public final double xMin, yMin, xMax, yMax;

    public BoiteEnglobante(double xMin, double yMin, double xMax, double yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    public double largeur() {
        return xMax - xMin;
    }

    public double hauteur() {
        return yMax - yMin;
    }

    public boolean intersecte(BoiteEnglobante autre) {
        boolean seRecoupentEnX = xMin <= autre.xMax && xMax >= autre.xMin;
        boolean seRecoupentEnY = yMin <= autre.yMax && yMax >= autre.yMin;
        return seRecoupentEnX && seRecoupentEnY;
    }

    public boolean contient(BoiteEnglobante autre) {
        boolean contenuEnX = autre.xMin >= xMin && autre.xMax <= xMax;
        boolean contenuEnY = autre.yMin >= yMin && autre.yMax <= yMax;
        return contenuEnX && contenuEnY;
    }

    public double distanceCarreeVers(double pointX, double pointY) {
        double ecartX = Math.max(Math.max(xMin - pointX, 0), pointX - xMax);
        double ecartY = Math.max(Math.max(yMin - pointY, 0), pointY - yMax);
        return ecartX * ecartX + ecartY * ecartY;
    }

    public BoiteEnglobante fusionnerAvec(BoiteEnglobante autre) {
        return new BoiteEnglobante(
                Math.min(xMin, autre.xMin),
                Math.min(yMin, autre.yMin),
                Math.max(xMax, autre.xMax),
                Math.max(yMax, autre.yMax));
    }

    @Override
    public String toString() {
        return "[" + xMin + "," + yMin + " -> " + xMax + "," + yMax + "]";
    }
}