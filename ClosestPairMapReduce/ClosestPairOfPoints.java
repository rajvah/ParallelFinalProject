import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapred.InputSplit;

public class ClosestPairOfPoints {

    private static Double EuclideanDistance(ArrayList<Double> p1, ArrayList<Double> p2 ){

        return Math.sqrt(
                Math.pow(p2.get(1) - p1.get(1), 2) + Math.pow(p2.get(0) - p1.get(0), 2)
        );
    }
    public static class SortingMap extends MapReduceBase implements Mapper<LongWritable, Text, DoubleWritable, Text> {

        public void map(LongWritable key, Text value, OutputCollector<DoubleWritable, Text> output, Reporter reporter) throws IOException {
            String line = value.toString();
            String[] points = line.split("\\s+");
            Double xCoordinate = Double.parseDouble(points[0]);
            output.collect(new DoubleWritable(xCoordinate), new Text(points[1]));
        }
    }

    public static class SortingReduce extends MapReduceBase implements Reducer<DoubleWritable, Text, DoubleWritable, Text> {
        public void reduce(DoubleWritable key, Iterator<Text> values, OutputCollector<DoubleWritable, Text> output, Reporter reporter) throws IOException {
            while (values.hasNext()) {
                Text value = values.next();
                output.collect(key, value);
            }
        }
    }

    public static class PointsMappper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
        public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
            InputSplit is = reporter.getInputSplit();
            String splitId = MD5Hash.digest(is.toString()).toString();
            output.collect(new Text(splitId), value);
        }
    }

    public static class PointsCombiner extends MapReduceBase implements Reducer<Text, Text, Text, DoubleWritable> {

        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
            ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();

            while (values.hasNext()) {
                ArrayList<Double> point = new ArrayList<>();
                String[] pointStr = values.next().toString().split("\\s+");
                point.add(Double.parseDouble(pointStr[0]));
                point.add(Double.parseDouble(pointStr[1]));

                points.add(point);
            }
            System.out.println("Humar pointsva");
            System.out.println(points);
            Double min = Double.MAX_VALUE;
            for( int i =0 ; i < points.size(); i++){
                for(int j=i+1 ; j < points.size(); j++){
                    System.out.println("Humar distance");
                    System.out.println(EuclideanDistance(points.get(i), points.get(j)));
                    min = Double.min(EuclideanDistance(points.get(i), points.get(j)), min);
                }
            }
            output.collect(key, new DoubleWritable(min));
        }
    }

    // public static class PointsReducer extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, DoubleWritable> {
    // int count = 1;
    // public void reduce(Text key, Iterator<DoubleWritable> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
    // 	Double fin = Double.MAX_VALUE;
    // 	// int k = key.toString()
    // 	while (values.hasNext()) {
    // 		fin = Double.min(values.next().get(), fin);
    // 	}
    // 	output.collect(key, new DoubleWritable(fin));
    // }
    // }

    public static void main(String[] args) throws Exception {
        JobConf job1 = new JobConf(ClosestPairOfPoints.class);
        job1.setJobName("Sorter");

        job1.setOutputKeyClass(DoubleWritable.class);
        job1.setOutputValueClass(Text.class);

        job1.setMapperClass(SortingMap.class);
        job1.setCombinerClass(SortingReduce.class);
        job1.setReducerClass(SortingReduce.class);

        job1.setInputFormat(TextInputFormat.class);
        job1.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job1, new Path(args[0]));
        FileOutputFormat.setOutputPath(job1, new Path(args[1]));

        JobClient.runJob(job1);

        JobConf job2 = new JobConf(ClosestPairOfPoints.class);
        job2.setJobName("MinDistanceCalculator");

        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);

        job2.setMapperClass(PointsMappper.class);
        // job1.setCombinerClass(PointsCombiner.class);
        job2.setReducerClass(PointsCombiner.class);

        job2.setInputFormat(TextInputFormat.class);
        job2.setOutputFormat(TextOutputFormat.class);
        // job2.set("mapred.min.split.size", "90");
        // job2.set("mapred.max.split.size", "90");
        FileInputFormat.setInputPaths(job2, new Path(args[1] + "/part-00000"));
        FileOutputFormat.setOutputPath(job2, new Path(args[1] + "_new_version"));

        JobClient.runJob(job2);
    }
}
