import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.InputSplit;

public class ClosestPairOfPoints {

    public static class SortingMap extends MapReduceBase implements Mapper<LongWritable, Text, DoubleWritable, Text> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key, Text value, OutputCollector<DoubleWritable, Text> output, Reporter reporter) throws IOException {
            String line = value.toString();
            String[] points = line.split("\\s+");
            Double xCoordinate = Double.parseDouble(points[0]);

            output.collect(new DoubleWritable(xCoordinate), new Text(points[1]));
        }
    }

    public static class SortingReduce extends MapReduceBase implements Reducer<DoubleWritable, Text, DoubleWritable, Text> {
        public void reduce(DoubleWritable key, Iterator<Text> values, OutputCollector<DoubleWritable, Text> output, Reporter reporter) throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                Text value = values.next();
                output.collect(key, value);
            }
        }
    }

    public static class PointsMappper extends MapReduceBase implements Mapper<LongWritable, Text, DoubleWritable, Text> {
        public void map(LongWritable key, Text value, OutputCollector<DoubleWritable, Text> output, Reporter reporter) throws IOException {

        }
    }

    public static void main(String[] args) throws Exception {
        JobConf job1 = new JobConf(ClosestPairOfPoints.class);
        job1.setJobName("wordcount");

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

    }
}
