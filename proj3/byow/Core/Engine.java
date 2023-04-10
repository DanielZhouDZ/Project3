package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    private Random random;
    private char[] actionSequence;
    private TETile[][] output;
    private List<Room> listOfRooms;
    private WeightedQuickUnionUF disjointSet;
    public static final TETile FLOOR = Tileset.FLOOR;
    public static final TETile WALL = Tileset.WALL;
    private static final int RATIO = 300;
    private static final int ROOMSIZE = 7;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        input = input.toUpperCase();
        listOfRooms = new ArrayList<>();
        long seed = 0;
        this.output = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                output[i][j] = Tileset.NOTHING;
            }
        }
        switch (input.charAt(0)) {
            case 'N':
                int i = 1;
                while (i < input.length() && input.charAt(i) != 'S') {
                    i++;
                }
                seed = Long.parseLong(input.substring(1, i));
                this.random = new Random(seed);
                int j = i;
                while (j < input.length() && input.charAt(j) != ':') {
                    j++;
                }
                this.actionSequence = input.substring(i + 1, j).toCharArray();
                generateWorld();
                break;
            case 'L':
                // load()
                break;
            default:
                // quit()
        }
        return output;
    }
    public static void main(String[] args) {
        Engine engine = new Engine();
        Random r = new Random();
        String input = "N" + r.nextInt(10000000) + "S";
        engine.ter.initialize(WIDTH, HEIGHT);
        engine.ter.renderFrame(engine.interactWithInputString(input));
    }

    /**
     * Performs checks to see if tile can be placed at location, placing tile if allowed
     * @param x: x coordinate
     * @param y: y coordinate
     * @param tile: tile to be placed
     */
    private void placeTile(int x, int y, TETile tile) {
        if (0 <= x && x < output.length && 0 <= y && y < output[0].length) {
            if (tile == FLOOR) {
                output[x][y] = tile;
            } else if (output[x][y] != FLOOR) {
                output[x][y] = tile;
            }
        }
    }

    /**
     * Goes to coordinate (x, y) and makes sure that spot is empty.
     * If it is, place a room there with random width and height determined by ROOMSIZE
     * The minimum dimension is 3, unless cut off by edge
     * @param x: x coordinate
     * @param y: y coordinate
     * @return boolean if the room placement was a success or not
     */
    private boolean placeRoom(int x, int y) {
        if (output[x][y].equals(Tileset.NOTHING)) {
            this.listOfRooms.add(new Room(x, y, this.random.nextInt(ROOMSIZE) + 7,
                    this.random.nextInt(ROOMSIZE) + 7, output));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Generates the world by first placing a random number(between 3 and the total area / 300 + 3)
     * of rooms at random spots. It then repeatedly calls connectRoom() on two randomly selected rooms
     * until all the rooms are connected.
     */
    private void generateWorld() {
        int numOfRooms = this.random.nextInt(WIDTH * HEIGHT / RATIO) + 2;
        while (numOfRooms > 0) {
            if (placeRoom(this.random.nextInt(WIDTH - 4), this.random.nextInt(HEIGHT - 4))) {
                numOfRooms--;
            }
        }
        this.disjointSet = new WeightedQuickUnionUF(listOfRooms.size());
        while (!allRoomsConnected()) {
            randomlyConnectRooms();
        }
        for (int i = 0; i < random.nextInt(8); i++) {
            randomlyConnectRooms();
        }
    }
    public void randomlyConnectRooms() {
        int r1 = this.random.nextInt(listOfRooms.size());
        int r2 = this.random.nextInt(listOfRooms.size());
        if (r1 == r2 || disjointSet.connected(r1, r2)) {
            return;
        }
        connectRooms(r1, r2);
    }

    /**
     * Takes two indexes of listOfRooms, selects random points in the rooms' bounding box,
     * and draws a line connecting those two points.
     * @param r1: index of room 1
     * @param r2: index of room 2
     */
    private void connectRooms(int r1, int r2) {
        this.disjointSet.union(r1, r2);
        Point r1Point = listOfRooms.get(r1).getRandomPoint(random);
        Point r2Point = listOfRooms.get(r2).getRandomPoint(random);
        Point diff = new Point(r2Point.getX() - r1Point.getX(), r2Point.getY() - r1Point.getY());
        int modX = (diff.getX() > 0) ? 1 : -1;
        int modY = (diff.getY() > 0) ? 1 : -1;
        for (int i = 0; i < Math.abs(diff.getX()) + 1; i++) {
            drawHallwayTile(r1Point.getX() + i * modX, r1Point.getY());
            if (diff.getY() != 0 && random.nextInt(4) == 0) {
                drawHallwayTile(r1Point.getX()+ i * modX, r1Point.getY() + modY);
                diff.changeY(modY);
                r1Point.changeY(-modY);
            }
        }
        for (int i = 0; i < Math.abs(diff.getY()) + 1; i++) {
            drawHallwayTile(r2Point.getX(), r1Point.getY() + i * modY);
        }
    }

    /**
     * Draws one tile of the hallway, and surrounds it with walls if able
     * @param x: x position of tile
     * @param y: y position of tile
     */
    private void drawHallwayTile(int x, int y) {
        placeTile(x, y, FLOOR);
        placeTile(x + 1, y + 1, WALL);
        placeTile(x - 1, y - 1, WALL);
        placeTile(x + 1, y - 1, WALL);
        placeTile(x - 1, y + 1, WALL);
        placeTile(x + 1, y, WALL);
        placeTile(x - 1, y, WALL);
        placeTile(x, y - 1, WALL);
        placeTile(x, y + 1, WALL);
    }

    /**
     * Checks if all rooms are connected
     * @return true if they are, false otherwise
     */
    private boolean allRoomsConnected() {
        for (int i = 1; i < this.listOfRooms.size(); i++) {
            if (!disjointSet.connected(0, i)) {
                return false;
            }
        }
        return true;
    }
}
