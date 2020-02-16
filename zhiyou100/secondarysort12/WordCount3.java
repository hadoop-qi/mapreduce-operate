package com.zhiyou100.secondarysort12;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount3 {

	public static void main(String[] args) {
		
		try {
			
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS", "hdfs://master:9000");

			Job job = Job.getInstance(conf, "wordcount2");
			job.setJarByClass(WordCount3.class);
			
			job.setMapperClass(WordCount3Mapper.class);
			job.setReducerClass(WordCount3Reducer.class);
			
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			
			job.setPartitionerClass(WordCount3Partitioner.class);
			job.setSortComparatorClass(WordCount3SortComparator.class);
			job.setGroupingComparatorClass(WordCount3GroupingComparator.class);
			
			job.setNumReduceTasks(2);
			
			job.setInputFormatClass(KeyValueTextInputFormat.class);
			Path inputPath = new Path("/WutheringHeights-Count/part-r-00000");
			FileInputFormat.addInputPath(job, inputPath);
			
			Path outputDir = new Path("/WutheringHeights-Count-2");
			FileSystem.get(conf).delete(outputDir, true);
			FileOutputFormat.setOutputPath(job, outputDir);

			boolean flag = job.waitForCompletion(true);
			
			System.exit(flag ? 0 : 1);
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

	
	public static class WordCount3Mapper extends Mapper<Text, Text, Text, Text> {
		
		private Text outputKey = new Text();
		
		@Override
		protected void map(Text key, Text value, Mapper<Text, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			
			String line = value.toString();
			
			String[] words = line.split("---");
			
			// key：单词---次数
			// value：文件---次数
			outputKey.set(key.toString() + "---" + words[1]);
			
			context.write(outputKey, value);
		}
	}
	
	public static class WordCount3Reducer extends Reducer<Text, Text, Text, Text> {
		
		private Text outputKey = new Text();
		private Text outputValue = new Text();

		@Override
		protected void reduce(Text key, Iterable<Text> value,
				Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
			
			StringBuilder valueText = new StringBuilder();
			
			for (Text text : value) {
				
				valueText.append(text.toString().replaceAll("---", "-->"));
				valueText.append("\t");
			}
			
			outputKey.set(key.toString().split("---")[0]);
			outputValue.set(valueText.toString());
			
			context.write(outputKey, outputValue);
		}
	}
}
