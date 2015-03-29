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
    private final int num_free_ghosts, num_active_dots, num_ghosts_close;
    private final boolean eatModeActive;
    
    public GameStateFeatures(int timestep,
                             int num_free_ghosts, 
                             int num_ghosts_close,
                             int num_active_dots,
                             boolean eatModeActive,
                             Pair<Integer, Integer> pacmanLoc,
                             Pair<Integer, Integer> pacmanDir,
                             List<Pair<Integer, Integer>> ghostLocs)
    {
        this.timestep = timestep;
        this.num_free_ghosts = num_free_ghosts;
        this.num_active_dots = num_active_dots;
        this.num_ghosts_close = num_ghosts_close;
        this.eatModeActive = eatModeActive;
        this.pacmanLoc = pacmanLoc;
        this.pacmanDir = pacmanDir;
        this.ghost_locations = ghostLocs;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof GameStateFeatures))  
            return false;
        
        GameStateFeatures other = (GameStateFeatures) obj;
        
        // timestep comparison is purposefully omitted
        return this.num_active_dots == other.num_active_dots &&
               this.num_free_ghosts == other.num_free_ghosts &&
               this.num_ghosts_close == other.num_ghosts_close &&
               this.eatModeActive == other.eatModeActive &&
               this.pacmanLoc.equals( other.pacmanLoc ) &&
               this.pacmanDir.equals( other.pacmanDir ) && 
               this.ghost_locations.equals( other.ghost_locations );

    }

    
    public static String header()
    {
        return "timestep     pacman_x     pacman_y     pacman_xdir     pacman_ydir     "
                + "free_ghosts     close_ghosts      eat_mode_active      active_dots     "
                + "ghost1_x     ghost1_y     ghost2_x     ghost2_y     ghost3_x     ghost3_y"
                + "     ghost4_x     ghost4_y\n";
    }
    
    @Override
    public String toString()
    {
        return String.format
                ("%-8s     %-8s     %-8s     %-11s     %-11s     %-11s     %-13s     %-16s     %-11s"
               + "     %-8s     %-8s     %-8s     %-8s     %-8s     %-8s     %-8s     %-8s\n",
                 this.timestep,
                 this.pacmanLoc.left, this.pacmanLoc.right,
                 this.pacmanDir.left, this.pacmanDir.right,
                 this.num_free_ghosts, 
                 this.num_ghosts_close,
                (this.eatModeActive) ? 1 : 0, 
                 this.num_active_dots,
                 this.ghost_locations.get(0).left, this.ghost_locations.get(0).right,
                 this.ghost_locations.get(1).left, this.ghost_locations.get(1).right,
                 this.ghost_locations.get(2).left, this.ghost_locations.get(2).right,
                 this.ghost_locations.get(3).left, this.ghost_locations.get(3).right);
    }
    
    
}
