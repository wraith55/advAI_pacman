/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.util.Map;
import java.util.Random;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.bayes.NaiveBayesClassifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;



/**
 *
 * @author jamesvickers19
 */
public class Foo implements Classifier
{

    @Override
    public void buildClassifier(Dataset dtst) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object classify(Instance instnc) 
    {   
        int r = new Random().nextInt(4); // [0, 1, 2, 3]
        switch(r)
        {
            case 0:
                return DIRECTION.DOWN;
            case 1:
                return DIRECTION.LEFT;
            case 2:
                return DIRECTION.RIGHT;
            case 3:
                return DIRECTION.UP;
        }
        throw new IllegalStateException("whaaatttttt......");
    }

    @Override
    public Map<Object, Double> classDistribution(Instance instnc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
