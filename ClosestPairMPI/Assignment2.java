/**
 * *
 *  *  Class to represent the Main functionality
 *  *  The class includes a public static void main
 *  *  Functionality is to parse the points in the program2data.txt file and get the
 *  *  minimum distance between the points inside the file
 *  *
 * @author Sidhant, Harshit and Kanika
 */

//package Assignment2;

import mpi.*;           // for mpiJava
import java.net.*;      // for InetAddress
import java.util.*;     // for Date

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.*;
import java.util.*;

public class Assignment2 {

    static PointsGrabber[] getPoints = null;
    static PointsGrabber[] xSortedPoints = null;
    static Double minima = 0.0;

    public static void main(String[] args) throws IOException, MPIException {
        
        File file = new File("10000.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        int number = Integer.parseInt(line);
        ArrayList<String> data = new ArrayList<>();

        for(int i = 0; i < number; ++i) {
            line = br.readLine().trim();
            data.add(line);
        }

        // use the points class to set the members
        Points points = new Points();
        // set number of points
        points.setNumberOfPoints(number);
        // set the points collected in the array list
        points.setPoints(PointsUtils.ConvertStringToArrayList(number, data));

        // close the buffer reader
        br.close();
        int size = points.getNumberOfPoints();
        getPoints = PointsUtils.init(size, points);
        xSortedPoints = getPoints.clone(); //Cloning original array into new array which will have points sorted according to x coordinates
        PointsUtils.sortByX(xSortedPoints); //Sorting by X coordinates
        
        Date startTime = new Date( );
        MPI.Init(args);
        Double min = PointsUtils.calculateLocalMinima(xSortedPoints);
        PointsUtils.calculateBorderMinima(xSortedPoints, min, startTime); 
        MPI.Finalize();

        
    }
}
