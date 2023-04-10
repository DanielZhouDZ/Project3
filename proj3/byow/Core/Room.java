package byow.Core;

public class Room {
    private Point center;
    private int width;
    private int height;

    public Room(int x, int y, int width, int height) {
        this.center = new Point(x, y);
        this.width = width;
        this.height = height;
    }
}
