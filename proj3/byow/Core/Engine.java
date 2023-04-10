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
    public static final int HEIGHT = 30;
    private Random random;
    private char[] actionSequence;
    private TETile[][] output;
    private List<Room> listOfRooms;
    private WeightedQuickUnionUF disjointSet;
    public static final TETile FLOOR = Tileset.FLOOR;
    public static final TETile WALL = Tileset.WALL;

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
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
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
                this.actionSequence = input.substring(i + 1).toCharArray();
                new Room(0, 0, 5, 5, output);
                // generateWorld();
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
        String input = "N1S";
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
     * Goes to coordinate (x, y) and makes sure that spot is empty. If it is, place a room there with random width and height between 3 and 15
     * @param x: x coordinate
     * @param y: y coordinate
     * @return boolean if the room placement was a success or not
     */
    private boolean placeRoom(int x, int y) {
        if (output[x][y].equals(Tileset.NOTHING)) {
            this.listOfRooms.add(new Room(x, y, this.random.nextInt(12)+3, this.random.nextInt(12)+3, output));
            return true;
        } else {
            return false;
        }
    }
    private void generateWorld() {
        int numOfRooms = this.random.nextInt(WIDTH * HEIGHT / 300)+3;
        while (numOfRooms > 0) {
            if (placeRoom(this.random.nextInt(WIDTH-2), this.random.nextInt(HEIGHT-2))) {
                numOfRooms --;
            }
        }
    }
    private void connectRooms(Room r1, Room r2) {
        Point r1Point = r1.getRandomPoint(random);
        Point r2Point = r2.getRandomPoint(random);
        Point diff = new Point(r1Point.getX() - r2Point.getX(), r1Point.getY() - r2Point.getY());
        for (int i = 0; i < Math.abs(diff.getX()) + 1; i++) {
            drawHallwayTile(r1Point.getX(), r1Point.getY());
        }
    }
    private void drawHallwayTile(int x, int y) {
        placeTile(x, y, FLOOR);
        placeTile(x+1, y+1, WALL);
        placeTile(x-1, y-1, WALL);
        placeTile(x+1, y-1, WALL);
        placeTile(x-1, y+1, WALL);
        placeTile(x+1, y, WALL);
        placeTile(x-1, y, WALL);
        placeTile(x, y-1, WALL);
        placeTile(x, y+1, WALL);
    }
}
