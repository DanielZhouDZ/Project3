package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Room {
    private final Point spot;
    private final int index;
    private final int width;
    private final int height;
    private final PriorityQueue<Room> closestRooms;
    private Map<Integer, List<Point>> lights;
    private static final int LIGHTRANGE = 8;
    private static Map<Integer, TETile> LEVELTOTILES = null;
    private static Map<TETile, Integer> TILESTOLEVEL = null;
    private static final Color COLOR = new Color(128, 192, 128);
    private static final Color STARTINGCOLOR = new Color(148, 222, 148);
    private static final double COLORMULT = 1.5;
    private static final int START = 148;
    private final TETile[][] map;

    public Room(int x, int y, int width, int height, TETile[][] map, int index) {
        if (LEVELTOTILES == null) {
            LEVELTOTILES = new HashMap<>();
            TILESTOLEVEL = new HashMap<>();
            LEVELTOTILES.put(LIGHTRANGE, new TETile('*', COLOR, STARTINGCOLOR,
                    "light"));
            TILESTOLEVEL.put(LEVELTOTILES.get(LIGHTRANGE), LIGHTRANGE);
            int diff = START / LIGHTRANGE - 1;
            for (int i = LIGHTRANGE - 1; i > 0; i--) {
                LEVELTOTILES.put(i, new TETile('.', COLOR,
                        new Color(diff * i, (int) (diff * i * COLORMULT), diff * i), "light"));
                TILESTOLEVEL.put(LEVELTOTILES.get(i), i);
            }
        }
        this.map = map;
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
        this.lights = new HashMap<>();
        for (int i = LIGHTRANGE; i > 0; i--) {
            lights.put(i, new ArrayList<>());
        }
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
        int xMiddle = getSpot().getX() + width / 2;
        int yMiddle = getSpot().getY() + height / 2;
        Point center = new Point(xMiddle, yMiddle);

        return center;
    }

    public void spawnLight(Random rand) {
        Queue<Point> q = new LinkedList<>();
        List<Point> visited = new ArrayList<>();
        q.add(getRandomPoint(rand));
        visited.add(q.peek());
        Point pt;
        for (int i = LIGHTRANGE; i > 0; i--) {
            int s = q.size();
            for (int j = 0; j < s; j++) {
                pt = q.poll();
                this.lights.get(i).add(pt);
                spreadLight(pt, q, visited);
            }
        }
    }
    public TETile openLights(Point playerPT, TETile[][] world) {
        TETile output = null;
        for (int i : lights.keySet()) {
            for (Point pt : lights.get(i)) {
                if (world[pt.getX()][pt.getY()] == Tileset.AVATAR) {
                    output = LEVELTOTILES.get(i);
                } else if (TILESTOLEVEL.containsKey(world[pt.getX()][pt.getY()])) {
                    world[pt.getX()][pt.getY()] = LEVELTOTILES.
                            get(Math.min(TILESTOLEVEL.get(world[pt.getX()][pt.getY()]) + i, LIGHTRANGE));
                } else {
                    world[pt.getX()][pt.getY()] = LEVELTOTILES.get(i);
                }
            }
        }
        return output;
    }
    public TETile closeLights(Point playerPT, TETile[][] grid) {
        for (int i : lights.keySet()) {
            for (Point pt : lights.get(i)) {
                if (grid[pt.getX()][pt.getY()] != Tileset.AVATAR) {
                    grid[pt.getX()][pt.getY()] = Tileset.FLOOR;
                }
            }
        }
        return Tileset.FLOOR;
    }
    private void spreadLight(Point pt, Queue<Point> q, List<Point> visited) {
        int x = pt.getX();
        int y = pt.getY();
        Point light;
        if (!Point.pointInList(visited, x - 1, y) && x > 0 && map[x - 1][y] == Tileset.FLOOR) {
            light = new Point(x - 1, y);
            q.add(light);
            visited.add(light);
        }
        if (!Point.pointInList(visited, x + 1, y) && x < map.length && map[x + 1][y] == Tileset.FLOOR) {
            light = new Point(x + 1, y);
            q.add(light);
            visited.add(light);
        }
        if (!Point.pointInList(visited, x, y - 1) && y > 0 && map[x][y - 1] == Tileset.FLOOR) {
            light = new Point(x, y - 1);
            q.add(light);
            visited.add(light);
        }
        if (!Point.pointInList(visited, x, y + 1) && y < map[0].length && map[x][y + 1] == Tileset.FLOOR) {
            light = new Point(x, y + 1);
            q.add(light);
            visited.add(light);
        }
    }
}
