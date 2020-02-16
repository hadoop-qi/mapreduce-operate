package com.zhiyou100.secondarysort12;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class WordCount3Partitioner extends Partitioner<Text, Text> {

	@Override
	public int getPartition(Text key, Text value, int numPartitions) {
		
		String line = key.toString();
		String[] words = line.split("---");
		
		return (words[0].hashCode() & Integer.MAX_VALUE) % numPartitions;
	}
}
