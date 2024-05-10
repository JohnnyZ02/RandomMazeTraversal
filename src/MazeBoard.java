import java.util.Random;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * @author: Johnny Zheng and Jason Reaume
 * @version: 4/24/22
 * 
 *           MazeBoard is a class that constructs a grid of squares by calling on the nested class
 *           MazeSquare. MazeBoard and MazeSquare together contain all required methods to build,
 *           solve, and interact with the maze.
 */

public class MazeBoard extends GridPane {
  Text header = new Text();
  Button nextPathButton = new Button("Next Path");
  Button findPathButton = new Button("Find Path");
  Button clearBoardButton = new Button("Clear Board");
  Button clearPathButton = new Button("Clear Path");

  private boolean gameOver = false;
  private boolean didFindPath = false;

  MazeSquare[][] mazeSpaces;

  /*
   * index: 0 - up, 1 - left, 2 - right, 3 - down; values: 0 - free space, 1 - space occupied
   */
  private int[] path = new int[4];

  private int[] location = {0, 0};

  public MazeBoard(int size, int length, int width) {
    
    mazeSpaces = new MazeSquare[size][size];
    // Creates a grid of squares
    for (int i = 0; i < mazeSpaces.length; i++) {
      for (int j = 0; j < mazeSpaces[i].length; j++) {
        mazeSpaces[i][j] = new MazeSquare(length, width);
        add(mazeSpaces[i][j], i, j, 1, 1);
      }
    }

    this.setStyle("-fx-padding: 10 10 0 10;");
    mazeSpaces[0][0].fillCurrentLocation();
    mazeSpaces[7][7].fillEnd();


    clearPathButton.setOnMouseClicked(e -> {
      gameOver = false;
      didFindPath = false;
      clearPath();
    });

    clearBoardButton.setOnMouseClicked(e -> {
      gameOver = false;
      didFindPath = false;
      clearWalls();
    });

    nextPathButton.setOnMouseClicked(e -> {
      if (!gameOver && !didFindPath) {
        nextPath();
        didFindPath = false;
      }
    });

    /*
     * Uses recursion to find path automatically. Catches StackOverflowError from overloading stack
     * frame in complex maze setups
     */
    findPathButton.setOnMouseClicked(e -> {
      try {
        findPath(3);
      } catch (StackOverflowError noSolution) {
        impossiblePathFind();
      }
      gameOver = false;
      didFindPath = true;
    });
  }

  /**
   * Goes through the grid of squares and resets them to the starting positions
   */
  private void clearWalls() {
    for (int i = 0; i < mazeSpaces.length; i++) {
      for (int j = 0; j < mazeSpaces[i].length; j++) {
        mazeSpaces[i][j].resetBoard();
      }
    }
  }

  /**
   * Looks for gray squares by traversing though the grid
   * 
   * @return count: An integer representing the number of gray squares in the grid
   */
  private int countPathSquares() {
    int count = 0;
    for (int i = 0; i < mazeSpaces.length; i++) {
      for (int j = 0; j < mazeSpaces[i].length; j++) {
        if (mazeSpaces[i][j].getStyle()
            .equals("-fx-border-color: black;-fx-background-color: gray;"))
          count++;
      }
    }
    return count;
  }

  /**
   * Finds a random path instantly using nested recursion
   * 
   * @param numPaths: An integer defining the number of paths available for the current location of
   *        the green square
   */
  private void findPath(int numPaths) {
    if (numPaths != 0 && !gameOver) {
      clearIntArray(path);
      boundaryStatus(location[0], location[1]);
      if (byEnd() == 2 || byEnd() == 3) {
        walk(byEnd());
        pathFoundMessage();
        gameOver = true;
      } else {
        pathStatus(location[0], location[1]);
        if (deadPath()) {
          deathMessage();
          clearPath();
          findPath(2);
        } else {
          walk(randDirection(generateRand()));
        }
      }
      findPath(countAvailablePaths(3, 1));
    }
  }

  /**
   * Recursively returns how many paths are available for traversal
   *
   * @param length: An integer represents the highest value index of the path array (3)
   * @param x: An integer that denotes the number of spaces which are free
   * @return
   */
  private int countAvailablePaths(int length, int x) {
    if (length < 0)
      return 0;
    if (path[length] == 0)
      return (countAvailablePaths(length - 1, x) + x);
    else
      return (countAvailablePaths(length - 1, x));
  }

  /**
   * Displays the next random move in a path and checks whether or not the path is near the end so
   * it can land on the end square. Otherwise, if the path goes dead, the program displays a
   * message.
   */
  private void nextPath() {
    clearIntArray(path);
    boundaryStatus(location[0], location[1]);

    if (byEnd() == 2 || byEnd() == 3) {
      walk(byEnd());
      pathFoundMessage();
      gameOver = true;
    } else {
      pathStatus(location[0], location[1]);
      if (deadPath()) {
        deathMessage();
      } else {
        walk(randDirection(generateRand()));
      }
    }
  }

  // Checks if current location of path is touching the edges of the board
  private void boundaryStatus(int x, int y) {
    if (x == 0) {
      path[1] = 1;
    } else if (x == 7)
      path[2] = 1;
    if (y == 0)
      path[0] = 1;
    else if (y == 7)
      path[3] = 1;
  }

  // Checks which directions path can move on the board
  private void pathStatus(int x, int y) {
    if (path[0] == 0) {
      if (mazeSpaces[x][y - 1].getStyle()
          .equals("-fx-border-color: black;-fx-background-color: white;"))
        path[0] = 0;
      else
        path[0] = 1;
    }
    if (path[1] == 0) {
      if (mazeSpaces[x - 1][y].getStyle()
          .equals("-fx-border-color: black;-fx-background-color: white;"))
        path[1] = 0;
      else
        path[1] = 1;
    }
    if (path[2] == 0) {
      if (mazeSpaces[x + 1][y].getStyle()
          .equals("-fx-border-color: black;-fx-background-color: white;"))
        path[2] = 0;
      else
        path[2] = 1;
    }
    if (path[3] == 0) {
      if (mazeSpaces[x][y + 1].getStyle()
          .equals("-fx-border-color: black;-fx-background-color: white;"))
        path[3] = 0;
      else
        path[3] = 1;
    }
  }

  private int generateRand() {
    Random random = new Random();
    return (random.nextInt(5));
  }

  /**
   * Converts the random number to an available direction, if the direction is unavailable it calls
   * on itself again
   * 
   * @param random: A random integer between 0 and 5 generated by generateRand
   * @return
   */
  private int randDirection(int random) {
    if (random == 0 && path[0] == 0)
      return 0;
    else if (random == 1 && path[1] == 0)
      return 1;
    else if ((random == 2 || random == 3) && (path[2] == 0))
      return 2;
    else if ((random == 4 || random == 5) && (path[3] == 0))
      return 3;
    else
      return randDirection(generateRand());
  }

  /**
   * Moves the green square to its new location and leaves a gray square in its place
   * 
   * @param direction: An int (0,1,2,3) representing the random available direction generated by
   *        randDirection
   */
  private void walk(int direction) {
    mazeSpaces[location[0]][location[1]].fillPath();
    if (direction == 0)
      location[1]--;
    else if (direction == 1)
      location[0]--;
    else if (direction == 2)
      location[0]++;
    else if (direction == 3)
      location[1]++;
    mazeSpaces[location[0]][location[1]].fillCurrentLocation();
  }

  /**
   * Checks if a 0 is present in path i.e. if the space is open
   * 
   * @return isDeadPath: A booelan that represents if there or is not a 0 in path
   */
  private boolean deadPath() {
    boolean isDeadPath = true;
    for (int i = 0; i < path.length; i++) {
      if (path[i] == 0) {
        isDeadPath = false;
      }
    }
    return isDeadPath;
  }

  private void deathMessage() {
    header.setText("No moves available. Died at age " + countPathSquares() + ".");
  }

  private void pathFoundMessage() {
    header.setText("Purpose found at age " + countPathSquares() + ".");
  }

  private void impossiblePathFind() {
    header.setText("No solution. Purpose does not exist. Died at age " + countPathSquares() + ".");
  }

  /**
   * Checks if the green square by the gold square
   * 
   * @return: An int that depicts whether it is or is not by the gold square. 2 - right above gold,
   *          3 - to the left of gold, 0 - not by gold.
   */
  private int byEnd() {
    if (path[2] == 0)
      if (mazeSpaces[location[0] + 1][location[1]].getStyle()
          .equals("-fx-border-color: black;-fx-background-color: gold;")) {
        return 2;
      }
    if (path[3] == 0)
      if (mazeSpaces[location[0]][location[1] + 1].getStyle()
          .equals("-fx-border-color: black;-fx-background-color: gold;")) {
        return 3;
      }
    return 0;
  }

  /**
   * Resets the style of all grid spaces to default values
   */
  private void clearPath() {
    for (int i = 0; i < mazeSpaces.length; i++) {
      for (int j = 0; j < mazeSpaces[i].length; j++) {
        // If the square is gray or green, sets the color to white
        if ((mazeSpaces[i][j].getStyle()
            .equals("-fx-border-color: black;-fx-background-color: gray;")
            || mazeSpaces[i][j].getStyle()
                .equals("-fx-border-color: black;-fx-background-color: green;"))) {
          mazeSpaces[i][j].setStyle("-fx-border-color: black;-fx-background-color: white;");
        }
      }
    }
    // Resets the board
    mazeSpaces[0][0].fillCurrentLocation();
    mazeSpaces[7][7].fillEnd();
    clearHeader();
    clearIntArray(path);
    clearIntArray(location);
  }

  // Used for resetting all values in an integer array to 0
  private void clearIntArray(int[] arr) {
    for (int i = 0; i < arr.length; i++)
      arr[i] = 0;
  }

  private void clearHeader() {
    header.setText("");
  }


  // Nested class MazeSquare extends Pane
  public class MazeSquare extends Pane {
    private boolean marked = false;

    public MazeSquare(int length, int width) {
      this.setPrefSize(length, width);
      this.setStyle("-fx-border-color: black;" + "-fx-background-color: white;");

      this.setOnMouseClicked(e -> {
        // Restricts user from placing walls on green, gold, or gray spaces, and stops user from
        // placing walls once the path is dead
        if (e.getTarget() != mazeSpaces[0][0] && e.getTarget() != mazeSpaces[7][7]
            && !(this.getStyle().equals("-fx-border-color: black;-fx-background-color: green;")
                || this.getStyle().equals("-fx-border-color: black;-fx-background-color: gray;"))
            && deadPath() == false)
          toggleWall();
      });
    }

    // Toggles on and off wall dependind on the marked variable, where true means it is already
    // black and false is white
    private void toggleWall() {
      if (!marked) {
        this.setStyle("-fx-border-color: black;-fx-background-color: black;");
        marked = true;
      } else {
        this.setStyle("-fx-border-color: black;-fx-background-color: white;");
        marked = false;
      }
    }

    // Sets all squares to blank, white squares and calls clearPath to remove all paths
    private void resetBoard() {
      this.setStyle("-fx-border-color: black;-fx-background-color: white;");
      marked = false;
      clearPath();
    }

    private void fillCurrentLocation() {
      this.setStyle("-fx-border-color: black;-fx-background-color: green;");
    }

    private void fillPath() {
      this.setStyle("-fx-border-color: black;-fx-background-color: gray;");
    }

    private void fillEnd() {
      this.setStyle("-fx-border-color: black;-fx-background-color: gold;");
    }
  }
}