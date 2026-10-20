public final class LimitesSpatiales {
    public final double minimumX, minimumY, maximumX, maximumY;

    public LimitesSpatiales(double minimumX, double minimumY, double maximumX, double maximumY) {
        this.minimumX = minimumX;
        this.minimumY = minimumY;
        this.maximumX = maximumX;
        this.maximumY = maximumY;
    }

    public double obtenirLargeur() {
        return maximumX - minimumX;
    }

    public double obtenirHauteur() {
        return maximumY - minimumY;
    }

    public boolean chevauche(LimitesSpatiales autresLimites) {
        boolean chevauchementX = minimumX <= autresLimites.maximumX && maximumX >= autresLimites.minimumX;
        boolean chevauchementY = minimumY <= autresLimites.maximumY && maximumY >= autresLimites.minimumY;
        return chevauchementX && chevauchementY;
    }

    public boolean englobe(LimitesSpatiales autresLimites) {
        boolean inclusionX = autresLimites.minimumX >= minimumX && autresLimites.maximumX <= maximumX;
        boolean inclusionY = autresLimites.minimumY >= minimumY && autresLimites.maximumY <= maximumY;
        return inclusionX && inclusionY;
    }

    public double calculerDistanceCarree(double positionX, double positionY) {
        double distanceX = Math.max(Math.max(minimumX - positionX, 0), positionX - maximumX);
        double distanceY = Math.max(Math.max(minimumY - positionY, 0), positionY - maximumY);
        return distanceX * distanceX + distanceY * distanceY;
    }

    public LimitesSpatiales unirAvec(LimitesSpatiales autresLimites) {
        return new LimitesSpatiales(
                Math.min(minimumX, autresLimites.minimumX),
                Math.min(minimumY, autresLimites.minimumY),
                Math.max(maximumX, autresLimites.maximumX),
                Math.max(maximumY, autresLimites.maximumY));
    }

    @Override
    public String toString() {
        return "[" + minimumX + "," + minimumY + " -> " + maximumX + "," + maximumY + "]";
    }
}