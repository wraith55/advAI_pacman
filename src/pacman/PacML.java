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
        
        String mfLine = mfReader.readLine();
        String dotLine = dotReader.readLine();
        String magDotLine = magDotReader.readLine();
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
