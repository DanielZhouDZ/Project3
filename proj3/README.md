# Build Your Own World Design Document

**Partner 1:**
Chengming Li
**Partner 2:**
Daniel Zhou
## Classes and Data Structures
- [x] List(Room) rooms: the list of rooms
- [x] WQU: each number representing the room at that index in rooms, used to determine if all the rooms are reachable or not
- [x] Room
  - Point spot: bottom left corner of room
  - int width: width of room
  - int height: height of room
  - int index: the index of the room in the list of rooms
  - [x] Priority Queue: to order the other rooms in order of distance from this room, 
to draw hallways between this room and the room closest to it
- [x] Point:
  - double x: x coordinate of point
  - double y: y coordinate of point
- [x] Room Comparator: used to order the priority queue in Room 
## Algorithms
- [x] generateWorld(): 
  - randomly choose a number of rooms to generate. For each room, call the placeRoom() method on a randomly chosen unoccupied space. Then, while not all rooms are connected in the WQU, call connectRandomRooms()
- [x] connectRandomRooms(): randomly chooses a room, and calls connectRooms() on it and the room closest to it
- [x] placeRoom(int x, int y): 
  - goes to the coordinate (x, y). If that tile is unoccupied, draw a room with a random width and height there, and create a Room object and add it to the list of Rooms
- [x] connectRooms(Room r1, Room r2): 
  - pick a random spot in each room, then draw a hallway to connect those random spots. The hallway will only overwrite wall and empty tiles, not floor tiles. Then, in the WQU, connect the two rooms
- [x] allRoomsConnected():
  - Returns true if all the rooms are connected, false otherwise
- drawHallwayTile(int x, int y):
  - Uses placeTile() to draw one tile in the hallway segment(a floor tile surrounded by wall tiles)
- placeTile(int x, int y, TETile tile):
  - checks to make sure spot is valid, then places a tile at the spot in the map.
  - Different conditions apply depending on the tile
## Persistence
To save the file, we'll write the current seed, plus any inputs made by the player, to a file.
<br>To load the file, we'll just regenerate the world based on the seed, then reenact all the inputs

## Phase 2
- [x] Player with avatar, input control
- [ ] Save/persistence
  - [ ] When the player enters :Q, quit and save
  - [ ] When the player presses L, the world should load from save
    - If no previous save, quit with no errors
- [ ] HUD that shows character under mouse
- [ ] Modify interactWithInputString() to handle movement, saving, and loading
- [ ] Ambition Score