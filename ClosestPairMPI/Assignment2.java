/**
 * *
 *  *  Class to represent the Main functionality
 *  *  The class includes a public static void main
 *  *  Functionality is to parse the points in the program2data.txt file and get the
 *  *  minimum distance between the points inside the file
 *  *
 * @author Harshit Rajvaidya
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

public class Assignment2 {

    int myrank = 0;
    int nprocs = 0;

    double[] x_coord = null;
    double[] y_coord = null;
    double[] dummy = null;
    pointsGrabber[] getPoints = null;


    int averows;               // average #rows allocated to each rank
    int extra;                 // extra #rows allocated to some ranks
    int offset[] = new int[1]; // offset in row
    int rows[] = new int[1];   // the actual # rows allocated to each rank
    int mtype;                 // message type (tagFromMaster or tagFromSlave )

    final static int tagFromMaster = 1;
    final static int tagFromSlave = 2;
    final static int master = 0;

    private void init(int size, Points points){
        for(int i = 0; i < size; i++){
            x_coord[i] = points.getPoints().get(i).get(0);
        }
        for(int i = 0; i < size; i++){
            y_coord[i] = points.getPoints().get(i).get(1);
        }
        for(int i = 0; i < size; i++){
            getPoints[i] = new pointsGrabber(points.getPoints().get(i).get(0), points.getPoints().get(i).get(1));
        }
    }

    public Assignment2(int size, Points points) throws MPIException{

        myrank = MPI.COMM_WORLD.Rank( );
	    nprocs = MPI.COMM_WORLD.Size( );

        x_coord = new double[size];
        y_coord = new double[size];
        dummy = new double[size];
        getPoints = new pointsGrabber[size];

        

        if(myrank == 0){

            init(size, points);
            // System.out.println(getPoints.length);
            // for(int i = 0; i < size; i++){
            //     System.out.print(getPoints[i].getX() + "," + getPoints[i].getY());
            //     System.out.println();
            // }


            averows = size / nprocs;
	        extra = size % nprocs;
	        offset[0] = 0;
	        mtype = tagFromMaster;

            Date startTime = new Date( );

            for ( int rank = 0; rank < nprocs; rank++ ) {
		        rows[0] = ( rank < extra ) ? averows + 1 : averows;
		        System.out.println( "sending " + rows[0] + " rows to rank " + rank );

                if(rank != 0){
                    MPI.COMM_WORLD.Send( offset, 0, 1, MPI.INT, rank, mtype );
                    System.out.println("Off Sent: " + offset[0]);
		            MPI.COMM_WORLD.Send( rows, 0, 1, MPI.INT, rank, mtype );
                    MPI.COMM_WORLD.Send(getPoints, offset[0], rows[0], MPI.OBJECT, rank, mtype);
                    // MPI.COMM_WORLD.Send(x_coord, offset[0], rows[0], MPI.DOUBLE, rank, mtype );
                    // MPI.COMM_WORLD.Send(y_coord, offset[0], rows[0], MPI.DOUBLE, rank, mtype );
                }
                offset[0] += rows[0];
            }
            
            System.out.println( "M : " + getPoints[rows[0]].getX() + " " +getPoints[rows[0]].getY());
            ArrayList<ArrayList<Double>> listTemp = new ArrayList();
            ArrayList<ArrayList<Double>> sortedXListTemp = new ArrayList();
            ArrayList<ArrayList<Double>> sortedYListTemp = new ArrayList();

            for(int i = 0; i < rows[0]; i++){
                ArrayList<Double> anotherTemp = new ArrayList();
                anotherTemp.add(getPoints[i].getX());
                anotherTemp.add(getPoints[i].getY());
                listTemp.add(anotherTemp);
            }

            sortedXListTemp = PointsUtils.SortByXCoordinate(listTemp);
            sortedYListTemp = PointsUtils.SortByYCoordinate(listTemp);
            
            PointsUtils.GetMinDistance(sortedXListTemp, sortedYListTemp, 0, sortedXListTemp.size()-1);

            // for(int i = 0; i < size; i++){
            //     dummy[i] = x_coord[i] + y_coord[i];
            // }

            // Collect results from each slave.
	        // int mytpe = tagFromSlave;
	        // for ( int source = 1; source < nprocs; source++ ) {
		    // MPI.COMM_WORLD.Recv( offset, 0, 1, MPI.INT, source, mtype );
		    // MPI.COMM_WORLD.Recv( rows, 0, 1, MPI.INT, source, mtype );
            // MPI.COMM_WORLD.Recv(dummy, offset[0], rows[0],MPI.DOUBLE, source, mtype);
            // }

            Date endTime = new Date( );
            
            // System.out.println("Dummy");
            // for(int i = 0; i < dummy.length; i++){
            //     System.out.println(dummy[i]);
            // }

            System.out.println( "time elapsed = " + ( endTime.getTime( ) - startTime.getTime( ) ) + " msec" );

        }

        else{
            int mtype = tagFromMaster;
	        MPI.COMM_WORLD.Recv( offset, 0, 1, MPI.INT, master, mtype );
	        MPI.COMM_WORLD.Recv( rows, 0, 1, MPI.INT, master, mtype );
            MPI.COMM_WORLD.Recv(getPoints, 0, rows[0], MPI.OBJECT, master, mtype);
            // MPI.COMM_WORLD.Recv( x_coord, 0, rows[0], MPI.DOUBLE, master, mtype );
            // MPI.COMM_WORLD.Recv( y_coord, 0, rows[0], MPI.DOUBLE, master, mtype );
            
            System.out.println( "H : " + getPoints[0].getX() + " " +getPoints[0].getY());
            // for(int i = 0; i < size; i++){
            //     System.out.println(getPoints[i].getX() + "," + getPoints[i].getY());
            // }

            // MPI.COMM_WORLD.Send( offset, 0, 1, MPI.INT, master, mtype );
	        // MPI.COMM_WORLD.Send( rows, 0, 1, MPI.INT, master, mtype );
            // MPI.COMM_WORLD.Send( dummy, 0, rows[0], MPI.DOUBLE, master, mtype );
        }
    }


    public static void main(String[] args) throws IOException, MPIException {

        MPI.Init(args) ;

        //if(my_rank == 0){
            // Creates a new File instance by converting the given pathname string into an abstract pathname.
            // If the given string is the empty string, then the result is the empty abstract pathname
            File file = new File("program2data.txt");

            // Creates a buffering character-input stream that uses a default-sized input buffer.
            //Params:
            //  Input File Reader
            BufferedReader br = new BufferedReader(new FileReader(file));

            //Reads a line of text. A line is considered to be terminated by any one of a line feed ('\n') or by reaching the end-of-file (EOF).
            //Returns:
            //A String containing the contents of the line, not including any line-termination characters,
            // or null if the end of the stream has been reached without reading any characters
            //Throws:
            //IOException – If an I/O error occurs
            String line = br.readLine();

            //Returns: the integer value represented by the argument in decimal.
            //Throws: NumberFormatException – if the string does not contain a parsable integer.
            int number = Integer.parseInt(line);
            ArrayList<String> data = new ArrayList();

            for(int i = 0; i < number; ++i) {
                // .trim() is a failsafe here to ensure extra whitespaces do not cause an issue with
                //Removes white space characters from the beginning and end of the string.
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

            // get the points sorted along the x coordinates
            // ArrayList<ArrayList<Double>> xsortedPoints = PointsUtils.SortByXCoordinate(points.getPoints());
            // // get the points sorted along the y coordinates
            // ArrayList<ArrayList<Double>> ysortedPoints = PointsUtils.SortByYCoordinate(points.getPoints());

            // // Pre: Sorted list of points
            // // Post: minimum distance across the points
            // PointsUtils.GetMinDistance(xsortedPoints, ysortedPoints,
            //         0, xsortedPoints.size()-1);
        //}
        //System.out.println(points.getPoints());

        // pointsGrabber pg = new pointsGrabber(points.getPoints().get(0).get(0), points.getPoints().get(0).get(1));
        // System.out.println(pg.getX());
        // System.out.println(pg.getY());
        int size = points.getNumberOfPoints();
        MPI.COMM_WORLD.Bcast(size, 0, 1, MPI.INT, master);
        new Assignment2(size, points);
        MPI.Finalize();

    }
}
