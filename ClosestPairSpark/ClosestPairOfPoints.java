import scala.Tuple2;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.DoubleFunction;

public class ClosestPairOfPoints {

    public static double euclideanDistance(Coordinates c1, Coordinates c2){

        return Math.sqrt(
            Math.pow( c1.x - c2.x, 2) + Math.pow( c1.y - c2.y, 2)
        );

    }

    public static void main(String[] args) {
        // start Sparks and read a given input file                                                                                                 
        String inputFile = args[0];
        SparkConf conf = new SparkConf().setAppName( "Closest Pair of points" ); 
        JavaSparkContext jsc = new JavaSparkContext( conf ); 
        JavaRDD<String> lines = jsc.textFile( inputFile );
        
        long startTime = System.currentTimeMillis();

        JavaRDD<Coordinates> points = lines.map( line -> {
            String[] pts = line.split("\\s+");
            Coordinates c = new Coordinates( Double.parseDouble(pts[0]), Double.parseDouble(pts[1]));
            return c;
        });
        
        JavaPairRDD<Coordinates,Coordinates> pointsPairRDD = points.cartesian(points); //creating a pair RDD of the coordinates, each point is a pair with every other point.

        JavaDoubleRDD distances = pointsPairRDD.mapToDouble(new DoubleFunction<Tuple2<Coordinates, Coordinates>>() { //storing the distances in JavaDoubleRDD
            @Override
            public double call(Tuple2<Coordinates, Coordinates> t) throws Exception {
                double distance = euclideanDistance(t._1, t._2);
                if(distance == 0) {
                    return Double.MAX_VALUE; //in case the point calculates the distance with itself, the distance will come as zero. So turning it into
                                            // MIX_VALUE of double so that it will not be considered in finding the minimum distance
                } else {
                    return distance;
                }
            }
            
        });

        System.out.println("Minimum distance: " + distances.min()); //printing the minimum value in JavaDoubleRDD
        long endTime = System.currentTimeMillis();
        long totalElapsed = endTime - startTime;
        System.out.println("Total time elapsed: " + totalElapsed);
        jsc.close();
    }
}
