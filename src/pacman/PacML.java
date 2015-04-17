/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
                                                      PacInstanceMaker instMaker)
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
                                            magDotPath, instMaker) ) ;
        }
        /************************************************************************/
        
        return instances;
    }
            
    
    public static List<Instance> readInstances(String mainPath, String dotPath, String magDotPath, 
                                               PacInstanceMaker instMaker) 
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
        
        return instances;
    }
    
    public static Dataset makeDataset(Collection<Instance> data) throws IllegalArgumentException
    {
        if (data == null || data.size() <= 0)
            throw new IllegalArgumentException("data is null or empty: data = " + data);
        
        return new DefaultDataset(data);
    }
        
}
