import java.io.IOException;  
import java.util.Iterator;  
import java.util.StringTokenizer;
import java.util.Arrays;
import java.io.*;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.io.LongWritable; 
import org.apache.hadoop.conf.Configuration;  
import org.apache.hadoop.fs.Path;  
import org.apache.hadoop.io.IntWritable; 
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class IndexInverse {
	
	//映射
	public static class Map 
		extends Mapper<Object,Text,Text,Text>
	{
		private Text key_=new Text();//Text/key + fileName
		private Text value_=new Text();//Value=1 time
		
		public void map(Object key, Text value,
				Mapper<Object, Text, Text, Text>.Context context)
				throws IOException,InterruptedException
		{
			//分割字符串
			StringTokenizer itr=new StringTokenizer(value.toString());
			//得到文件信息
			FileSplit inputSplit=(FileSplit)context.getInputSplit();
			String fileName=inputSplit.getPath().getName();
			
			value_.set("1");
			//获取所有字符串信息
			while(itr.hasMoreTokens()){
				key_.set(itr.nextToken()+" "+fileName);
				
				//压入key和value
				context.write(key_,value_);
			}
		}
		
	}

	//合并数据集
	public static class Combiner 
		extends Reducer<Text,Text,Text,Text>
	{
		private Text key_ = new Text();//key
		private Text value_ = new Text();//<fileName,times>
		
		public void reduce(Text key, Iterable<Text> values,
				Reducer<Text,Text,Text,Text>.Context context)
				throws IOException, InterruptedException
		{
			//分割得到内容字符串和fileName字符串
			String[]splitString=key.toString().split(" ");
			
			//统计总数
			int sum=0;
			for(Text t:values)sum+=Integer.parseInt(t.toString());
			
			//记录key和对应的<fileName,times>
			key_.set(splitString[0]);
			value_.set("<"+splitString[1]+","+sum+">");
			
			//压入
			context.write(key_,value_);
		}
	}
	
	
	public static class Reduce 
		extends Reducer<Text, Text, Text, Text>
	{
		private Text value = new Text();
		
		public void reduce(Text key, Iterable<Text> values, 
				Reducer<Text, Text, Text, Text>.Context context)
				throws IOException,InterruptedException
		{
			//整合相同词
			String sum="";
			for(Text t:values)sum+=t.toString()+" ";
			
			value.set(sum);
			context.write(key,value);
		}
	}
	public static void main(String[] args) throws Exception  
    {  
        Configuration conf = new Configuration();
		
		Job job = new Job(conf,"IndexInverse");
		job.setJarByClass(IndexInverse.class);
		
		job.setCombinerClass(Combiner.class);
		job.setReducerClass(Reduce.class);
		
		job.setMapperClass(Map.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true)?0:1);
    }
	
}


