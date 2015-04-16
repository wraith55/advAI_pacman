package pacman;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.javaml.classification.Classifier;

/**
 * Main.fx created on 2008-12-20, 12:02:26 <br>
 * Main.java created October 2011
 *
 * @see <a href="http://www.javafxgame.com">http://www.javafxgame.com</a>
 * @author Henry Zhang
 * @author Patrick Webster
 */
public class Main extends Application {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) 
  {
    Application.launch(Main.class, args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Pac-Man by Henry Zhang www.javafxgame.com and Patrick Webster");
    primaryStage.setWidth(MazeData.calcGridX(MazeData.GRID_SIZE_X + 2));
    primaryStage.setHeight(MazeData.calcGridY(MazeData.GRID_SIZE_Y + 4));

    final Group root = new Group();
    final Scene scene = new Scene(root);
    
    VickersInstanceMaker instMaker = new VickersInstanceMaker();
    Classifier foo = new Foo();
    //root.getChildren().add(new Maze("fake_name", 10, foo, instMaker));
    root.getChildren().add(new Maze("fake_name", 10, null, null));
    
    primaryStage.setScene(scene);
    primaryStage.show();
  }

}
