package pacman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KDtreeKNN;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Instance;

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
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Pac-Man by Henry Zhang www.javafxgame.com and Patrick Webster");
    primaryStage.setWidth(MazeData.calcGridX(MazeData.GRID_SIZE_X + 2));
    primaryStage.setHeight(MazeData.calcGridY(MazeData.GRID_SIZE_Y + 4));

    final Group root = new Group();
    final Scene scene = new Scene(root);
    
    final int k = 3;
    Classifier c = new KDtreeKNN(k);
    PacInstanceMaker instMaker = new RonaldInstanceMaker();

    
    // create maze before training, which initializes the MazeData data structures
    //Maze maze = new Maze("foobar", 50, null, null);         // human controller
    Maze maze = new Maze("kdKNN3_controller", 10, c, instMaker);  // AI controller
        
    System.out.println("current dir = " + System.getProperty("user.dir"));
    
    int minSize = 250;
    
    List<Instance> instances = PacML.readInstancesFromDir("data/vickers_data/main", "data/vickers_data/dots", 
                                                          "data/vickers_data/magic_dots", instMaker, minSize) ;
    System.out.println("instances size = " + instances.size() ) ;
    
    //cross_validate(instances);
    
    //PacML.makeBasicClassifiers(instances, "vickersOnly_minSize" + minSize + "_");
    
    //System.exit(0);
    
    //Classifier c = PacML.readClassifierFile("vickersOnly_knn1_vickersOnly_minSize250_", 25);

    System.out.println("building KDtreeKNN" + k + " classifier...");
    c.buildClassifier(PacML.makeDataset(instances));
    
    root.getChildren().add(maze);
    
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  public static void cross_validate(List<Instance> instances) throws Exception
  {
    for (int cross_idx = 0; cross_idx < 10; cross_idx++)
    {
        List<Instance> training = new ArrayList<>();
        List<Instance> testing = new ArrayList<>();
        
        for (int i = 0; i < instances.size(); i++)
        {
            if (i % 10 == cross_idx)
                testing.add(instances.get(i));
            else
                training.add(instances.get(i));
        }
        
        Classifier c = new KNearestNeighbors(5);
        System.out.println("building classifier from dataset...cross_idx = " + cross_idx);
        c.buildClassifier(PacML.makeDataset(training));
        
        int left = 0, up = 0, down = 0, right = 0;
        int num_correct = 0;
        for (Instance inst : testing)
        {
            DIRECTION actual = (DIRECTION) inst.classValue();
            DIRECTION predicted = (DIRECTION) c.classify(inst);
            if (actual == predicted)
                num_correct++;
            switch(predicted)
            {
                case LEFT:
                    left++;
                    break;
                case RIGHT:
                    right++;
                    break;
                case DOWN:
                    down++;
                    break;
                case UP:
                    up++;
                    break;
            }
        }
        System.out.println("trial " + cross_idx + " done");
        System.out.println("num_correct = " + num_correct + ", out of " + testing.size() 
                + ", rate is " + ((double) num_correct / testing.size()));
        System.out.println("left = " + left + ", right = " + right + ", down = " + down + ", up = " + up);
    }
  }

}
