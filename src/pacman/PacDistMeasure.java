package pacman;


import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;

    public class PacDistMeasure implements DistanceMeasure
    {

        @Override
        public double measure(Instance inst1, Instance inst2) 
        {   double dist = 0.0;
        
            Double[] data1 = (Double[]) inst1.values().toArray(new Double[inst1.values().size()]);
            Double[] data2 = (Double[]) inst2.values().toArray(new Double[inst2.values().size()]);
            
            // eat mode.  
            dist += 10 * Math.abs(data1[0] - data2[0]);
            
            // position bit map.  
            double bitMapDist = Math.abs(data1[1] - data2[1]); 
            if (bitMapDist > 0)
            {   
                dist += 50.0;  // bitMaps that are different should be treated as such
            }

            // ghost x distance.  
            dist += Math.abs(data1[2] - data2[2]);
            
            // ghost y distance.  
            dist += Math.abs(data1[3] - data2[3]);

            return dist;
        }

        @Override
        public boolean compare(double d1, double d2) 
        {  return d1 < d2;
        }

        @Override
        public double getMinValue() 
        { return 0.0 ;
        }

        @Override
        public double getMaxValue() 
        {  return Double.MAX_VALUE ;
        }
        
    }