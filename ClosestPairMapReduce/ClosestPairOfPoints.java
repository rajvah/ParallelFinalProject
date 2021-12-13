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

	public static class PointsReducer extends MapReduceBase implements Reducer<Text, Text, Text, DoubleWritable> { 
        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
			ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
		
			while (values.hasNext()) {
				ArrayList<Double> point = new ArrayList<>();
				String[] pointStr = values.next().toString().split("\\s+");
				point.add(Double.parseDouble(pointStr[0]));
                point.add(Double.parseDouble(pointStr[1]));

				points.add(point);
			}
			Double min = Double.MAX_VALUE;
			for( int i =0 ; i < points.size(); i++){
				for(int j=i+1 ; j < points.size(); j++){
					min = Double.min(EuclideanDistance(points.get(i), points.get(j)), min);
				}
			}

			output.collect(key, new DoubleWritable(min));
		}
	}


	public static class MinCalculatorMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, DoubleWritable> {
		public void map(LongWritable key, Text value, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
			String line = value.toString();
			String[] minMap = line.split("\\s+");
			Double min = Double.parseDouble(minMap[1]);
			output.collect(new Text("min"), new DoubleWritable(min));
		}
	}

	public static class MinCalculatorReducer extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, Text> {
	public void reduce(Text key, Iterator<DoubleWritable> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException { 
	    Double min = Double.MAX_VALUE;
		while (values.hasNext()) {
			Double value = Double.parseDouble(values.next().toString());

			min = Math.min(value, min);
	    }
		output.collect(key, new Text(min.toString()));
	}
    }
    
    public static void main(String[] args) throws Exception {

		long startTime = new Date().getTime();

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
		job2.setReducerClass(PointsReducer.class);
		
		job2.setInputFormat(TextInputFormat.class);
		job2.setOutputFormat(TextOutputFormat.class);
		// job2.set("mapred.min.split.size", "90");
		// job2.set("mapred.max.split.size", "90");
		String newInput = args[1] + "/part-00000";
		String newOutput = args[1] + "_new";
		FileInputFormat.setInputPaths(job2, new Path(newInput));
		FileOutputFormat.setOutputPath(job2, new Path(newOutput));

		JobClient.runJob(job2);

		JobConf job3 = new JobConf(ClosestPairOfPoints.class);
		job3.setJobName("FinalDistance");
		
		job3.setOutputKeyClass(Text.class);
		job3.setOutputValueClass(DoubleWritable.class);
		
		job3.setMapperClass(MinCalculatorMapper.class);
		job3.setReducerClass(MinCalculatorReducer.class);
		
		job3.setInputFormat(TextInputFormat.class);
		job3.setOutputFormat(TextOutputFormat.class);
		FileInputFormat.setInputPaths(job3, new Path(newOutput + "/part-00000"));
		FileOutputFormat.setOutputPath(job3, new Path(newOutput + "_final"));

		JobClient.runJob(job3);

		long endTime = System.currentTimeMillis();

		System.out.println("Total Time Taken "+ Long.toString(endTime - startTime) + " milli seconds");

    }
}
