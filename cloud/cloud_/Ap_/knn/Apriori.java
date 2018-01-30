package apriori;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;







public class Apriori extends Configured implements Tool{

    public static class AprioriPass1Mapper extends Mapper<Object,Text,Text,IntWritable>{
        private final static IntWritable one = new IntWritable(1);
        private Text number = new Text();

        //第一次pass的Mapper只要把每个item映射为1
        public void map(Object key,Text value,Context context) throws IOException,InterruptedException{

            String[] ids = value.toString().split("[\\s\\t]+");
            for(int i = 0;i < ids.length;i++){
                context.write(new Text(ids[i]),one);
            }
        }
    }

    public static class AprioriReducer extends Reducer<Text,IntWritable,Text,IntWritable>{
        private IntWritable result = new IntWritable();

        //所有Pass的job共用一个reducer，即统计一种itemset的个数，并筛选除大于s的
        public void reduce(Text key,Iterable<IntWritable> values,Context context) throws IOException,InterruptedException{
            int sum = 0;

            int minSup = context.getConfiguration().getInt("minSup",5);
            for(IntWritable val : values){
                sum += val.get();
            }
            result.set(sum);

            if(sum > minSup){
                context.write(key,result);
            }
        }
    }

    public static class AprioriPassKMapper extends Mapper<Object,Text,Text,IntWritable>{
        private final static IntWritable one = new IntWritable(1);
        private Text item = new Text();

        private List< List<Integer> > prevItemsets = new ArrayList< List<Integer> >();
        private List< List<Integer> > candidateItemsets = new ArrayList< List<Integer> >();
        private Map<String,Boolean> candidateItemsetsMap = new HashMap<String,Boolean>();


        //第一个以后的pass使用该Mapper，在map函数执行前会执行setup来从k-1次pass的输出中构建候选itemsets,对应于apriori算法
        @Override
        public void setup(Context context) throws IOException, InterruptedException{
            int passNum = context.getConfiguration().getInt("passNum",2);
            String prefix = context.getConfiguration().get("hdfsOutputDirPrefix","");
            String lastPass1 = context.getConfiguration().get("fs.default.name") + "/user/hadoop/chess-" + (passNum - 1) + "/part-r-00000";
            String lastPass = context.getConfiguration().get("fs.default.name") + prefix + (passNum - 1) + "/part-r-00000";

            try{
                Path path = new Path(lastPass);
                FileSystem fs = FileSystem.get(context.getConfiguration());
                BufferedReader fis = new BufferedReader(new InputStreamReader(fs.open(path)));
                String line = null;

                while((line = fis.readLine()) != null){
                    
                    List<Integer> itemset = new ArrayList<Integer>();

                    String itemsStr = line.split("[\\s\\t]+")[0];
                    for(String itemStr : itemsStr.split(",")){
                        itemset.add(Integer.parseInt(itemStr));
                    }

                    prevItemsets.add(itemset);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            //get candidate itemsets from the prev itemsets
            candidateItemsets = getCandidateItemsets(prevItemsets,passNum - 1);
        }


        public void map(Object key,Text value,Context context) throws IOException,InterruptedException{
            String[] ids = value.toString().split("[\\s\\t]+");

            List<Integer> itemset = new ArrayList<Integer>();
            for(String id : ids){ 
                itemset.add(Integer.parseInt(id));
            }

            //遍历所有候选集合
            for(List<Integer> candidateItemset : candidateItemsets){
                //如果输入的一行中包含该候选集合，则映射1，这样来统计候选集合被包括的次数 
                //子集合，消耗掉了大部分时间
                if(contains(candidateItemset,itemset)){
                    String outputKey = "";
                    for(int i = 0;i < candidateItemset.size();i++){
                        outputKey += candidateItemset.get(i) + ",";
                    }
                    outputKey = outputKey.substring(0,outputKey.length() - 1);
                    context.write(new Text(outputKey),one);
                }
            }
        }

        //返回items是否是allItems的子集
        private boolean contains(List<Integer> items,List<Integer> allItems){
            
            int i = 0;
            int j = 0;
            while(i < items.size() && j < allItems.size()){
                if(allItems.get(j) > items.get(i)){
                    return false;
                }else if(allItems.get(j) == items.get(i)){
                    j++;
                    i++;
                }else{
                    j++;
                }    
            }

            if(i != items.size()){
                return false;
            }
            return true;
        }

        //获取所有候选集合，参考apriori算法
        private List< List<Integer> > getCandidateItemsets(List< List<Integer> > prevItemsets, int passNum){

            List< List<Integer> > candidateItemsets = new ArrayList<List<Integer> >();
            
            //上次pass的输出中选取连个itemset构造大小为k + 1的候选集合
            for(int i = 0;i < prevItemsets.size();i++){
                for(int j = i + 1;j < prevItemsets.size();j++){
                    List<Integer> outerItems = prevItemsets.get(i);
                    List<Integer> innerItems = prevItemsets.get(j);

                    List<Integer> newItems = null;
                    if(passNum == 1){
                        newItems = new ArrayList<Integer>();
                        newItems.add(outerItems.get(0));        
                        newItems.add(innerItems.get(0));        
                    }
                    else{    
                        int nDifferent = 0;
                        int index = -1;
                        for(int k = 0; k < passNum && nDifferent < 2;k++){
                            if(!innerItems.contains(outerItems.get(k))){
                                nDifferent++;
                                index = k;
                            }
                        }

                        if(nDifferent == 1){
                            //System.out.println("inner " + innerItems + " outer : " + outerItems);
                            newItems = new ArrayList<Integer>();
                            newItems.addAll(innerItems);
                            newItems.add(outerItems.get(index));
                        }
                    }
                    if(newItems == null){continue;}

                    Collections.sort(newItems);

                    //候选集合必须满足所有的子集都在上次pass的输出中，调用isCandidate进行检测，通过后加入到候选子集和列表
                    if(isCandidate(newItems,prevItemsets) && !candidateItemsets.contains(newItems)){
                        candidateItemsets.add(newItems);    
                        //System.out.println(newItems);
                    }
                }
            }

            return candidateItemsets;
        }

        private boolean isCandidate(List<Integer> newItems,List< List<Integer> > prevItemsets){
        
            List<List<Integer>> subsets = getSubsets(newItems);     
            
            for(List<Integer> subset : subsets){
                if(!prevItemsets.contains(subset)){
                    return false;
                }
            }

            return true;
        }

        private List<List<Integer>> getSubsets(List<Integer> items){
        
            List<List<Integer>> subsets = new ArrayList<List<Integer>>();
            for(int i = 0;i < items.size();i++){
                List<Integer> subset = new ArrayList<Integer>(items);
                subset.remove(i);
                subsets.add(subset);
            }

            return subsets;
        }
    }
    
    public static int s;
    public static int k;

    public int run(String[] args)throws IOException,InterruptedException,ClassNotFoundException{
        long startTime = System.currentTimeMillis();

        String hdfsInputDir = args[0];        //从参数1中读取输入数据
        String hdfsOutputDirPrefix = args[1];    //参数2为输出数据前缀，和第pass次组成输出目录
        s = Integer.parseInt(args[2]);        //阈值
        k = Integer.parseInt(args[3]);        //k次pass
        
        //循环执行K次pass
        for(int pass = 1; pass <= k;pass++){
            long passStartTime = System.currentTimeMillis();
            
            //配置执行该job
            if(!runPassKMRJob(hdfsInputDir,hdfsOutputDirPrefix,pass)){
                return -1;    
            }
        
            long passEndTime = System.currentTimeMillis();
            System.out.println("pass " + pass + " time : " + (passEndTime - passStartTime));
        }

        long endTime = System.currentTimeMillis();
        System.out.println("total time : " + (endTime - startTime));

        return 0;
    }

    private static boolean runPassKMRJob(String hdfsInputDir,String hdfsOutputDirPrefix,int passNum)
            throws IOException,InterruptedException,ClassNotFoundException{

            Configuration passNumMRConf = new Configuration();
            passNumMRConf.setInt("passNum",passNum);
            passNumMRConf.set("hdfsOutputDirPrefix",hdfsOutputDirPrefix);
            passNumMRConf.setInt("minSup",s);

            Job passNumMRJob = new Job(passNumMRConf,"" + passNum);
            passNumMRJob.setJarByClass(Apriori.class);
            if(passNum == 1){
                //第一次pass的Mapper类特殊对待，不许要构造候选itemsets
                passNumMRJob.setMapperClass(AprioriPass1Mapper.class);
            }
            else{
                //第一次之后的pass的Mapper类特殊对待，不许要构造候选itemsets
                passNumMRJob.setMapperClass(AprioriPassKMapper.class);
            }
            passNumMRJob.setReducerClass(AprioriReducer.class);
            passNumMRJob.setOutputKeyClass(Text.class);
            passNumMRJob.setOutputValueClass(IntWritable.class);

            FileInputFormat.addInputPath(passNumMRJob,new Path(hdfsInputDir));
            FileOutputFormat.setOutputPath(passNumMRJob,new Path(hdfsOutputDirPrefix + passNum));

            return passNumMRJob.waitForCompletion(true);
    }

    public static void main(String[] args) throws Exception{
        int exitCode = ToolRunner.run(new Apriori(),args);
        System.exit(exitCode);
    }
}