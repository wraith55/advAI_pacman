package pacman;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.bayes.NaiveBayesClassifier;
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
    
    PacInstanceMaker instMaker = new RonaldInstanceMaker();
    
    System.out.println("current dir = " + System.getProperty("user.dir"));
    
    List<Instance> instances = PacML.readInstancesFromDir("data/main", "data/dots", "data/magic_dots", instMaker) ;
    System.out.println("instances size = " + instances.size() ) ;
    
    
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
        //Classifier c = new NaiveBayesClassifier(true, true, false);
        System.out.println("building classifier from dataset...cross_idx = " + cross_idx);
        c.buildClassifier(PacML.makeDataset(training));
        File f = PacML.writeClassifierFile(c, "test_classifier");
        Classifier d = PacML.readClassifierFile("test_classifier", 5);
        
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
    System.exit(0);
    
    Classifier c = new KNearestNeighbors(5);
    System.out.println("building classifier from dataset...");
    c.buildClassifier(PacML.makeDataset(instances));
    
    root.getChildren().add(new Maze("fake_name", 10, c, instMaker));
    //root.getChildren().add(new Maze("test1", 10, null, null));
    
    primaryStage.setScene(scene);
    primaryStage.show();
  }

}
