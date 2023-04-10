package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Room {
    private final Point spot;
    private final int width;
    private final int height;

    public Room(int x, int y, int width, int height, TETile[][] map) {
        width = Math.min(map.length, x + width);
        height = Math.min(map[0].length, y + height);
        for (int i = x; i < width; i++) {
            for (int j = y; j < height; j++) {
                if ((i == x || i == width - 1 || j == y
                        || j == height - 1) && (map[i][j] == Tileset.NOTHING || map[i][j] == Tileset.WALL)) {
                    map[i][j] = Tileset.WALL;
                } else {
                    map[i][j] = Tileset.FLOOR;
                }
            }
        }
        this.width = width - x;
        this.height = height - y;
        this.spot = new Point(x, y);
    }
    public Point getRandomPoint(Random rand) {
        int x = rand.nextInt(this.width - 3) + spot.getX() + 1;
        int y = rand.nextInt(this.height - 3) + spot.getY() + 1;
        return new Point(x, y);
    }
}
