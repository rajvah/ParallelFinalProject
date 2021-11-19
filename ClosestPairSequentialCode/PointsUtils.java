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

package Assignment2;

import java.util.ArrayList;

public class PointsUtils {

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

    //Pre: Unsorted list ArrayList<ArrayList<Double>> of coordinates
    //Post: ArrayList<ArrayList<Double>> of coordinates sorted along the x coordinate
    public static ArrayList<ArrayList<Double>> SortByXCoordinate( ArrayList<ArrayList<Double>> points){
        // using custom comparator to sort
        points.sort((o1, o2) -> o1.get(0).compareTo(o2.get(0)));
        ArrayList<ArrayList<Double>> p1 = (ArrayList<ArrayList<Double>>) points.clone();
        return p1;
    }

    //Pre: Unsorted ArrayList<ArrayList<Double>> of coordinates
    //Post: ArrayList<ArrayList<Double>> of coordinates sorted along the y coordinate
    public static ArrayList<ArrayList<Double>> SortByYCoordinate( ArrayList<ArrayList<Double>> points){
        // using custom comparator to sort
        points.sort((o1, o2) -> o1.get(1).compareTo(o2.get(1)));
        ArrayList<ArrayList<Double>> p1 = (ArrayList<ArrayList<Double>>) points.clone();
        return p1;
    }

    //Params:
    // * ArrayList<ArrayList<Double>> of coordinates sorted along X coordinates,
    // * ArrayList<ArrayList<Double>> of coordinates sorted along Y coordinates
    // * int start of the X coordinates list
    // * int end of the X coordinates list
    // Returns: ArrayList<ArrayList<Double>> of coordinates sorted along the x coordinate
    // Pre: A list of all the points along the X coordinates
    // Post: A double minimum distance between the all points in the 2D plane
    public static Double GetMinDistance(ArrayList<ArrayList<Double>> sortedX, ArrayList<ArrayList<Double>> sortedY, int start ,int end){

        // return section of the recursive function
        // if number of elements in the list is less than 4 then use brute force
        // to calculate distance and return min distance between them

        if(sortedX.size() < 4) {
            Double min = Double.MAX_VALUE;
            // since size is equal to 3 or less, using brute force to calculate the minimum distance
            for (int i = 0; i < sortedX.size(); i++) {
                for (int j = i+1; j < sortedX.size(); j++) {
                    min = Math.min(min, EuclideanDistance(sortedX.get(i), sortedX.get(j)));
                }
            }
            // print out min distance between the points
            System.out.println(String.format("D[%d,%d]: %.4f", start, end, min));
            return min;
        }


        // find the middle point of the array
        // if number is odd, keep the number of elements in the left group 1 more
        // than on the right side
        int mid = (sortedX.size() +1) /2;

        // l is the plane dividing line
        Double l = ((sortedX.get(mid-1).get(0) + sortedX.get(mid).get(0)) / 2);

        // Create a sublist of the points that lie between the start and the dividing line L
        ArrayList<ArrayList<Double>> AlistFirst = new ArrayList<>();
        AlistFirst.addAll(sortedX.subList(0, mid));
        // Create a sublist of the points that lie between the dividing line L and
        // the last of existing point in the sorted array
        ArrayList<ArrayList<Double>> AlistSecond = new ArrayList<>();
        AlistSecond.addAll(sortedX.subList(mid,  sortedX.size()));

        // delta is the minimum distance between the points that lie on the either side of our line L
        Double delta = Math.min( GetMinDistance( AlistFirst, sortedY, start, start + mid  -1),
                GetMinDistance( AlistSecond, sortedY, start + mid , end ));
        // minDistAcrossBoundry is the minimum distance bwteen points that lie across the point L
        Double minDistAcrossBoundry = MinDistanceAcrossBoundary(l,delta, sortedY);

        System.out.println(String.format("D[%d,%d]: %.4f", start, end, Math.min(delta, minDistAcrossBoundry)));
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
    private static Double MinDistanceAcrossBoundary(Double l , Double delta, ArrayList<ArrayList<Double>> sortedY){
        //array list to store all the points that lie in the delta range
        ArrayList<ArrayList<Double>> pointsBetweenPlane = new ArrayList<ArrayList<Double>>();

        //creating lower and upper bound which will be + or - delta away from Line L
        double upperBound = l + delta;
        double lowerBound = l - delta;
        // gathering the points in O(n)
        sortedY.forEach(point -> {
            if(point.get(0) <= upperBound && point.get(0) >= lowerBound){
                pointsBetweenPlane.add(point);
            }
        });

        // using brute force to calculate the minimum distance along sorted y corrdiantes

        Double min = Double.MAX_VALUE;
        for (int i = 0; i < pointsBetweenPlane.size(); i++) {
            for (int j = i+1; j < Math.min(pointsBetweenPlane.size(), 7) ; j++) {
                min = Double.min(min, EuclideanDistance(pointsBetweenPlane.get(i), pointsBetweenPlane.get(j)));
            }
        }
        return min;
    }


    // Pre: 2 Array list as coordinates
    // Post: Euclidean distance between the points
    // Calculates the distance between the points by using the formula : sqrt((x1 – x2)^2 + (y1 – y2)^2)
    private static Double EuclideanDistance(ArrayList<Double> p1, ArrayList<Double> p2 ){

        return Math.sqrt(
                Math.pow(p2.get(1) - p1.get(1), 2) + Math.pow(p2.get(0) - p1.get(0), 2)
        );
    }

}
