package pacman;

import java.util.List;

/**
 *
 * @author jamesvickers19
 */
public class GameStateFeatures 
{
    private final long timestep;
    private final Pair<Integer, Integer> pacmanLoc;
    private final Pair<Integer, Integer> pacmanDir;
    private final List<Pair<Integer, Integer>> ghost_locations;
    private final boolean eatModeActive;
    
    public GameStateFeatures(int timestep,
                             boolean eatModeActive,
                             Pair<Integer, Integer> pacmanLoc,
                             Pair<Integer, Integer> pacmanDir,
                             List<Pair<Integer, Integer>> ghostLocs)
    {
        this.timestep = timestep;
        this.eatModeActive = eatModeActive;  
        this.pacmanLoc = pacmanLoc;
        this.pacmanDir = pacmanDir;
        this.ghost_locations = ghostLocs;
    }
    
    public long getTimestep()  {  return this.timestep;  }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof GameStateFeatures))  
            return false;
        
        GameStateFeatures other = (GameStateFeatures) obj;
        
        // timestep comparison is purposefully omitted
        return this.eatModeActive == other.eatModeActive &&
               this.pacmanLoc.equals( other.pacmanLoc ) &&
               this.pacmanDir.equals( other.pacmanDir ) && 
               this.ghost_locations.equals( other.ghost_locations );

    }

    
    public static String header()
    {
        return "timestep     pacman_x     pacman_y     pacman_xdir     pacman_ydir     "
                + "eat_mode_active     "
                + "ghost1_x     ghost1_y     ghost2_x     ghost2_y     ghost3_x     ghost3_y"
                + "     ghost4_x     ghost4_y\n";
    }
    
    @Override
    public String toString()
    {
        return String.format
                ("%-8s     %-8s     %-8s     %-11s     %-11s     %-15s     %-8s"
               + "     %-8s     %-8s     %-7s      %-8s     %-8s     %-8s     %-8s\n",
                 this.timestep,
                 this.pacmanLoc.left, this.pacmanLoc.right,
                 this.pacmanDir.left, this.pacmanDir.right,
                (this.eatModeActive) ? 1 : 0, 
                 this.ghost_locations.get(0).left, this.ghost_locations.get(0).right,
                 this.ghost_locations.get(1).left, this.ghost_locations.get(1).right,
                 this.ghost_locations.get(2).left, this.ghost_locations.get(2).right,
                 this.ghost_locations.get(3).left, this.ghost_locations.get(3).right);
    }
    
    /*  I use this to make a single integer representation of a pacman state.
        It is a bitmap, filled in as follows:
    
        [ghost4.y][ghost4.x]...[ghost1.y][ghost1.x][pac.ydir][pac.xdir][pac.y][pac.x][free_ghosts][close_ghosts][eat_active]
        
        where an x or y location is encoded with 5 bits, a direction with 3 bits, free_ghosts and close_ghosts 
        are each 3 bits, and eat_active is a single bit, for a total of 64.
    */
    
    public long representAsLong() 
    {
        
        long bitmask = (this.eatModeActive) ? 1 : 0 ;
        bitmask |= ( 1 << 1 )   ;
        bitmask |= ( 1  << 4 )   ;
        bitmask |= ( this.pacmanLoc.left   << 7 )   ;
        bitmask |= ( this.pacmanLoc.right  << 12 )  ;
        bitmask |= ( this.pacmanDir.left   << 17 )  ;
        bitmask |= ( this.pacmanDir.right  << 22 )  ;
        
        // ghost locations
        for (int i = 0; i < this.ghost_locations.size(); i++)
        {    
            int shift = 23 + (i * 10);
            Pair<Integer, Integer> loc = this.ghost_locations.get(i);
            
            bitmask |= ( loc.left << shift );
            bitmask |= ( loc.right << (shift+5) );  // each ghost has 5 bits each for (x,y)
        }

        return bitmask;
    }
    
    
}
