/**
 * *
 *  *  Class to represent the Main functionality
 *  *  The class includes a public static void main
 *  *  Functionality is to parse the points in the program2data.txt file and get the
 *  *  minimum distance between the points inside the file
 *  *
 * @author Sidhant Bansal
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

    // double[] x_coord = null;
    // double[] y_coord = null;
    double[] dummy = null;
    PointsGrabber[] getPoints = null;
    PointsGrabber[] xSortedPoints = null;
    PointsGrabber[] ySortedPoints = null;

    public static void main(String[] args) throws IOException, MPIException {
        MPI.Init(args);
        File file = new File("program2data.txt");
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
        MPI.COMM_WORLD.Bcast(size, 0, 1, MPI.INT, PointsUtils.MASTER);
        getPoints = PointsUtils.init(size, points);
        xSortedPoints = getPoints.clone(); //Cloning original array into new array which will have points sorted according to x coordinates
        ySortedPoints = getPoints.clone(); //Cloning original array into new array which will have points sorted according to y coordinates
        
        PointsUtils.sortByX(xSortedPoints); //Sorting by X coordinates
        PointsUtils.sortByY(ySortedPoints); //Sorting by Y coordinates
        
        PointsUtils.calculateLocalMinima(xSortedPoints, ySortedPoints);
        //PointsUtils.calculateBorderMinima(size, points);
        MPI.Finalize();

    }

}
