public final class AABB {
    public final double minX, minY, maxX, maxY;

    public AABB(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public double width() { return maxX - minX; }
    public double height() { return maxY - minY; }

    public boolean intersects(AABB o) {
        return minX <= o.maxX && maxX >= o.minX && minY <= o.maxY && maxY >= o.minY;
    }

    public boolean contains(AABB o) {
        return o.minX >= minX && o.maxX <= maxX && o.minY >= minY && o.maxY <= maxY;
    }

    /** Distance au carré entre un point et le rectangle (0 si le point est dedans). */
    public double distSqTo(double px, double py) {
        double dx = Math.max(Math.max(minX - px, 0), px - maxX);
        double dy = Math.max(Math.max(minY - py, 0), py - maxY);
        return dx * dx + dy * dy;
    }

    public AABB union(AABB o) {
        return new AABB(Math.min(minX, o.minX), Math.min(minY, o.minY),
                Math.max(maxX, o.maxX), Math.max(maxY, o.maxY));
    }

    @Override
    public String toString() {
        return "[" + minX + "," + minY + " -> " + maxX + "," + maxY + "]";
    }
}