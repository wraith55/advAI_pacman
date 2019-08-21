/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import weka.core.Instances;

/**
 *
 * @author jamesvickers19
 */
public class WekaClassifier implements net.sf.javaml.classification.Classifier {

    
    private final weka.classifiers.Classifier weka_clf;
    
    public WekaClassifier(weka.classifiers.Classifier clf)
    {
        this.weka_clf = clf;
    }
    
    @Override
    public void buildClassifier(Dataset dtst) 
    {
        
        //TODO: convert Dataset to weka.core.Instances somehow
    }

    @Override
    public Object classify(Instance javaml_inst) 
    {
        weka.core.DenseInstance weka_inst = convert(javaml_inst);
        
        double val;
        
        try 
        {
            val = this.weka_clf.classifyInstance(weka_inst);
            return convertToDir(val);
        } 
        catch (Exception ex) 
        {
            System.err.println("WekaClassifier.classify: threw exception, returning null");
            return null;
        }
    }

    @Override
    public Map<Object, Double> classDistribution(Instance instnc) 
    {   return null;
    }
    
    public static weka.core.Instances convertToInstances(Dataset d)
    {
        //Instances inst = new Instances();
        return null;
    }
    
    public static DIRECTION convertToDir(double v)
    {
        int val = (int) v;
        
        if (val <= 0)
            return DIRECTION.LEFT;
        
        if (val == 1)
            return DIRECTION.RIGHT;
        
        if (val == 2)
            return DIRECTION.DOWN;
        
        return DIRECTION.UP;
    }
    
    public static weka.core.DenseInstance convert(net.sf.javaml.core.Instance javaml_inst)
    {
        Collection c = javaml_inst.values();
        Double[] d = (Double[])(c.toArray(new Double[c.size()]));
        double[] atts = RonaldInstanceMaker.primConverter(d);
        
        final double weight = 1.0;
        weka.core.DenseInstance weka_inst = new weka.core.DenseInstance(weight, atts);
        
        return weka_inst;
    }
    

    
}
