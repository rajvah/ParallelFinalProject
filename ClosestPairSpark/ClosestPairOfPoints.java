import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import java.util.Iterator;

public class ClosestPairOfPoints {
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

    JavaRDD<Coordinates> p2 = points.sortBy(i -> {return i.x; }, true, 1 );
        for (Coordinates test : p2.collect())
        {
            System.out.println(test.x + ", " + test.y);
        }
    }
}