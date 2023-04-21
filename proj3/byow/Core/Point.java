package byow.Core;

import java.util.List;

public class Point {
    private int x;
    private int y;
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public void changeX(int change) {
        this.x = x - change;
    }
    public void changeY(int change) {
        this.y = y - change;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    @Override
    public String toString() {
        return this.x + ", " + this.y;
    }
    public static boolean pointInList(List<Point> list, int x, int y) {
        for (Point p : list) {
            if (p.getY() == y && p.getX() == x) {
                return true;
            }
        }
        return false;
    }
}
