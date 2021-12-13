/**
 *  Class to represent Points
 *  The Points is represented by numberOfPoints and an array of points as (x, y) coordinates
 *  Functionality includes setting an getting the class elements
 *  @author Sidhant, Harshit and Kanika
 */

//package Assignment2;

import java.util.*;
import java.io.*;

public class Points implements Serializable{

    private double x, y; // x and y coordinates of the point

    // total number of points
    private int numberOfPoints;
    
    // a 2d list of points to store x coordinate at index 0 and the y coordinate at index 1
    private ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();

    public Points(){
        super();
    }

    Points(double x, double y){ 
        this.x = x;
        this.y = y;
    }

    // set the values for list of all given points
    //Params: ArrayList<ArrayList<Double>> points
    //Returns: void
    public void setPoints(ArrayList<ArrayList<Double>> pts){
        this.points = pts;
    }

    //Params: None
    //Returns: ArrayList<ArrayList<Double>> as the list of all points
    public ArrayList<ArrayList<Double>> getPoints() {
        return points;
    }

    // set the number of points
    //Params: integer n
    //Returns: void
    public void setNumberOfPoints(int number){
        this.numberOfPoints = number;
    }

    // get the number of points
    //Params: None
    //Returns: Integer numberOfPoints
    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setX(Double number){
        this.x = number;
    }

    public double getX() {
        return x;
    }

    public void setY(Double number){
        this.y = number;
    }

    public double getY() {
        return y;
    }
}