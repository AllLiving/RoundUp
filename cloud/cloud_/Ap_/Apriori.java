package myapriori;

import java.io.*;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class AprioriMapReduce extends Configured implements Tool {

    public static class Map extends MapReduceBase implements
            Mapper<LongWritable, Text, Text, IntWritable> {

        public List<List<String>> nextrecords = null;
        public ArrayList<String> allitems = new ArrayList<String>();

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        private static String count = null;

        public void configure(JobConf job) {

            String record = job.get("map.record.file");
            String isDirectory = job.get("map.record.isDirectory");
            count = job.get("map.record.isDirectory");

            if(!isDirectory.equals("true")){
                nextrecords = Assitance.getNextRecord(record, isDirectory);
            }


            if(nextrecords.isEmpty()||nextrecords.size()==0){
                List<String> finish = new ArrayList<String>();
                finish.add("null");
                nextrecords.add(finish);
            }

        }

        @Override
        public void map(LongWritable key, Text value,
                OutputCollector<Text, IntWritable> output, Reporter report)
                throws IOException {
            // lines of text;
            String line = value.toString().toLowerCase();
            int tcount = line.indexOf("\t");
            if(tcount >= 0){
                line = line.substring(tcount,line.length()).trim().replaceAll("\t", "").toLowerCase();
            }

            // comma 
            String[] dd = line.split(",");
            if(!count.equals("false")){
                for(String sd : dd){
                    List<String> dstr = new ArrayList<String>();
                    dstr.add(sd);
                    word = new Text(dstr.toString());
                    output.collect(word, one);
                }
            }
            else{
                List<String> dstr = new ArrayList<String>();
                for(String ss: dd){
                    dstr.add(ss);
                }

                for(int i = 0 ; i< nextrecords.size();i++){
                    if(dstr.containsAll(nextrecords.get(i))){
                        word = new Text(nextrecords.get(i).toString());
                        output.collect(word, one);
                    }
                }
            }
        }

    }

    // public static class Combine extends MapReduceBase implements
    //         Reducer<Text, IntWritable, Text, IntWritable> {

    //     @Override
    //     public void reduce(Text key, Iterator<IntWritable> values,
    //             OutputCollector<Text, IntWritable> output, Reporter report)
    //             throws IOException {
    //         int sum = 0;
    //         while (values.hasNext()) {
    //             sum += values.next().get();
    //         }

    //         output.collect(key, new IntWritable(sum));

    //     }

    // }

    public static class Reduce extends MapReduceBase implements
            Reducer<Text, IntWritable, Text, IntWritable> {
        private static int minnum = 0;

        @Override
        public void reduce(Text key, Iterator<IntWritable> values,
                OutputCollector<Text, IntWritable> output, Reporter report)
                throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();
            }
            if (sum >= minnum) {
                output.collect(key, new IntWritable(sum));
            }

        }

        public void configure(JobConf job) {
            System.out.println(minnum);
            minnum = Integer.parseInt(job.get("map.record.supportnum"));
        }

    }

    @Override
    public int run(String[] args) throws Exception {
        
        Configuration conf = new Configuration();
		
		Job job = new Job(conf,"apriori");
		job.setJarByClass(AprioriMapReduce.class);
		
		job.setCombinerClass(Combine.class);
		job.setReducerClass(Reduce.class);
		
		job.setMapperClass(Map.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true)?0:1);

        // JobConf conf = new JobConf(getConf(), AprioriMapReduce.class);
        // conf.setJobName("apriori");

        // conf.setMapperClass(Map.class);
        // conf.setMapOutputKeyClass(Text.class);
        // conf.setMapOutputValueClass(IntWritable.class);

        // conf.setCombinerClass(Reduce.class);

        // conf.setCombinerClass(Combine.class);

        // conf.setReducerClass(Reduce.class);
        // conf.setOutputKeyClass(Text.class);
        // conf.setOutputValueClass(Text.class);

        // conf.setInputFormat(TextInputFormat.class);
        // conf.setOutputFormat(TextOutputFormat.class);

        // FileInputFormat.setInputPaths(conf, new Path(args[0]));
        // FileOutputFormat.setOutputPath(conf, new Path(args[1]));
        // conf.set("map.items.file", args[0]);
        // conf.set("map.record.file", args[5]);
        // conf.set("map.record.supportnum", args[3]);
        // conf.set("map.record.isDirectory", args[4]);

        // JobClient.runJob(conf);
        return 0;
    }

    // args[0] is the path for input;
	// args[1] is the path of output;
	// args[2] is the name of output file;
	// args[3] is the 
    public static void main(String[] args) throws Exception {
        // 获取记录条数
        int res = 0;
        /*
         * int itemsnum = 0; itemsnum = Utils.CountItemsNum(args[0],
         * args[args.length - 1]); if (itemsnum < 1) {
         * System.out.println("输入的文件有误！"); System.exit(res); } // 获取最小的支持总数
         * Float minnum = itemsnum * Float.parseFloat(args[args.length - 2]);
         */
        if(args.length<4){
            System.err.println("please ensure the args length is no less 4!");
            System.exit(res);
        }

        int count = 0;
        boolean target = true;
        String lastarg[] = new String[args.length+1];
        for (int i = 0; i < args.length; i++) {
            lastarg[i] = args[i];
        }
        while (target) {
            // 执行第一遍的mapreduce
            if (count == 0) {
                lastarg[args.length] = args[0];
            }
            else
                lastarg[args.length] = args[2]+"/num"+count+"frequeceitems.data";

            count++;
            res = ToolRunner.run(new Configuration(), new AprioriMapReduce(),
                    lastarg);
            target = Assitance.SaveNextRecords(args[1], args[2], count);

            lastarg[4] = "false";

        }
        System.exit(res);
    }

}