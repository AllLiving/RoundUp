
//package cn.kepu.littlefu;
 
import java.io.IOException;
 
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
 
import java.util.Iterator;
import org.apache.hadoop.mapred.Reducer;
/*
public static class InverseIndexMapper extends MapReduceBase 
					implements Mapper <Object, Text, Text, Text> {
   
	 String inputFile;
   
	 public void configure(JobConf job) {
		String inputFileFull = job.get("map.input.file");
		inputFile =inputFileFull.substring(inputFileFull.lastIndexOf("/")+1);
	  }
   
	 @Override 
	 public void map(Object key, Text value,OutputCollector<Text, Text> output, Reporter reporter) 
					throws IOException{
 
		String line = value.toString();
		String[] word = line.split(" ");
	   
		for(String s : word){
			//output<word:doc1, 1>
			output.collect(new Text(s+":"+inputFile), new Text("1"));
		}
	   
	}
}

public static class InverseIndexCombiner extends MapReduceBase implements
			Reducer<Text, Text, Text, Text> {
	@Override
	public void reduce(Text key, Iterator<Text>values,
			OutputCollector<Text,Text> output, Reporter reporter)
			throws IOException {
		//total
		int sum =0;
		while(values.hasNext()){
			sum+= Integer.parseInt(values.next().toString());
		}
		//outputposition
		int pos =key.toString().indexOf(":");
		//output<word,doc1:1>
		Text outKey = new Text(key.toString().subSequence(0, pos).toString());
		Text outValue = new Text(key.toString().substring(pos+1).toString()+":"+sum);
		System.out.print("combiner:<key:" + outKey.toString() + 
						",value:" + outValue.toString() + ">");
		output.collect(outKey, outValue);
	}
}

public static class InverseIndexReducer extends MapReduceBase implements
		Reducer<Text,Text, Text, Text> {
 
	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text,Text> output, Reporter reporter)
			throws IOException {
	   
		String fileList = new String();
		while(values.hasNext()){
			fileList+= values.next().toString()+";";
		}
		//output<word,doc1:1;doc2:2;doc3:1;>
		output.collect(key,new Text(fileList));
	}
}*/

public class InverseIndex {
	
    public static void main(String[] args) throws IOException{
   
        if(args.length != 2){
            System.err.println("Usage :InverseIndex <input path> <output path>");
            System.exit(-1);
        }
       
        JobConf conf = new JobConf(InverseIndex.class);
        conf.setJobName("InverseIndex");
       
        FileInputFormat.addInputPath(conf,new Path(args[0]));
        FileOutputFormat.setOutputPath(conf,new Path(args[1]));
       
        conf.setMapperClass(InverseIndexMapper.class);
        conf.setCombinerClass(InverseIndexCombiner.class);
        conf.setReducerClass(InverseIndexReducer.class);
       
        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(Text.class);
       
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
       
        JobClient.runJob(conf);
    }
}

/*
public class InverseIndex{
	// 实现映射的数据结构
	public static class TokenizerMapper
		extends Mapper<Object, Text, Text, IntWritable>{
		
		// 算筹单元
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		
		// 重载函数 map
		protected void map(Object key, Text value, Context context)
			throws IOException, InterruptedException{
			StringTokenizer itr = new StringTokenizer(value.toString());
			while(itr.hasMoreTokens()){
				word.set(itr.nextToken());
				// context 作为返回值
				context.write(word, one);
			}
		}
	}


	// 实现计数的功能函数
	public static class IntSumReducer
		// 继承 Reducer 的基类
		extends Reducer<Text, IntWritable, Text, IntWritable>{
		private IntWritable result = new IntWritable();
		
		// 重载函数 reduce
		public void reduce(Text key, Iterator<IntWritable> values,
							Context context)
			throws IOException, InterruptedException{
			int sum = 0;
			for(IntWritable val : values){
				sum += val.get();
			}
			result.set(sum);
			// context 作为返回值
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception{
		// 配置信息
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if(otherArgs.length != 2){
			System.err.println("Usage: Wordcount <int> <out>");
			System.exit(2);
		}
		Job job = new Job(conf, "word count");
		job.setJarByClass(WordCount.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutoputKeyClass(Text.class);
		job.setOutoputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutoputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true)?0:1);
	}
}
	*/

