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
        System.out.println("String1:" + mainLine);
        System.out.println("String2:" + dotLine);
        System.out.println("String3:" + magicDotLine);
        
        double[] mainArr = gameDataReader(mainLine);
        double[] dotArrTotal = new double[512];
        double[] magicDotArrTotal = new double[8];
        
        Arrays.fill(dotArrTotal, 0);
        Arrays.fill(magicDotArrTotal, 0);
        
        double[] dotArr = dotReader(dotLine);
        double[] magicDotArr = dotReader(magicDotLine);
        
        dotArrTotal = copyArray(dotArrTotal, dotArr);
        magicDotArrTotal = copyArray(magicDotArrTotal, magicDotArr);
        
        
        double[] totalArr = concatArrays(mainArr, dotArrTotal, magicDotArrTotal);        
        
        Enum direction = pacDir(mainArr[3], mainArr[4]);
        Instance dataInstance = new DenseInstance(totalArr,direction);
        
        
        
        
        return dataInstance;
    }
    
    
    /**
     * Reads data from strings of the game data recorded
     * @param mainline a string of values passed in from a text file
     * @return a double array with all numeric values
     */
    private double[] gameDataReader(String mainline){
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
        Double[] valArr = new Double[values.size()];
        valArr = values.toArray(valArr);
        
        double[] primArr = primConverter(valArr);
        
        return primArr;
        
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
        if(valArr.length <= 0) System.err.println("ERROR: array is null");
        System.out.println("Valarr length = " + valArr.length);
        for(int i=0; i<valArr.length;i++){
            System.out.println("i= " + i);
            
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
    private int[] minKIndices(double[] dotvals, double pacx, double pacy, int k) throws IllegalArgumentException{
        int[] indices = new int[k];
        double[] dists = new double[dotvals.length/2];
        if (dotvals.length % 2 != 0) throw new IllegalArgumentException("Length of dotvals must be even");
        for(int i=0;i<dotvals.length;i+=2){
            dists[i] = PacMan.dist_formula(dotvals[i],dotvals[i+1], pacx, pacy);            
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
            dists[index] = 999;
        }
        
        return indices;
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
    
    
}



