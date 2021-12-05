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

        int myrank = MPI.COMM_WORLD.Rank( );
        int nprocs = MPI.COMM_WORLD.Size( );
        MPI.Init(args) ;
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
        MPI.COMM_WORLD.Bcast(size, 0, 1, MPI.INT, MASTER);
        PointsUtils.calculateLocalMinima(size, points);
        calculateBorderMinima();
        MPI.Finalize();

    }

    private void calculateBorderMinima(int size, Points point, double minimum) {
        int averows = size / nprocs;
        int extra = size % nprocs;
        int[] offset = new int[1];
        offset[0] = 0;
        
        for ( int rank = 1; rank < nprocs; rank+=2 ) {
            rows[0] = ( rank < extra ) ? averows + 1 : averows;
            offset[0] += rows[0];
            
            System.out.println( "sending " + rows[0] + " rows to rank " + rank );

            MPI.COMM_WORLD.Send( offset, 0, 1, MPI.INT, rank, mtype );
            MPI.COMM_WORLD.Send( rows, 0, 1, MPI.INT, rank, mtype );
            MPI.COMM_WORLD.Send(getPoints, offset[0], rows[0], MPI.OBJECT, rank, mtype);
        }
    }
}
