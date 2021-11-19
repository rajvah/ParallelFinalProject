/**
 * *
 *  *  Class to represent the Main functionality
 *  *  The class includes a public static void main
 *  *  Functionality is to parse the points in the program2data.txt file and get the
 *  *  minimum distance between the points inside the file
 *  *
 * @author Harshit Rajvaidya
 */

package Assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class Assignment2 {

    public static void main(String[] args) throws IOException {

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
        ArrayList<ArrayList<Double>> xsortedPoints = PointsUtils.SortByXCoordinate(points.getPoints());
        // get the points sorted along the y coordinates
        ArrayList<ArrayList<Double>> ysortedPoints = PointsUtils.SortByYCoordinate(points.getPoints());

        // Pre: Sorted list of points
        // Post: minimum distance across the points
        PointsUtils.GetMinDistance(xsortedPoints, ysortedPoints,
                0, xsortedPoints.size()-1);

    }
}
