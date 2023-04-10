# Build Your Own World Design Document

**Partner 1:**
Chengming Li
**Partner 2:**
Daniel Zhou
## Classes and Data Structures
- [x] List(Room) rooms: the list of rooms
- [x] WQU: each number representing the room at that index in rooms, used to determine if all the rooms are reachable or not
- [x] Room
  - Point center: center of the room 
  - int width: width of room
  - int height: height of room
- [x] Point:
  - int x: x coordinate of point
  - int y: y coordinate of point
## Algorithms
- [ ] generateWorld(): 
  - randomly choose a number of rooms to generate. For each room, call the placeRoom() method on a randomly chosen unoccupied space. Then, while not all rooms are connected in the WQU, call connectRooms() on two randomly selected rooms
- [ ] placeRoom(int x, int y): 
  - goes to the coordinate (x, y). Then, draw a room with a random width and height there, and create a Room object and add it to the list of Rooms
- [ ] connectRooms(Room r1, Room r2): 
  - pick a random spot in each room, then draw a hallway to connect those random spots. The hallway will only overwrite wall and empty tiles, not floor tiles. Then, in the WQU, connect the two rooms
## Persistence
To save the file, we'll write the current seed, plus any inputs made by the player, to a file.
<br>To load the file, we'll just regenerate the world based on the seed, then reenact all the inputs