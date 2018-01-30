package cn.kepu.littlefu;
 
import java.io.IOException;
import java.util.Iterator;
 
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
 
public class InverseIndexCombiner extends MapReduceBase implements
Reducer<Text, Text, Text, Text>{
 
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
        System.out.print("combiner:<key:"+outKey.toString()+",value:"+outValue.toString()+">");
        output.collect(outKey, outValue);
    }
 
}
 