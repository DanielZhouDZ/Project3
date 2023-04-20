package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;
import java.util.PriorityQueue;

public class Room {
    private final Point spot;
    private final int index;
    private final int width;
    private final int height;
    private final PriorityQueue<Room> closestRooms;

    public Room(int x, int y, int width, int height, TETile[][] map, int index) {
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
        this.closestRooms = new PriorityQueue<>(new RoomComparator(this.spot));
        this.index = index;
    }

    /**
     * Generates a random point within the room, used to create the start or end point for a hallway
     * @param rand: the Random object used to randomly select the x and y coordinate
     * @return a point object representing the randomly selected tile
     */
    public Point getRandomPoint(Random rand) {
        int x = rand.nextInt(this.width - 3) + spot.getX() + 1;
        int y = rand.nextInt(this.height - 3) + spot.getY() + 1;
        return new Point(x, y);
    }

    /**
     * Returns the point corresponding to the bottom left corner of the room
     * @return a point object representing tile at the bottom left corner of the room
     */
    public Point getSpot() {
        return spot;
    }

    /**
     * Returns the room closest to this room through the use of a priority queue
     * @return room object representing the closest room, or null if there are no rooms in the priority queue
     */
    public Room getClosestRoom() {
        if (!closestRooms.isEmpty()) {
            return closestRooms.poll();
        }
        return null;
    }

    /**
     * Adds a room to the priority queue
     * @param r: room to be added
     */
    public void addToClosestRooms(Room r) {
        this.closestRooms.offer(r);
    }

    /**
     * Returns the current room's index in the listOfRooms list
     * @return
     */
    public int getIndex() {
        return index;
    }

    public Point getCenter() {
        int x_middle = getSpot().getX() + width / 2;
        int y_middle = getSpot().getY() + height / 2;
        Point center = new Point(x_middle, y_middle);

        return center;
    }
}
