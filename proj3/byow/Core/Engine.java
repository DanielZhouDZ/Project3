package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Character.isDigit;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    private Random random;
    private TETile[][] myWorld;
    private List<Room> listOfRooms;
    private WeightedQuickUnionUF disjointSet;
    public static final TETile FLOOR = Tileset.FLOOR;
    public static final TETile WALL = Tileset.WALL;
    public static final TETile EXIT = Tileset.EXIT;
    private static final int RATIO = 175;
    private static final int ROOMSIZE = 8;
    private static final int RANDOM = 10000;
    private TETile past;
    private static final int SMALLFONTSIZE = 20;
    private static final int LARGEFONTSIZE = 30;
    private static final int CANVASRATIO = 16;
    private static final int PAUSETIME = 100;
    private boolean lit;
    private static final DateTimeFormatter TIMEFORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private Point exit;
    private static final TETile[] AVATARS = new TETile[] {
        new TETile('^', Color.white, Color.black, "you"),
        new TETile('<', Color.white, Color.black, "you"),
        new TETile('>', Color.white, Color.black, "you"),
        new TETile('V', Color.white, Color.black, "you"),
    };

    private Point avatarPosition;
    String tileBelow;

    private List<Character> keyActions;

    private long seed;

    private String savedActions;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        this.lit = false;

        displayStartScreen();

        this.keyActions = null;

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                if (ch == 'N' || ch == 'n') {
                    enterSeedScreen();
                    drawWorld();
                    playGame(false);
                } else if (ch == 'L' || ch == 'l') {
                    loadGameData();
                    drawWorld();
                    playGame(true);
                } else if (ch == 'Q' || ch == 'q') {
                    System.exit(0);
                }
            }
        }
    }

    private void drawWorld() {

        this.listOfRooms = new ArrayList<>();
        this.keyActions = new ArrayList<>();

        this.myWorld = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                myWorld[i][j] = Tileset.NOTHING;
            }
        }

        this.random = new Random(seed);
        generateWorld();
        spawnAvatar();
        spawnExit();
    }

    private void replayActions(boolean pause) {
        ter.renderFrame(this.myWorld);
        headUpDisplay();
        for (int i = 0; i < this.savedActions.length(); i++) {
            moveAvatar(this.savedActions.charAt(i));
            if (pause) {
                ter.renderFrame(this.myWorld);
                headUpDisplay();
                StdDraw.pause(PAUSETIME);
            }
        }
    }

    private void playGame(boolean replay) {
        boolean colonTyped = false;
        boolean gameOver = false;

        ter.initialize(WIDTH, HEIGHT + 2);
        if (replay) {
            replayActions(true);
        }
        while (!gameOver) {
            if (StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                if (ch == ':') {
                    colonTyped = true;
                    continue;
                }

                if (colonTyped) {
                    if (ch == 'Q' || ch == 'q') {
                        saveGame();
                        gameOver = true;
                    } else {
                        colonTyped = false;
                    }
                } else {
                    moveAvatar(ch);
                }
            }
            ter.renderFrame(this.myWorld);
            headUpDisplay();
        }

        System.exit(0);
    }

    private void headUpDisplay() {
        StdDraw.setPenColor(Color.WHITE);
        Font fontSmall = new Font("Monaco", Font.PLAIN, SMALLFONTSIZE);
        StdDraw.setFont(fontSmall);

        LocalDateTime currentTime = LocalDateTime.now();

        if ((int) StdDraw.mouseX() < WIDTH && (int) StdDraw.mouseY() < HEIGHT) {
            TETile tile = myWorld[(int) StdDraw.mouseX()][(int) StdDraw.mouseY()];
            if (tile.description().equals("you")) {
                tileBelow = "AVATAR";
            } else if (tile.description().equals("wall")) {
                tileBelow = "WALL";
            } else if (tile.description().equals("floor") || tile.description().equals("light")) {
                tileBelow = "FLOOR";
            } else if (tile.description().equals("nothing")) {
                tileBelow = "NOTHING";
            } else {
                tileBelow = tile.description().toUpperCase();
            }
        }
        StdDraw.textLeft((float) 1, HEIGHT + 1, tileBelow);
        StdDraw.text((float) WIDTH / 2, HEIGHT + 1, "Seed: " + seed);
        StdDraw.textRight((float) WIDTH - 1, HEIGHT + 1, "Current Time: " + currentTime.format(TIMEFORMATTER));
        StdDraw.show();
    }

    private void displayStartScreen() {
        StdDraw.setCanvasSize(WIDTH * CANVASRATIO, HEIGHT * CANVASRATIO);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);

        drawMenu();
    }

    private void drawMenu() {
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, LARGEFONTSIZE);
        StdDraw.setFont(fontBig);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 + 7, "CS61B: THE GAME");

        Font fontSmall = new Font("Monaco", Font.PLAIN, SMALLFONTSIZE);
        StdDraw.setFont(fontSmall);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, "New Game (N)");
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 - 2, "Load Game (L)");
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 - 4, "Quit (Q)");
    }

    private void loadGameData() {
        try {
            File data = new File("gameData.txt");
            Scanner sc = new Scanner(data);

            if (sc.hasNextLine()) {
                String firstLine = sc.nextLine();
                if (firstLine.length() == 0) {
                    System.exit(0);
                }
                this.seed = Long.parseLong(firstLine);
            } else {
                System.exit(0);
            }

            if (sc.hasNextLine()) {
                this.savedActions = sc.nextLine();
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred when loading the game");
            e.printStackTrace();
        }
    }

    private void enterSeedScreen() {
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 - 7, "Enter Seed:");

        boolean sEntered = false;
        String typed = "";

        while (!sEntered) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if ((key == 'S' || key == 's') && typed.length() > 0) {
                    this.seed = Long.parseLong(typed);
                    sEntered = true;
                }
                if (isDigit(key)) {
                    typed = typed + key;
                    StdDraw.clear(Color.BLACK);
                    drawMenu();
                    StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 - 7, "Enter Seed:");
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 - 9, typed);
                }
            }
        }
    }

    private void saveGame() {
        try {
            FileWriter fw = new FileWriter("gameData.txt", false);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(String.valueOf(this.seed));
            bw.newLine();
            StringBuilder sb = new StringBuilder();
            for (Character keyAction : keyActions) {
                sb.append(keyAction);
            }
            bw.write(sb.toString());
            bw.newLine();

            bw.close();
        } catch (IOException e) {
            System.out.println("Error occurred while saving game data");
        }
    }

    private void moveAvatar(char ch) {
        if (ch == 'W' || ch == 'w') {
            moveAvatarTo(avatarPosition.getX(), avatarPosition.getY() + 1, AVATARS[0]);
            keyActions.add('W');
        } else if (ch == 'A' || ch == 'a') {
            moveAvatarTo(avatarPosition.getX() - 1, avatarPosition.getY(), AVATARS[1]);
            keyActions.add('A');
        } else if (ch == 'S' || ch == 's') {
            moveAvatarTo(avatarPosition.getX(), avatarPosition.getY() - 1, AVATARS[3]);
            keyActions.add('S');
        } else if (ch == 'D' || ch == 'd') {
            moveAvatarTo(avatarPosition.getX() + 1, avatarPosition.getY(), AVATARS[2]);
            keyActions.add('D');
        } else if (ch == 'e' || ch == 'E') {
            toggleLights();
            keyActions.add('E');
        }
    }

    private void spawnExit() {
        Room exitRoom = this.listOfRooms.get(random.nextInt(this.listOfRooms.size() - 1) + 1);
        this.exit = exitRoom.getRandomPoint(random);
    }

    private void moveAvatarTo(int x, int y, TETile avatar) {
        if (x >= 0 && x < myWorld.length && y >= 0 && y < myWorld[0].length && myWorld[x][y] != WALL) {
            myWorld[avatarPosition.getX()][avatarPosition.getY()] = past;
            past = myWorld[x][y];
            myWorld[x][y] = avatar;
            avatarPosition.setX(x);
            avatarPosition.setY(y);
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.)
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

        switch (input.charAt(0)) {
            case 'N':
                int i = 1;
                while (i < input.length() && input.charAt(i) != 'S') {
                    i++;
                }

                this.seed = Long.parseLong(input.substring(1, i));

                drawWorld();

                boolean colonTyped = false;

                String actions = input.substring(i + 1, input.length());
                for (int j = 0; j < actions.length(); j++) {
                    char ch = actions.charAt(j);
                    if (ch == ':') {
                        colonTyped = true;
                        continue;
                    }

                    if (colonTyped) {
                        if (ch == 'Q') {
                            saveGame();
                            break;
                            //System.exit(0);
                        } else {
                            colonTyped = false;
                        }
                    } else {
                        moveAvatar(ch);
                    }

                }
                break;

            case 'L':
                // load()
                loadGameData();
                drawWorld();
                replayActions(false);

                int k = 1;
                boolean colonTyped2 = false;

                String actions2 = input.substring(k, input.length());
                for (int j = 0; j < actions2.length(); j++) {
                    char ch = actions2.charAt(j);
                    if (ch == ':') {
                        colonTyped2 = true;
                        continue;
                    }

                    if (colonTyped2) {
                        if (ch == 'Q' || ch == 'q') {
                            saveGame();
                            System.exit(0);
                        } else {
                            colonTyped2 = false;
                        }
                    } else {
                        moveAvatar(ch);
                    }
                }
                break;
            default:
                break;
        }
        return myWorld;
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        Random r = new Random();
        String input = "N" + r.nextInt(RANDOM) + "S";
        engine.ter.initialize(WIDTH, HEIGHT);
        //engine.ter.renderFrame(engine.interactWithInputString(input));
        engine.ter.renderFrame(engine.interactWithInputString("N123SAAAAWWWWS:q"));
    }

    /**
     * Performs checks to see if tile can be placed at location, placing tile if allowed
     * @param x: x coordinate
     * @param y: y coordinate
     * @param tile: tile to be placed
     */
    private void placeTile(int x, int y, TETile tile) {
        if (0 <= x && x < myWorld.length && 0 <= y && y < myWorld[0].length) {
            if (tile == FLOOR) {
                myWorld[x][y] = tile;
            } else if (myWorld[x][y] != FLOOR) {
                myWorld[x][y] = tile;
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
        int height = this.random.nextInt(ROOMSIZE) + 5;
        int width = this.random.nextInt(ROOMSIZE) + 5;
        int endWidth = Math.min(x + width, myWorld.length - 1);
        int endHeight = Math.min(y + height, myWorld[0].length - 1);
        if (myWorld[x][y].equals(Tileset.NOTHING) && myWorld[endWidth][endHeight].equals(Tileset.NOTHING)
                && myWorld[(x + endWidth) / 2][(y + endHeight) / 2].equals(Tileset.NOTHING)
                && myWorld[endWidth][y].equals(Tileset.NOTHING)
                && myWorld[x][endHeight].equals(Tileset.NOTHING)) {
            Room r = new Room(x, y, width,
                    height, myWorld, this.listOfRooms.size());
            for (Room room : listOfRooms) {
                room.addToClosestRooms(r);
            }
            this.listOfRooms.add(r);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Generates the world by first placing a random number(between 8 and the total area / 125 + 8)
     * of rooms at random spots. It then repeatedly calls randomlyConnectRooms() until all rooms are connected
     */
    private void generateWorld() {
        int numOfRooms = this.random.nextInt(WIDTH * HEIGHT / RATIO) + 8;
        while (numOfRooms > 0) {
            if (placeRoom(this.random.nextInt(WIDTH - 4), this.random.nextInt(HEIGHT - 4))) {
                numOfRooms--;
            }
        }
        this.disjointSet = new WeightedQuickUnionUF(listOfRooms.size());
        while (!allRoomsConnected()) {
            randomlyConnectRooms();
        }
        for (int i = 0; i < random.nextInt(4); i++) {
            randomlyConnectRooms();
        }
        for (Room r : listOfRooms) {
            r.spawnLight(random);
        }
    }

    /**
     * Randomly selects a room, then connects it with the room closest to it.
     */
    public void randomlyConnectRooms() {
        int r1 = this.random.nextInt(listOfRooms.size());
        Room r2 = this.listOfRooms.get(r1).getClosestRoom();
        if (r2 == null || this.disjointSet.connected(r1, r2.getIndex())) {
            return;
        }
        connectRooms(r1, r2.getIndex());
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
            if (diff.getY() != 0 && random.nextInt(9) == 0) {
                drawHallwayTile(r1Point.getX() + i * modX, r1Point.getY() + modY);
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

    private void spawnAvatar() {
        Room room = listOfRooms.get(0);
        avatarPosition = new Point(room.getCenter().getX(), room.getCenter().getY());
        this.past = myWorld[avatarPosition.getX()][avatarPosition.getY()];
        myWorld[avatarPosition.getX()][avatarPosition.getY()] = AVATARS[0];
    }
    private void toggleLights() {
        TETile pastTile;
        if (this.lit) {
            if (this.avatarPosition.equals(exit)) {
                System.out.println("Game Over!");
                System.exit(0);
            }
            for (Room r : listOfRooms) {
                this.past = r.closeLights(myWorld);
            }
            myWorld[exit.getX()][exit.getY()] = FLOOR;
        } else {
            for (Room r : listOfRooms) {
                pastTile = r.openLights(myWorld, past);
                if (pastTile != null) {
                    this.past = pastTile;
                }
            }
            if (!this.avatarPosition.equals(exit)) {
                myWorld[exit.getX()][exit.getY()] = EXIT;
            } else {
                this.past = EXIT;
            }
        }
        this.lit = !this.lit;
    }
}
