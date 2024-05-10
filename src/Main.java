import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;


/**
 * @author: Johnny Zheng and Jason Reaume
 * @version: 4/24/22
 * 
 * 
 *           This project creates a grid where the user can place walls to make a maze. The program
 *           will either instantaneously find a random solution to it or randomly traverse the maze
 *           through clicks of a button.
 * 
 *           Maze is the class that sets up the scene and layout of the project by calling on the
 *           constructor in MazeBoard.
 * 
 * 
 *           Big O Analysis:
 *           
 *           findPath is a recursive method that call on itself until it reaches
 *           the desired location. It contains a recursive method that will always run 4 times. As
 *           the solution from findPath are random, there is no upper limit to the number of times findPath can
 *           run. Furthermore, if the user creates an unsolvable maze, findPath will run until the
 *           the try catch recognizes a StackOverflow Error. Therefore, the big O time complexity for
 *           findPath is unfortunately O(2^n).
 * 
 */

public class Main extends Application {

  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Maze Solver");
    primaryStage.setWidth(640);
    primaryStage.setHeight(800);
    
    // Calls the constructor in MazeBoard to create the grid
    MazeBoard mazePane = new MazeBoard(8, 100, 100);

    BorderPane borderPane = new BorderPane(mazePane);

    Scene scene = new Scene(borderPane);
    
    // Sets the header right above the grid
    mazePane.header.setX(400);
    mazePane.header.setY(50);
    mazePane.header.setText("");
    mazePane.header.setFont(Font.font(20));
    
    // Buttons
    HBox hBox = new HBox();
    hBox.getChildren().addAll(mazePane.findPathButton, mazePane.nextPathButton,
        mazePane.clearPathButton, mazePane.clearBoardButton);
    borderPane.setBottom(hBox);
    hBox.setSpacing(10);
    hBox.setPadding(new Insets(10));
    hBox.setAlignment(Pos.BOTTOM_CENTER);

    borderPane.setBottom(hBox);
    borderPane.setTop(mazePane.header);
    BorderPane.setAlignment(mazePane.header, Pos.TOP_CENTER);
    BorderPane.setMargin(mazePane.header, new Insets(10, 0, 0, 0));

    primaryStage.setScene(scene);
    primaryStage.setAlwaysOnTop(true);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}