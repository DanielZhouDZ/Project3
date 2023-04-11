package byow.Core;

import java.util.Comparator;

class RoomComparator implements Comparator<Room> {
    private final Point pt;

    /**
     * Stores the location of the main room
     * @param spot: a point object representing the location of the main room
     */
    public RoomComparator(Point spot) {
        this.pt = spot;
    }

    /**
     * Compares the distance between the two rooms from the main room
     * @param r1: the first object to be compared.
     * @param r2: the second object to be compared.
     * @return -1 if r1 is closer, 0 if they're even, and 1 if r2 is larger
     */
    @Override
    public int compare(Room r1, Room r2) {
        return Double.compare(distance(this.pt, r1.getSpot()), distance(this.pt, r2.getSpot()));
    }

    /**
     * Calculates the distance between the two points
     * @param p1: point one
     * @param p2: point two
     * @return a double representing the distance between the two points
     */
    private double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
    }
}

