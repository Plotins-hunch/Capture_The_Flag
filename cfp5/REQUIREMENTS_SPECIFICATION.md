# Requirements Specification (PSE FSS24)


## Project - Capture The Flag

### Gameplay

Possible gameplay and rules are limited by the richness of the map template schema (see [here](#map-template-schema) for details).

#### Rules

1. Each team has a base and a flag (or more), which are located in the center of their half or quadrant of the grid.
2. Each team takes turns moving one piece at a time, with the goal of capturing the opponent's flag while protecting their own.
3. The base and flag(s) are considered immovable objects and cannot be moved.
4. Pieces can capture opponent pieces by occupying the same space as them. A piece can only capture an opponent piece if it has an equal or higher attack power than the opponent piece (e.g., P_A >= P_B where P_A is the attack power of some piece of team A and P_B the attack power of some piece of team B).
5. If a piece captures an opponent piece, the opponent piece is removed from the board and cannot be used again.
6. The game can also contain blocks that are randomly placed on the grid. Blocks are considered immovable objects and cannot be moved. Pieces cannot occupy the same space as them, and they cannot make movements over blocks.
7. If a team's flag(s) are captured, the game is over and the team that captured the flag wins.
8. If all pieces on a team are captured, the game is over and the team with remaining pieces wins. 
9. The game can also end if a time limit is reached.

##### Moves

1. A piece can travel less than the maximum number of squares it is allowed to move (as defined in [Directions.java](src%2Fmain%2Fjava%2Fde%2Funimannheim%2Fswt%2Fpse%2Fctf%2Fgame%2Fmap%2FDirections.java)).
2. A piece is not allowed to _jump_ over an opponent's piece on its way.

#### Winner

A team wins (and the game ends) once (either of the following)

* all the opponents flag(s) are captured,
* all the opponents pieces are captured,
* the opponent(s) give up (depending on the number of teams),
* the total game time is reached (see variations below).

A draw (multiple winners) is also possible.

In a two-player game, in case a team cannot make a move anymore, the outcome of the game is a draw. In case of a multi-player game (>= 3 teams) in which at least two teams are still able to make moves, the move of a team (i.e., which is not able to move) is ignored.

#### Game Variations

The following limitations can be optionally set in the map template (see [here](#map-template-schema) for details) -

##### Time Limits

* **total game limit in seconds** - if reached, the winner is the team with the highest number of remaining pieces,
* **the time to move a piece in seconds** - if reached, the move is simply skipped(!) without any actions (i.e., no random move etc.), and it is the turn of the team next.

##### Multi-Flag Capture The Flag (number of flags > 1)

When a piece captures a flag, it has the option to keep moving around the board and capturing more flags. However, before it can do so, the piece must be 'respawned' next to its own base. The respawn square must be randomly chosen and must be in contact with the base. If all squares adjacent to the base are already occupied, then the respawn square must be the closest unoccupied square to the base. This rule ensures that players must carefully consider their moves and cannot simply park a flag-capturing piece next to the opponent's base indefinitely.

### Example

![img.png](img%2Fimg.png)

## Webservice Project Template

1. Client/Server architecture
   1. predefined RESTful interface (several endpoints)
   2. protocol (schema) - JSON representation
   3. Java interface for the game engine
   4. map template schema
2. Webservice project 
   1. based on spring boot project
   2. managed with Maven build tool

### RESTful Interface, Protocol and Schemas

The fully-specified RESTful interface is realized using `spring-web`. It delegates the work to the game engine using the game engine's Java interface (see [Java Interface](#java-interface-game-engine)). It is documented using `spring-doc`. You can find the corresponding controller class here

* [GameSessionController.java](src%2Fmain%2Fjava%2Fde%2Funimannheim%2Fswt%2Fpse%2Fctf%2Fcontroller%2FGameSessionController.java)

You can explore the RESTful API, its documented endpoints and schemas in an interactive manner using OpenAPI/swagger
* http://localhost:8888/swagger-ui/index.html

For this, either run the Java class `CtfApplication` in your IDE, or build and run the entire project. For example,

```bash
# in the repository root
./mvnw clean install
java -jar target/ctf-0.0.1-SNAPSHOT.jar
```

Note: The actual port can be either specified in [application.properties](src%2Fmain%2Fresources%2Fapplication.properties) or via command line (i.e., overrides properties) as follows

```bash
# build jar (see above)
# in the repository root
java -Dserver.port=8888 -jar target/ctf-0.0.1-SNAPSHOT.jar
```

### Java Interface (Game Engine)

Fully implemented game engine following the Java interface

* [Game.java](src%2Fmain%2Fjava%2Fde%2Funimannheim%2Fswt%2Fpse%2Fctf%2Fgame%2FGame.java)

It is documented using classic JavaDoc. Requests to the RESTful API (i.e., controller class) are delegated to this interface, and its returned objects are translated into corresponding responses.


### Grid Format Example: Cell Values (Squares)

The [GameState.java](src%2Fmain%2Fjava%2Fde%2Funimannheim%2Fswt%2Fpse%2Fctf%2Fgame%2Fstate%2FGameState.java) class assumes a certain grid format to link cells to teams' bases, pieces and block.

The following (simplified) example of a possible `String[][] grid` value (5x5) depicts the expected format (represented as a JSON array)  

```json
[
   ["", "", "p:1_2", "", ""],
   ["", "p:1_1", "b:1", "p:1_3", ""],
   ["b", "", "", "", "b"],
   ["", "p:2_1", "b:2", "p:2_3", ""],
   ["", "", "p:2_2", "", ""]
]
```

* `""` - denotes an empty cell (i.e., square)
* `b:1` - denotes the base of team `1`
* `p:1_n` and `p:2_n` - denote the pieces of team `1` (and `2`) where `n` is the piece identifier
* `b` - denotes a block

Using this format for cell values, a piece, for instance, can be resolved to a piece object ([Piece.java](src%2Fmain%2Fjava%2Fde%2Funimannheim%2Fswt%2Fpse%2Fctf%2Fgame%2Fstate%2FPiece.java)).


## Requirements

### Functional Requirements

1. **Backend** - Develop two independent systems (i.e., independent execution)
   1. Game engine (extend the existing webservice project)
   2. Player clients
      1. three autonomous AI (machine) players (note: AI in the broad sense including your developed algorithms derived from gameplay etc.)
      2. human player client (accepts user inputs)
2. **Frontend** - Graphical user interface (GUI) using JavaFX
   1. Player client GUI - visualize gameplay
      1. visualize autonomous gameplay of AI players
      2. allow human player interactions (user inputs)
      3. allow for custom theming of the board and pieces (two different themes mandatory)
   2. Map editor GUI - create maps on your own
      1. template engine (note: map template schema is predefined!)
      2. let users design new maps visually
      3. render existing maps based on given map template schema
      4. create three _interesting_ and playable maps to demonstrate your systems
         * in general, you can assign custom images to the predefined types of your pieces
         * for unknown types (i.e., not part of your predefined maps), you can assign random colors automatically and show a legend, for instance
3. **Testing** - Demonstrate the correctness of your backend systems/components
      1. (automated) unit testing of all game engine operations (game flow, logic, moves, state etc.) with JUnit5 (https://junit.org/junit5/)
      2. integration testing with your three predefined maps (see above)
      3. demonstrate that your two systems (game engine service and clients) work with any valid map template
         1. this is the prerequisite to compete with other teams
      4. demonstrate that your player clients act autonomously given a webservice that realizes the RESTful interface
      5. automated GUI testing (i.e., creating a set of automated tests) is not necessary

### Non-Functional Requirements

The final software products are required to support the following environments for their execution
* Java JDK 17 (OpenJDK or similar like Eclipse Temurin etc.)
* JavaFX running on all major operating systems (Windows, MacOS, Linux)
* Code and GUI language: english

## Competition (to be announced)

Goal – Compete with your best AI player (your choice) in the final course tournament!

