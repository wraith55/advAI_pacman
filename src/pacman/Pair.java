/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

/**
 *
 * @author jamesvickers19
 */
public class Pair<L, R> 
{
    
    public final L left;
    public final R right;
    
    public Pair(L left, R right)
    {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof Pair))
            return false;
        Pair other = (Pair) obj;
        return this.left.equals( other.left ) 
                          &&
               this.right.equals( other.right );
    }
    
}
