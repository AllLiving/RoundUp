package cn.kepu.littlefu;
 
import java.io.IOException;
 
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
 
 
@SuppressWarnings("deprecation")
public class InverseIndexMapper extends MapReduceBase implements Mapper<Object, Text, Text, Text> {
   
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
            output.collect(newText(s+":"+inputFile), new Text("1"));
        }
       
    }
}