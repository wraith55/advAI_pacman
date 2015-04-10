/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import net.sf.javaml.core.Instance;

/**
 *
 * @author jamesvickers19
 */
public interface PacInstanceMaker 
{
    
    public Instance makeInstance(String mainLine, String dotLine, String magicDotLine);
    
}
