//Personal Debugging file for Sidhant Bansal
// import java.util.*;
// class Test{

//     static PointsGrabber[] getPoints = new PointsGrabber[2];

//     public static void sortByXPlease(PointsGrabber[] getPoints){
//         Comparator<PointsGrabber> sortingByX = Comparator.comparingDouble(PointsGrabber::getX);
//         Arrays.sort(getPoints, sortingByX);
//     }

//     public static void main(String[] args){
//         System.out.println("Hello World");
//         PointsGrabber[] getPoints = new PointsGrabber[2];
//         getPoints[0] = new PointsGrabber(5.0,2.0);
//         getPoints[1] = new PointsGrabber(3.0,2.0);
//         sortByXPlease(getPoints);

//         for(int i = 0; i < getPoints.length; i++){
//             System.out.print(getPoints[i].getX() + ",");
//             System.out.print(getPoints[i].getY() + " ");
//             System.out.println();
//         }
//     }
// }