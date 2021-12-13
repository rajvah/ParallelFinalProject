/**
 * *
 *  *  Class to represent the Main functionality
 *  *  The class includes a public static void main
 *  *  Functionality is to parse the points in the program2data.txt file and get the
 *  *  minimum distance between the points inside the file
 *  *
 *  * @author Sidhant, Harshit and Kanika
 */

import mpi.*;           // for mpiJava
import java.net.*;      // for InetAddress
import java.util.*;     // for Date

import java.io.*;       // To implement the file reader
import java.lang.*;
import java.util.*;

public class ClosestPair {

    static Points[] getPoints = null;
    static Points[] xSortedPoints = null;
    static Double minima = 0.0;

    public static void main(String[] args) throws IOException, MPIException {
        
        //Starting the process to read the input file.
        File file = new File("100000.txt");
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
