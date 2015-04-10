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
            
    
    public static List<Instance> readInstances(String path, PacInstanceMaker instMaker) throws FileNotFoundException, 
                                                                                        IOException, IllegalArgumentException
    {
        if (path == null || path.equals("") || instMaker == null)
            throw new IllegalArgumentException("path is empty or null or instMaker is null: path = " 
                                                + path + ", instMaker = " + instMaker);
        
        File f = new File(path);
        
        if (! f.exists())
            throw new FileNotFoundException("Cannot find instances file: " + path);
        
        List<Instance> instances = new ArrayList<>();
        
        BufferedReader reader = new BufferedReader( new FileReader(f) );
        String line = "";
        while (line != null)
        {
            line = reader.readLine();
            Instance inst = instMaker.makeInstance(line);
            if (inst == null)
                System.err.println("PacParser: readInstances - PacInstanceMaker returned null!");
            else
                instances.add( inst );
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
