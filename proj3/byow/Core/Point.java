package byow.Core;

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
    @Override
    public String toString() {
        return this.x + ", " + this.y;
    }
}
