/**
 *
 *  Class to represent utility for Points
 *  The class includes static function to perform all related utilities
 *  Functionality includes converting the string list of points to an array list as (x, y)
 *  Sorting the list along the X coordinate
 *  Sorting the list along the Y coordinate
 *  Calculating the minimum distance between two points in the given list of points
 *
 * @author Harshit Rajvaidya
 */

//package Assignment2;

import mpi.*;
import java.util.*;

public class PointsUtils {

    public final static int TAG_FROM_MASTER = 1;
    public final static int TAG_FROM_SLAVE = 2;
    public final static int MASTER = 0;

    //Params: int numberOfPoints, ArrayList of string points to be parsed into Double
    //Returns: ArrayList<ArrayList<Double>> mapped to coordinate points
    public static ArrayList<ArrayList<Double>> ConvertStringToArrayList(int numberOfPoints , ArrayList<String> data) {
        ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
        for(int i = 0; i < numberOfPoints; i++){
            ArrayList<Double> point = new ArrayList<>();

            // split the string by using regular expression to factor in number of spaces between the points
            String [] pointStr = data.get(i).split("\\s+");

            try{
                //Params: s – the string to be parsed.
                //Returns: the double value represented by the string argument.
                // x coordinate is at 0 index
                point.add(Double.parseDouble(pointStr[0]));
                // y coordinate is at 1 index
                point.add(Double.parseDouble(pointStr[1]));
            }
            catch (NumberFormatException e){
                System.err.println(" Parsing exception when converting numbers. The input format should be of type double " + e);
                System.exit(1);
            }
            points.add(point);
        }
        return points;
    }

    public static void sortByX(PointsGrabber[] getPoints){
        Comparator<PointsGrabber> sortingByX = Comparator.comparingDouble(PointsGrabber::getX);
        Arrays.sort(getPoints, sortingByX);
    }

    public static void sortByY(PointsGrabber[] getPoints){
        Comparator<PointsGrabber> sortingByY = Comparator.comparingDouble(PointsGrabber::getY);
        Arrays.sort(getPoints, sortingByY);
    }

    
    //Params:
    // * ArrayList<ArrayList<Double>> of coordinates sorted along X coordinates,
    // * ArrayList<ArrayList<Double>> of coordinates sorted along Y coordinates
    // * int start of the X coordinates list
    // * int end of the X coordinates list
    // Returns: ArrayList<ArrayList<Double>> of coordinates sorted along the x coordinate
    // Pre: A list of all the points along the X coordinates
    // Post: A double minimum distance between the all points in the 2D plane
    public static Double GetMinDistance(PointsGrabber[] xSortedPoints, PointsGrabber[] ySortedPoints, int start ,int end){

        // return section of the recursive function
        // if number of elements in the list is less than 4 then use brute force
        // to calculate distance and return min distance between them
        if(xSortedPoints.length < 4) {
            Double min = Double.MAX_VALUE;
            // since size is equal to 3 or less, using brute force to calculate the minimum distance
            for (int i = 0; i < xSortedPoints.length; i++) {
                for (int j = i+1; j < xSortedPoints.length; j++) {
                    min = Math.min(min, EuclideanDistance(xSortedPoints[i].getX(),xSortedPoints[i].getY(), xSortedPoints[j].getX(), xSortedPoints[j].getY()));
                }
            }
            return min;
        }


        // find the middle point of the array
        // if number is odd, keep the number of elements in the left group 1 more
        // than on the right side
        int mid = (xSortedPoints.length+1) /2;  

        // l is the plane dividing line
        Double l = ((xSortedPoints[mid-1].getX() + xSortedPoints[mid].getX()) / 2);

        // Create a sublist of the points that lie between the start and the dividing line L
        PointsGrabber[] AlistFirst = Arrays.copyOfRange(xSortedPoints, 0, mid);

        // Create a sublist of the points that lie between the dividing line L and
        // the last of existing point in the sorted array
        PointsGrabber[] AlistSecond = Arrays.copyOfRange(xSortedPoints, mid, xSortedPoints.length);

        // delta is the minimum distance between the points that lie on the either side of our line L
       Double delta = Math.min( GetMinDistance( AlistFirst, ySortedPoints, start, start + mid  -1),
                GetMinDistance( AlistSecond, ySortedPoints, start + mid , end));

        // minDistAcrossBoundry is the minimum distance bwteen points that lie across the point L
        Double minDistAcrossBoundry = MinDistanceAcrossBoundary(l,delta, ySortedPoints);

        // return the minimum between delta and minDistAcrossBoundry
        return Math.min(delta, minDistAcrossBoundry);

    }

    // finding the min distance between points at the left and right of our plane dividing line
    // Params:
    // * Double L as the plane dividing line
    // * Double delta as the minimum distance between the 2 planes
    // * ArrayList<ArrayList<Double>> of all coordinates sorted along Y.
    //Returns: Double minimum distance between the points on the either side of plane dividing line
    // Pre:
    // * A Double delta which is the minimum distance between the points along the dividing plane
    // * A dividing line L
    // Post: A double minimum distance between the points across the dividing line L
    private static Double MinDistanceAcrossBoundary(Double l , Double delta, PointsGrabber[] ySortedPoints){
        //array list to store all the points that lie in the delta range
        PointsGrabber[] pointsBetweenPlane = new PointsGrabber[ySortedPoints.length];

        //creating lower and upper bound which will be + or - delta away from Line L
        double upperBound = l + delta;
        double lowerBound = l - delta;

        int resultantIndex = 0;
        for(int index = 0; index < ySortedPoints.length; index++){
            if(ySortedPoints[index].getX() <= upperBound && ySortedPoints[index].getX() >= lowerBound){
                pointsBetweenPlane[resultantIndex++] = ySortedPoints[index]; 
            }
        }

        pointsBetweenPlane = Arrays.copyOf(pointsBetweenPlane, resultantIndex);
        
        sortByY(pointsBetweenPlane);

        // using brute force to calculate the minimum distance along sorted y corrdiantes

        Double min = Double.MAX_VALUE;
        for (int i = 0; i < pointsBetweenPlane.length; i++) {
            for (int j = i+1; j < Math.min(pointsBetweenPlane.length, i+7) ; j++) {
                min = Double.min(min, EuclideanDistance(pointsBetweenPlane[i].getX(),pointsBetweenPlane[i].getY(), pointsBetweenPlane[j].getX(),pointsBetweenPlane[j].getY()));
            }
        }
        return min;
    }


    // Pre: 2 Array list as coordinates
    // Post: Euclidean distance between the points
    // Calculates the distance between the points by using the formula : sqrt((x1 – x2)^2 + (y1 – y2)^2)
    private static Double EuclideanDistance(Double x1, Double y1, Double x2, Double y2){
        return Math.sqrt(
                Math.pow(y2-y1, 2) + Math.pow(x2-x1, 2)
        );
    }

    public static PointsGrabber[] init(int size, Points points) {
        PointsGrabber[] getPoints = new PointsGrabber[size];
        for(int i = 0; i < size; i++){
            getPoints[i] = new PointsGrabber(points.getPoints().get(i).get(0), points.getPoints().get(i).get(1));
        }
        return getPoints;
    }

    public static Double calculateLocalMinima(PointsGrabber[] xSortedPoints) throws MPIException{

        int myrank = MPI.COMM_WORLD.Rank();
        int nprocs = MPI.COMM_WORLD.Size();

        int size = xSortedPoints.length;

        int averows;                // average #rows allocated to each rank
        int extra;                 // extra #rows allocated to some ranks
        int offset[] = new int[1]; // offset in row
        int rows[] = new int[1];   // the actual # rows allocated to each rank
        int mtype = 0;                 // message type (tagFromMaster or tagFromSlave)
        Double finalMinimum = Double.MAX_VALUE; 
        Double masterMinimum = 0.0;
        Double rankMinimum = 0.0;
        PointsGrabber[] receivedXSorted = null;
        PointsGrabber[] ySortedPoints = null;
        int borderIndex = 0;
        int sizeOfPointsList[] = new int[1];
        PointsGrabber[] pointsArr = null;

        if(myrank == 0){
            averows = size / nprocs;
            extra = size % nprocs;
            offset[0] = 0;
            mtype = TAG_FROM_MASTER;

            
            for (int rank = 0; rank < nprocs; rank++ ) {
                rows[0] = ( rank < extra ) ? averows + 1 : averows;
                if(rank == 0){ // This is useful only when rank 0 has to take care of the remainder rows. 
                    receivedXSorted = Arrays.copyOfRange(xSortedPoints,0, rows[0]);
                }
                //System.out.println( "sending " + rows[0] + " rows to rank " + rank );
                if(rank != 0){
                    MPI.COMM_WORLD.Send(offset, 0, 1, MPI.INT, rank, mtype);
                    MPI.COMM_WORLD.Send(rows, 0, 1, MPI.INT, rank, mtype );
                    MPI.COMM_WORLD.Send(xSortedPoints, offset[0], rows[0], MPI.OBJECT, rank, mtype);
                }
                offset[0] += rows[0];
            }

            ySortedPoints = receivedXSorted.clone();

            masterMinimum = GetMinDistance(receivedXSorted, ySortedPoints, 0, receivedXSorted.length-1);

            //System.out.println("Rank: " + myrank + " min: " + masterMinimum);

            Double tempMin = 0.0;
            for ( int source = 1; source < nprocs; source++ ) {
                MPI.COMM_WORLD.Recv(tempMin,0, 1, MPI.DOUBLE, source, mtype);
                //System.out.println("Master Received : " + tempMin + " from rank " + source);
                masterMinimum = Math.min(masterMinimum, tempMin);
            }
            //System.out.println("Final Min after checking: " + masterMinimum);

            // System.out.println("Final Min: " + masterMinimum);
            // Date endTime = new Date( );
            // System.out.println( "time elapsed = " + ( endTime.getTime( ) - startTime.getTime( ) ) + " msec" );

            //Sending finalMinimum for border calculation
            for(int rank = 1; rank < nprocs; rank++) {
                MPI.COMM_WORLD.Send(masterMinimum, 0, 1, MPI.DOUBLE, rank, mtype );
            }
        }
        else{
            mtype = TAG_FROM_MASTER;
	        MPI.COMM_WORLD.Recv(offset, 0, 1, MPI.INT, MASTER, mtype );
	        MPI.COMM_WORLD.Recv(rows, 0, 1, MPI.INT, MASTER, mtype );
            receivedXSorted = new PointsGrabber[rows[0]];
            MPI.COMM_WORLD.Recv(receivedXSorted, 0, rows[0], MPI.OBJECT, MASTER, mtype);

            ySortedPoints = receivedXSorted.clone();

            rankMinimum = GetMinDistance(receivedXSorted, ySortedPoints, 0, receivedXSorted.length-1);
            // System.out.println("Rank: " + myrank + " First Element: " + receivedXSorted[0].getX() + " , " + receivedXSorted[0].getY() + " Last Element: " + receivedXSorted[receivedXSorted.length-1].getX() + " , " + receivedXSorted[receivedXSorted.length-1].getY());
            // System.out.println("Rank: " + myrank + " min: " + rankMinimum);

            MPI.COMM_WORLD.Send(rankMinimum, 0, 1, MPI.DOUBLE, MASTER, mtype);

            //Receiving final minimum from the master for border calculation
            MPI.COMM_WORLD.Recv(masterMinimum, 0, 1, MPI.DOUBLE, MASTER, mtype );
            //System.out.println("rank: " + myrank + " recv min: " +finalMinimum);

            //Trying to create border arrays
            //calculateBorderMinima(xSortedPoints, finalMinimum);
        }
        if(myrank == 0){
            //System.out.println("Final Min: " + masterMinimum);
            return masterMinimum;
        }
        return masterMinimum;
    }

    public static void calculateBorderMinima(PointsGrabber[] points, double minimum, Date startTime) throws MPIException {
        //System.out.println("Adjacent Ranks Minima calculation function received: " + minimum);

        int myrank = MPI.COMM_WORLD.Rank();
        int nprocs = MPI.COMM_WORLD.Size();
        int size = points.length;
        
        Double[] reducedMinimum = new Double[1];
        reducedMinimum[0] = minimum;
        Double[] borderMinimum = new Double[1];

        int averows = size/nprocs;               // average #rows allocated to each rank
        int extra = size%nprocs;                 // extra #rows allocated to some ranks
        int offset[] = new int[1]; // offset in row
        offset[0] = 0;
        int sizeOfPointsList[] = new int[1]; // size of arrays which we are sending
        PointsGrabber[] pointsArr = null;
        int borderIndex = 0;
        int rows = 0;   // the actual # rows allocated to each rank
        
        for ( int rank = 1; rank < nprocs; rank++ ) {
            if (rank == myrank) {
                rows = ( rank < extra ) ? averows + 1 : averows;
                borderIndex = (myrank < extra) ? rank * rows : rank * (rows + 1) + (myrank - extra) * rows;

                // System.out.println("Rank: " + rank + " rows: " + rows + " borderIndex: " + borderIndex);
                
                int nextBorderIndex = borderIndex + rows;
                double x = points[0].getX();
                List<PointsGrabber> pointsList = new ArrayList<>();
                for (int i = 0; i < points.length; i++) {
                    if (points[i].getX() > x + minimum) {
                        break;
                    }
                    if (points[i].getX() <= x + minimum) {
                        pointsList.add(points[i]);
                    }
                }

                // for(int i = 0; i < pointsList.size(); i++) {
                //     System.out.print("List A " + pointsList.get(i).getX() + " " + pointsList.get(i).getY() + ",");
                // }
                // System.out.println();

                sizeOfPointsList[0] = pointsList.size();
                MPI.COMM_WORLD.Send(sizeOfPointsList, 0, 1, MPI.INT, rank - 1, TAG_FROM_SLAVE);
                if (!pointsList.isEmpty()) {
                    pointsArr = new PointsGrabber[pointsList.size()];
                    pointsArr = pointsList.toArray(pointsArr);
                    MPI.COMM_WORLD.Send(pointsArr, offset[0], pointsArr.length, MPI.OBJECT, rank - 1, TAG_FROM_SLAVE);

                    //System.out.println("Sent : " + pointsArr.length + " to rank: " + (rank-1));
                
                }
            }
        }
        for (int rank = 0; rank < nprocs -1; rank++) {
            if (myrank == rank) {
                MPI.COMM_WORLD.Recv(sizeOfPointsList, 0, 1, MPI.INT, rank + 1, TAG_FROM_SLAVE);
                if (sizeOfPointsList[0] != 0) {
                    pointsArr = new PointsGrabber[sizeOfPointsList[0]];
                    MPI.COMM_WORLD.Recv(pointsArr, 0, sizeOfPointsList[0], MPI.OBJECT, rank + 1, TAG_FROM_SLAVE);

                    //System.out.println("Received : " + pointsArr.length + " from rank: " + (rank+1));

                    List<PointsGrabber> pList = new ArrayList<>();
                    Double x = points[0].getX();
                    for(int i = pointsArr.length - 1; i >= 0; i--) {
                        if (pointsArr[i].getX() > x + minimum) {
                            break;
                        }
                        if (pointsArr[i].getX() <= x + minimum) {
                            pList.add(pointsArr[i]);
                        }
                    }

                    // for(int i = 0; i < pList.size(); i++) {
                    //     System.out.print("List C " + pList.get(i).getX() + " " + pList.get(i).getY() + ",");
                    // }
                    // System.out.println();

                    // for(int i = 0; i < pointsArr.length; i++) {
                    //     System.out.print("List D " + pointsArr[i].getX() + " " + pointsArr[i].getY() + ",");
                    // }
                    //System.out.println();
                    
                    if(!pList.isEmpty()) {
                        pList.addAll(Arrays.asList(pointsArr));
                        Set<PointsGrabber> setToRemoveDupli = new LinkedHashSet<>();
                        setToRemoveDupli.addAll(pList);
                        pList.clear();
                        pList.addAll(setToRemoveDupli);
                        pointsArr = new PointsGrabber[pList.size()];
                        pointsArr = pList.toArray(pointsArr);
                        sortByY(pointsArr);
                        
                        // for(int i = 0; i < pointsArr.length; i++) {
                        //     System.out.print("List B " + pointsArr[i].getX() + " " + pointsArr[i].getY() + ",");
                        // }
                        // System.out.println();

                        for (int i = 0; i < pointsArr.length; i++ ) {
                            for(int j = i+1; j < Math.min(pointsArr.length , i+7) ; j++) {
                                minimum = Double.min(minimum, EuclideanDistance(pointsArr[i].getX(), pointsArr[i].getY(), pointsArr[j].getX(), pointsArr[j].getY()));
                            }
                        }                        
                    }
                    // borderMinimum[0] = minimum;
                    // MPI.COMM_WORLD.Reduce(borderMinimum, 0, reducedMinimum, 0, reducedMinimum.length, MPI.DOUBLE, MPI.MIN, MASTER);
                }
            }
        }
        //System.out.println("Phinal minimum : " + reducedMinimum[0]);
        if(myrank == 0){
            System.out.println("Final min: " + minimum);
            Date endTime = new Date( );
            System.out.println( "time elapsed = " + ( endTime.getTime( ) - startTime.getTime( ) ) + " msec" );
        }
    }
}

