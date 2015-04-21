/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.util.ArrayList;
import java.util.Scanner;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.DenseInstance;
import java.lang.IllegalArgumentException;
import java.util.Arrays;

/**
 *
 * @author wraith55
 */
public class RonaldInstanceMaker implements PacInstanceMaker{
    
    @Override
    public Instance makeInstance(String mainLine, String dotLine, String magicDotLine) {
        //System.out.println("String1:" + mainLine);
        //System.out.println("String2:" + dotLine);
        //System.out.println("String3:" + magicDotLine);
        
        Pair gameData = gameDataReader(mainLine);
        double[] mainArr = (double[]) gameData.left;
        DIRECTION pacDirection = (DIRECTION) gameData.right;
        
        double[] dotArrTotal = new double[512];
        double[] magicDotArrTotal = new double[8];
        
        Arrays.fill(dotArrTotal, -1000);  // default value puts it far outside location range
        Arrays.fill(magicDotArrTotal, -1000);
        
        double[] dotArr = dotReader(dotLine);
        double[] magicDotArr = dotReader(magicDotLine);
        
        dotArrTotal = copyArray(dotArrTotal, dotArr);
        magicDotArrTotal = copyArray(magicDotArrTotal, magicDotArr);
        
        int pacX = (int) mainArr[1] ;
        int pacY = (int) mainArr[2] ;
        
        // select 5 closest dots (10 numbers, 2 for each dot location)
        double[] minDotArr = minKLocationPairs(dotArrTotal, pacX, pacY, 5);

        // below: changed to minDotArr to use closest dot pair locations
        double[] totalArr = concatArrays(mainArr, minDotArr, magicDotArrTotal);        
        
        //Enum direction = pacDir(mainArr[3], mainArr[4]);
                
        Instance dataInstance = new DenseInstance(totalArr,pacDirection);
        
        return dataInstance;
    }
    
    
    /**
     * Reads data from strings of the game data recorded
     * @param mainline a string of values passed in from a text file
     * @return a double array with all numeric values
     */
    private Pair<double[],Enum> gameDataReader(String mainline){
        ArrayList<Double> values;
        values = new ArrayList<>();
        
        String lines[] = mainline.split("\\s+");
        for(int i=0;i<lines.length;i++){
            Scanner scan = new Scanner(lines[i]);
            while(scan.hasNext()){
                if(!scan.hasNextDouble()){
                    scan.next();
                }
                else{
                    values.add(scan.nextDouble());
                }
            }
        }
        DIRECTION dir = (DIRECTION) pacDir(values.get(3), values.get(4));
        
        //remove timestamp and pacdirection, which is essentially junk data
        values.remove(0);
        values.remove(3);
        values.remove(4);
        Double[] valArr = new Double[values.size()];
        valArr = values.toArray(valArr);
        
        double[] primArr = primConverter(valArr);
        Pair gameData = new Pair(primArr, dir);
        
        return gameData;
        
    }
    
    /**
     * 
     * @param dotline
     * @return 
     */
    private double[] dotReader (String dotline){
        ArrayList<Double> values;
        values = new ArrayList<>();
        
        String lines[] = dotline.split(",|\\(|\\)");
        
        for(int i=0;i<lines.length;i++){
            Scanner scan = new Scanner(lines[i]);
            while(scan.hasNext()){
                if(!scan.hasNextDouble()){
                    scan.next();
                }
                else{
                    values.add(scan.nextDouble());
                }
            }
        }
        
        Double[] valArr = new Double[values.size()];
        valArr = values.toArray(valArr);
        double[] primArr = primConverter(valArr);
        return primArr;

    }
    
    /**
     * Converts a Double array into a primitive double array
     * @param valArr the Double array
     * @return double[] a primitive array
     */
    private double[] primConverter (Double[] valArr){
        double[] primArr = new double[valArr.length];
        //if(valArr.length <= 0) System.err.println("ERROR: array is null");
        //System.out.println("Valarr length = " + valArr.length);
        for(int i=0; i<valArr.length;i++){
            //System.out.println("i= " + i);
            
            primArr[i] = valArr[i];
        }
        return primArr;
    }
    
    private double[] concatArrays(double[] arr1, double[] arr2, double[] arr3){
        int len1 = arr1.length;
        int len2 = arr2.length;
        int len3 = arr3.length;
        
        double[] concatArr  = new double[len1+len2+len3];
        System.arraycopy(arr1, 0, concatArr, 0, len1);
        System.arraycopy(arr2, 0, concatArr, len1, len2);
        System.arraycopy(arr3, 0, concatArr, len1+len2, len3);
        
        return concatArr;
        
    }
    
    /**
     * Copies values of an array to another
     * arr2 must be shorter, as it is being copied from
     * @param arr1 array to be copied to
     * @param arr2 array to be copied from
     * @return arr1, modified
     * @throws IllegalArgumentException if arr2 is longer than arr1
     */
    private double[] copyArray(double[] arr1, double[] arr2) throws IllegalArgumentException{
        if(arr1.length < arr2.length){
            throw new IllegalArgumentException("arr2 must be same length or shorter than arr1");
        }
        for(int i=0; i<arr2.length; i++){
            arr1[i] = arr2[i];
        }
        return arr1;        
    }
    
    /**
     * returns the max k indices of the dot values
     * @param dotvals the array of dot locations
     * @param pacx pacmans x loc
     * @param pacy pacmans y loc
     * @param k number of min you wantr
     * @return an array of indices of the locations of the k minimum dots
     */
    public static int[] minKIndices(double[] dotvals, double pacx, double pacy, int k) throws IllegalArgumentException{
        int[] indices = new int[k];
        double[] dists = new double[dotvals.length/2];
        if (dotvals.length % 2 != 0) throw new IllegalArgumentException("Length of dotvals must be even");
        int idx = 0;
        for(int i=0;i<dotvals.length-1;i+=2){
            dists[idx++] = PacMan.dist_formula(dotvals[i],dotvals[i+1], pacx, pacy);            
        }
        for(int i=0; i<k; i++){
            double min = dists[0];
            int index = 0;
            for(int j=0; j<dists.length;j++){
                if(dists[j] < min){
                    min = dists[j];
                    index = j;
                }
            }
            indices[i] = 2 * index;
            dists[index] = Double.MAX_VALUE;
        }
        
        return indices;
    }
    
    
    private double[] minKLocationPairs(double[] dotvals, double pacx, double pacy, int k)
    {
        int[] minIndices = minKIndices(dotvals, pacx, pacy, k);
        
        double[] minDotArr = new double[2 * k];  // pair of numbers for each dot position
        
        // for every index in minIndices, copy the corresponding value in dotvals
        // (which is x) and the one right after it (which is y)
        int idx = 0;
        for (int i = 0; i < minIndices.length; i++)
        {   minDotArr[idx] =  dotvals[ minIndices[i] ];
            minDotArr[idx+1] = dotvals[ minIndices[i] + 1 ];
            idx += 2;
        }   
        
        return minDotArr;
    }
    
    /**
     * Returns an enum given an integer direction of x and y for pacman
     * @param xdir x direction of pac
     * @param ydir y direction of pac
     * @return DIRECTION enum
     * @throws IllegalArgumentException if the x and y directions are not legal
     */
    private Enum pacDir(double xdir, double ydir) throws IllegalArgumentException{
        if(xdir == -1){
            if (ydir != 0) throw new IllegalArgumentException("Pacman cannot travel diagonally");
            else return DIRECTION.LEFT;
        }
        else if (xdir == 1){
            if (ydir != 0) throw new IllegalArgumentException("Pacman cannot travel diagonally");
            else return DIRECTION.RIGHT;
        }
        else if (ydir == -1){
            if (xdir != 0) throw new IllegalArgumentException("Pacman cannot travel diagonally");
            else return DIRECTION.DOWN;
        }
        else if (ydir == 1){
            if (xdir != 0) throw new IllegalArgumentException("Pacman cannot travel diagonally");
            else return DIRECTION.UP;
        }
        else if (xdir == 0 && ydir == 0){
            //???
            return DIRECTION.UP;
        }
        else{
            throw new IllegalArgumentException("xdir and ydir should only be 1, 0, or -1");
        }
        
        
    }
    
    public static void main(String[] args)
    {
        double[] dotVals = new double[] { 1, 2, 3, 4, 7, 7, 6, 6, 10, 11 };
        double pacX = 7;
        double pacY = 7;
        RonaldInstanceMaker m = new RonaldInstanceMaker();
        m.makeInstance("0            14           24           -1              0               0                   15           13           15           15           13           15           17           15      ",
        "(2, 1) ,(3, 1) ,(4, 1) ,(5, 1) ,(6, 1) ,(7, 1) ,(8, 1) ,(9, 1) ,(10, 1) ,(11, 1) ,(12, 1) ,(17, 1) ,(18, 1) ,(19, 1) ,(20, 1) ,(21, 1) ,(22, 1) ,(23, 1) ,(24, 1) ,(25, 1) ,(26, 1) ,(27, 1) ,(2, 5) ,(3, 5) ,(4, 5) ,(5, 5) ,(6, 5) ,(7, 5) ,(8, 5) ,(9, 5) ,(10, 5) ,(11, 5) ,(12, 5) ,(13, 5) ,(14, 5) ,(15, 5) ,(16, 5) ,(17, 5) ,(18, 5) ,(19, 5) ,(20, 5) ,(21, 5) ,(22, 5) ,(23, 5) ,(24, 5) ,(25, 5) ,(26, 5) ,(27, 5) ,(2, 8) ,(3, 8) ,(4, 8) ,(5, 8) ,(24, 8) ,(25, 8) ,(26, 8) ,(27, 8) ,(10, 8) ,(11, 8) ,(12, 8) ,(13, 8) ,(16, 8) ,(17, 8) ,(18, 8) ,(19, 8) ,(2, 21) ,(3, 21) ,(4, 21) ,(5, 21) ,(6, 21) ,(7, 21) ,(8, 21) ,(9, 21) ,(10, 21) ,(11, 21) ,(12, 21) ,(17, 21) ,(18, 21) ,(19, 21) ,(20, 21) ,(21, 21) ,(22, 21) ,(23, 21) ,(24, 21) ,(25, 21) ,(26, 21) ,(27, 21) ,(2, 24) ,(27, 24) ,(7, 24) ,(8, 24) ,(9, 24) ,(10, 24) ,(11, 24) ,(12, 24) ,(17, 24) ,(18, 24) ,(19, 24) ,(20, 24) ,(21, 24) ,(22, 24) ,(2, 27) ,(3, 27) ,(4, 27) ,(5, 27) ,(24, 27) ,(25, 27) ,(26, 27) ,(27, 27) ,(10, 27) ,(11, 27) ,(12, 27) ,(17, 27) ,(18, 27) ,(19, 27) ,(2, 30) ,(3, 30) ,(4, 30) ,(5, 30) ,(6, 30) ,(7, 30) ,(8, 30) ,(9, 30) ,(10, 30) ,(11, 30) ,(12, 30) ,(13, 30) ,(14, 30) ,(15, 30) ,(16, 30) ,(17, 30) ,(18, 30) ,(19, 30) ,(20, 30) ,(21, 30) ,(22, 30) ,(23, 30) ,(24, 30) ,(25, 30) ,(26, 30) ,(27, 30) ,(1, 1) ,(1, 2) ,(1, 4) ,(1, 5) ,(1, 6) ,(1, 7) ,(1, 8) ,(28, 1) ,(28, 2) ,(28, 4) ,(28, 5) ,(28, 6) ,(28, 7) ,(28, 8) ,(1, 21) ,(1, 22) ,(1, 23) ,(28, 21) ,(28, 22) ,(28, 23) ,(1, 27) ,(1, 28) ,(1, 29) ,(1, 30) ,(28, 27) ,(28, 28) ,(28, 29) ,(28, 30) ,(3, 24) ,(3, 25) ,(3, 26) ,(26, 24) ,(26, 25) ,(26, 26) ,(6, 2) ,(6, 3) ,(6, 4) ,(6, 6) ,(6, 7) ,(6, 8) ,(6, 9) ,(6, 10) ,(6, 11) ,(6, 12) ,(6, 13) ,(6, 14) ,(6, 15) ,(6, 16) ,(6, 17) ,(6, 18) ,(6, 19) ,(6, 20) ,(6, 22) ,(6, 23) ,(6, 24) ,(6, 25) ,(6, 26) ,(6, 27) ,(23, 2) ,(23, 3) ,(23, 4) ,(23, 6) ,(23, 7) ,(23, 8) ,(23, 9) ,(23, 10) ,(23, 11) ,(23, 12) ,(23, 13) ,(23, 14) ,(23, 15) ,(23, 16) ,(23, 17) ,(23, 18) ,(23, 19) ,(23, 20) ,(23, 22) ,(23, 23) ,(23, 24) ,(23, 25) ,(23, 26) ,(23, 27) ,(9, 6) ,(9, 7) ,(9, 8) ,(20, 6) ,(20, 7) ,(20, 8) ,(9, 25) ,(9, 26) ,(9, 27) ,(20, 25) ,(20, 26) ,(20, 27) ,(13, 1) ,(13, 2) ,(13, 3) ,(13, 4) ,(16, 1) ,(16, 2) ,(16, 3) ,(16, 4) ,(13, 21) ,(13, 22) ,(13, 23) ,(13, 24) ,(16, 21) ,(16, 22) ,(16, 23) ,(16, 24) ,(13, 27) ,(13, 28) ,(13, 29) ,(16, 27) ,(16, 28) ,(16, 29) ,",
        "(1, 3) ,(28, 3) ,(1, 24) ,(28, 24) ,");
        
        int[] minIndices = m.minKIndices(dotVals, pacX, pacY, 2);
        for (int i = 0; i < minIndices.length; i++)
            System.out.println("minIndices[" + i + "] = "  + minIndices[i]);
        System.out.println("***********");
        
        double[] minLocs = m.minKLocationPairs(dotVals, pacX, pacY, 2);
        for (int i = 0; i < minLocs.length; i++)
            System.out.println("minLocs[" + i + "] = "  + minLocs[i]);
        System.out.println("***********");
    }
    
    
}



