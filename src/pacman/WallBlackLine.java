package pacman;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * WallBlackLine.fx created on 2008-12-27, 17:52:58 <br>
 * WallBlackLine.java created October 2011
 *
 * @author Henry Zhang
 * @author Patrick Webster
 */
public class WallBlackLine extends Line {


  public WallBlackLine(float x1, float y1, float x2, float y2) {
    init(x1, y1, x2, y2);
    
    setStrokeWidth(MazeData.GRID_STROKE + 1);
    setStroke(Color.BLACK);
  }

  public WallBlackLine(Color lineColor, float x1, float y1, float x2, float y2) {
    init(x1, y1, x2, y2);
    
    setStrokeWidth(MazeData.GRID_STROKE);
    setStroke(lineColor);
  }

  private void init(float x1, float y1, float x2, float y2) {

    setCache(true);

    if (x1 == x2) { // vertical line
      setStartX(MazeData.calcGridXFloat(x1));
      setStartY(MazeData.calcGridYFloat(y1) + MazeData.GRID_STROKE);
      setEndX(MazeData.calcGridXFloat(x2));
      setEndY(MazeData.calcGridYFloat(y2) - MazeData.GRID_STROKE);
    }
    else { // horizontal line
      setStartX(MazeData.calcGridXFloat(x1) + MazeData.GRID_STROKE);
      setStartY(MazeData.calcGridYFloat(y1));
      setEndX(MazeData.calcGridXFloat(x2) - MazeData.GRID_STROKE);
      setEndY(MazeData.calcGridYFloat(y2));
    }
//  } // end postinit
  }
  
}
