/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import libsvm.LibSVM;
import libsvm.SelfOptimizingLinearLibSVM;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KDtreeKNN;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.MeanFeatureVotingClassifier;
import net.sf.javaml.classification.NearestMeanClassifier;
import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;

/**
 *
 * @author jamesvickers19
 */
public class PacML
{
    
    public static List<Instance> readInstancesFromDir(String mainDirPath, String dotDirPath, 
                                                      String magDotDirPath,
                                                      PacInstanceMaker instMaker, int minSize)
                                                      throws FileNotFoundException, 
                                                      IOException, IllegalArgumentException
    {  
        // Check for null or empty path arguments
        if (mainDirPath == null || mainDirPath.equals("") )
            throw new IllegalArgumentException("mainDirPath is empty or null - mainDirPath = " 
                                                + mainDirPath);
        if (dotDirPath == null || dotDirPath.equals(""))
            throw new IllegalArgumentException("dotDirPath is empty or null - dotDirPath = " 
                                                + dotDirPath);
        if (magDotDirPath == null || magDotDirPath.equals(""))
            throw new IllegalArgumentException("magDotDirPath is empty or null - magDotDirPath = " 
                                                + magDotDirPath);
        /*************************************************************/
        // Open files, make sure they exist and are directories
        File mainDir = new File(mainDirPath);
        File dotDir = new File(dotDirPath);
        File magDotDir = new File(magDotDirPath);
        
        if (! mainDir.exists() || ! dotDir.exists() || ! magDotDir.exists())
            throw new FileNotFoundException("Cannot find one of the log files");
        
        if (! mainDir.isDirectory() || ! dotDir.isDirectory() || ! magDotDir.isDirectory())
            throw new IllegalArgumentException("One of the input paths is not a directory!");
        /************************************************************************************/
        
        List<Instance> instances = new ArrayList<>();
        
        // For every file in the mainDir, open the corresponding file in the
        // other two dirs, pass their paths and the instMaker to readInstances().
        for (File mainFile : mainDir.listFiles())
        {
            String mainName = mainFile.getName();
            String dotPath = dotDirPath + File.separator + mainName ;
            String magDotPath = magDotDirPath + File.separator + mainName ;
            System.out.println("mainName = " + mainName);
            System.out.println("paths = " + mainFile.getAbsolutePath() + ", " + dotPath + ", " + magDotPath);
            
            instances.addAll( readInstances(mainFile.getAbsolutePath(), dotPath, 
                                            magDotPath, instMaker, minSize) ) ;            
            
        }
        /************************************************************************/
        
        return instances;
    }
            
    
    public static List<Instance> readInstances(String mainPath, String dotPath, String magDotPath, 
                                               PacInstanceMaker instMaker, int minSize) 
                                                throws FileNotFoundException, 
                                                       IOException, IllegalArgumentException
    {
        if (mainPath == null || mainPath.equals(""))
            throw new IllegalArgumentException("mainPath is empty or null - mainPath = " 
                                                + mainPath);
        if (dotPath == null || dotPath.equals(""))
            throw new IllegalArgumentException("dotPath is empty or null - dotPath = " 
                                                + dotPath);
        if (magDotPath == null || magDotPath.equals(""))
            throw new IllegalArgumentException("magDotPath is empty or null - magDotPath = " 
                                                + magDotPath);
        
        File mainFile = new File(mainPath);
        File dotFile = new File(dotPath);
        File magDotFile = new File(magDotPath);
        
        if (! mainFile.exists() || ! dotFile.exists() || ! magDotFile.exists())
            throw new FileNotFoundException("Cannot find one of the log files");
        
        List<Instance> instances = new ArrayList<>();
        
        BufferedReader mfReader = new BufferedReader( new FileReader(mainFile) );
        BufferedReader dotReader = new BufferedReader( new FileReader(dotFile) );
        BufferedReader magDotReader = new BufferedReader( new FileReader(magDotFile) );
        
        // below: call readLine() twice the first time to skip the header!
        String mfLine = mfReader.readLine(); mfLine = mfReader.readLine() ;
        String dotLine = dotReader.readLine(); 
        String magDotLine = magDotReader.readLine(); 
        /***************************************/
        while (mfLine != null && dotLine != null && magDotLine != null)
        {
            
            Instance inst = instMaker.makeInstance(mfLine, dotLine, magDotLine);
            
            if (inst == null)
                System.err.println("PacParser: readInstances - PacInstanceMaker returned null!");
            else
                instances.add( inst );
            
            mfLine = mfReader.readLine();
            dotLine = dotReader.readLine();
            magDotLine = magDotReader.readLine();
        }
        
        if (instances.size() <= 0)
            System.err.println("PacML: readInstances returning an empty list");
        else if (instances.size() < minSize)
        {   System.out.println("PacML: readInstances returning an empty list because "
                    + "it has less than specified " + minSize + " instances");
            return new ArrayList<Instance>();  // return empty list instead of small instance set
        }
        
        return instances;
    }
    
    public static Dataset makeDataset(Collection<Instance> data) throws IllegalArgumentException
    {
        if (data == null || data.size() <= 0)
            throw new IllegalArgumentException("data is null or empty: data = " + data);
        
        return new DefaultDataset(data);
    }
    
    /**
     * Writes a file from a classifier that can be read from later
     * @param c a classifier object to be written to file
     * @param name desired name of the file
     * @return a file, which can be used if needed
     * @throws IOException if the file cannot be written
     */
    public static File writeClassifierFile(Classifier c, String name) throws IOException{
        File f = new File("data/classifiers/" + name);
        f.getParentFile().mkdirs();
        f.createNewFile();
        ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(f.getAbsoluteFile()));
        writer.writeObject(c);
        return f;
    }
    
    /**
     * reads a classifier from a serialized file
     * @param name name of the file to read
     * @param n number of neighbors for knearestneighbors
     * @return a Classifier that is read from the file
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static Classifier readClassifierFile(String name, int n) throws FileNotFoundException, IOException{
        InputStream file = new FileInputStream("data/classifiers/" + name);
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);
        
        try{
            Classifier fileClassifier = (Classifier)input.readObject();
            return fileClassifier;
        }
        catch(Exception e){
            throw new IOException("Classifier not read from file");
            
        }
        finally{
            input.close();
        }
        
    }
    
    public static void makeBasicClassifiers(List<Instance> train_instances, int minSize) throws IOException
    {
        
        Dataset data = makeDataset(train_instances);
        
        
        try
        {   Classifier knn1 = new KNearestNeighbors(1);
            System.out.println("building KNN (1) classifier...");
            knn1.buildClassifier(data);
            writeClassifierFile(knn1, "knn1" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier KNN (1)");
            e.printStackTrace();
        }
        System.exit(0);

        /*
        try
        {   Classifier knn25 = new KNearestNeighbors(25);
            System.out.println("building KNN (25) classifier...");
            knn25.buildClassifier(data);
            writeClassifierFile(knn25, "knn25" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier KNN(25)");
            e.printStackTrace();
        }
        
        try
        {   Classifier knn50 = new KNearestNeighbors(50);
            System.out.println("building KNN (50) classifier...");
            knn50.buildClassifier(data);
            writeClassifierFile(knn50, "knn50" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier KNN (50)");
            e.printStackTrace();
        }
        
        try
        {   Classifier knn100 = new KNearestNeighbors(100);
            System.out.println("building KNN (100) classifier...");
            knn100.buildClassifier(data);
            writeClassifierFile(knn100, "knn100" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier knn (100)");
            e.printStackTrace();
        }
        */
        try
        {   Classifier knn300 = new KNearestNeighbors(300);
            System.out.println("building KNN (300) classifier...");
            knn300.buildClassifier(data);
            writeClassifierFile(knn300, "knn300" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier knn (300)");
            e.printStackTrace();
        }
        
        try
        {   Classifier knn500 = new KNearestNeighbors(500);
            System.out.println("building KNN (500) classifier...");
            knn500.buildClassifier(data);
            writeClassifierFile(knn500, "knn500" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier knn (100)");
            e.printStackTrace();
        }
        try
        {   Classifier knn1000 = new KNearestNeighbors(1000);
            System.out.println("building KNN (1000) classifier...");
            knn1000.buildClassifier(data);
            writeClassifierFile(knn1000, "knn1000" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier knn (100)");
            e.printStackTrace();
        }
        
        /*  kd KNN just throws an exception
        try
        {   Classifier kdKNN5 = new KDtreeKNN(5);
            System.out.println("building KDtreeKNN (5) classifier...");
            kdKNN5.buildClassifier(data);
            writeClassifierFile(kdKNN5, "kdKNN5" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier KDtreeKNN(5)");
            e.printStackTrace();
        }
        
        try
        {   Classifier kdKNN50 = new KDtreeKNN(50);
            System.out.println("building KDtreeKNN (50) classifier...");
            kdKNN50.buildClassifier(data);
            writeClassifierFile(kdKNN50, "kdKNN50" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier KDtreeKNN(50)");
            e.printStackTrace();
        }
        */
         
        /*  Call to build classifier never completes (waited ~12 hours)
        try
        {   Classifier soLinSVM = new SelfOptimizingLinearLibSVM();
            System.out.println("building SelfOptimizingLinearLibSVM classifier...");
            soLinSVM.buildClassifier(data);
            writeClassifierFile(soLinSVM, "soLinSVM" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier soLinSVM");
            e.printStackTrace();
        }
        */
        
        try
        {   Classifier randForest5 = new RandomForest(5);
            System.out.println("building RandomForest (5) classifier...");
            randForest5.buildClassifier(data);
            writeClassifierFile(randForest5, "randForest5" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier RandomForest(5)");
            e.printStackTrace();
        }
            
        try
        {   Classifier randForest50 = new RandomForest(50);
            System.out.println("building RandomForest (50) classifier...");
            randForest50.buildClassifier(data);
            writeClassifierFile(randForest50, "randForest50" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier RandomForest(50)");
            e.printStackTrace();
        }
            
        try
        {   Classifier meanFeatVoting = new MeanFeatureVotingClassifier();
            System.out.println("building MeanFeatureVoting classifier...");
            meanFeatVoting.buildClassifier(data);
            writeClassifierFile(meanFeatVoting, "meanFeatVoting" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier MeanFeatureVoting");
            e.printStackTrace();
        }
            
        try
        {   Classifier nearestMean = new NearestMeanClassifier();
            System.out.println("building NearestMean classifier...");
            nearestMean.buildClassifier(data);
            writeClassifierFile(nearestMean, "nearestMean" + "_minSize_" + minSize);        
        }
        catch(Exception e)
        {   System.out.println("could not build classifier NearestMean");
            e.printStackTrace();
        }
        
        try        
        {   Classifier svm = new LibSVM();
            System.out.println("building svm classifier...");
            svm.buildClassifier(data);
            writeClassifierFile(svm, "svm" + "_minSize_" + minSize);
        }
        catch(Exception e)
        {   System.out.println("could not build classifier svm");
            e.printStackTrace();
        }

    }
        
}
