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
}
