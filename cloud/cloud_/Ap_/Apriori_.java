package myapriori;

import java.io.*;
import java.util.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.LineReader;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class AprioriMapReduce extends Configured implements Tool {
	public static class Assitance {

		public static List<List<String>> getNextRecord(String nextrecord,
				String isDirectory) {
			boolean isdy = false ;
			if(isDirectory.equals("true")){
				isdy = true;
			}
			List<List<String>> result = new ArrayList<List<String>>();

			try {
				Path path = new Path(nextrecord);

				Configuration conf = new Configuration();

				FileSystem fileSystem = path.getFileSystem(conf);

				if (isdy) {
					FileStatus[] listFile = fileSystem.listStatus(path);
					for (int i = 0; i < listFile.length; i++) {
						result.addAll(getNextRecord(listFile[i].getPath()
								.toString(), "false"));
					}
					return result;
				}

				FSDataInputStream fsis = fileSystem.open(path);
				LineReader lineReader = new LineReader(fsis, conf);

				Text line = new Text();
				while (lineReader.readLine(line) > 0) {
					List<String> tempList = new ArrayList<String>();
					// ArrayList<Double> tempList = textToArray(line);

					String[] fields = line.toString()
							.substring(0, line.toString().indexOf("]"))
							.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\t", "").split(",");
					for (int i = 0; i < fields.length; i++) {
						tempList.add(fields[i].trim());
					}
					Collections.sort(tempList);
					result.add(tempList);

				}
				lineReader.close();
				result = connectRecord(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			return result;
		}

		private static List<List<String>> connectRecord(List<List<String>> result) {

			List<List<String>> nextCandidateItemset = new ArrayList<List<String>>();
			for (int i = 0; i < result.size()-1; i++) {

				HashSet<String> hsSet = new HashSet<String>();
				HashSet<String> hsSettemp = new HashSet<String>();
				for (int k = 0; k < result.get(i).size(); k++)
					// 获得频繁集第i行
					hsSet.add(result.get(i).get(k));
				int hsLength_before = hsSet.size();// 添加前长度
				hsSettemp = (HashSet<String>) hsSet.clone();
				for (int h = i + 1; h < result.size(); h++) {// 频繁集第i行与第j行(j>i)连接
																// 每次添加且添加一个元素组成
																// 新的频繁项集的某一行，
					hsSet = (HashSet<String>) hsSettemp.clone();// ！！！做连接的hasSet保持不变
					for (int j = 0; j < result.get(h).size(); j++)
						hsSet.add(result.get(h).get(j));
					int hsLength_after = hsSet.size();
					if (hsLength_before + 1 == hsLength_after
							&& isnotHave(hsSet, nextCandidateItemset)
							&& isSubSet(hsSet, result)) {
						// 如果不相等，表示添加了1个新的元素，再判断其是否为record某一行的子集 若是则其为 候选集中的一项
						Iterator<String> itr = hsSet.iterator();
						List<String> tempList = new ArrayList<String>();
						while (itr.hasNext()) {
							String Item = (String) itr.next();
							tempList.add(Item);
						}
						Collections.sort(tempList);
						nextCandidateItemset.add(tempList);
					}

				}

			}
			return nextCandidateItemset;
		}

		private static boolean isSubSet(HashSet<String> hsSet,
				List<List<String>> result) {
			// hsSet转换成List

			List<String> tempList = new ArrayList<String>();

			Iterator<String> itr = hsSet.iterator();
			while (itr.hasNext()) {
				String Item = (String) itr.next();
				tempList.add(Item);
			}
			Collections.sort(tempList); 
			List<List<String>> sublist = new ArrayList<List<String>>();

			for(int i = 0; i < tempList.size(); i++){
				List<String> temp = new ArrayList<String>();
				for(int j = 0; j < tempList.size(); j++){
					temp.add(tempList.get(j));
				}
				temp.remove(temp.get(i));
				sublist.add(temp);

			}
			if(result.containsAll(sublist)){
				return true;
			}

			/*for (int i = 1; i < result.size(); i++) {
				List<String> tempListRecord = new ArrayList<String>();
				for (int j = 1; j < result.get(i).size(); j++)
					tempListRecord.add(result.get(i).get(j));
				if (tempListRecord.containsAll(tempList))
					return true;
			}*/
			return true;
		}

		private static boolean isnotHave(HashSet<String> hsSet,
				List<List<String>> nextCandidateItemset) {
			List<String> tempList = new ArrayList<String>();
			Iterator<String> itr = hsSet.iterator();
			while (itr.hasNext()) {
				String Item = (String) itr.next();
				tempList.add(Item);
			}
			Collections.sort(tempList);
			if(nextCandidateItemset.contains(tempList)){
				return false;
			}
			return true;
		}

		// outfile是输出路径， savefile是输出的文件名
		public static boolean SaveNextRecords(String outfile, String savefile,int count) {
			//读输出文件,将符合条件的行放到hdfs,保存路径为savafile+count
			boolean finish = false;
			try {
				Configuration conf = new Configuration();


				Path rPath = new Path(savefile+"/num"+count+"frequeceitems.data");
				FileSystem rfs = rPath.getFileSystem(conf);            
				FSDataOutputStream out = rfs.create(rPath);

				Path path = new Path(outfile);          
				FileSystem fileSystem = path.getFileSystem(conf);
				FileStatus[] listFile = fileSystem.listStatus(path);
				for (int i = 0; i < listFile.length; i++){
					 FSDataInputStream in = fileSystem.open(listFile[i].getPath());
					 //FSDataInputStream in2 = fileSystem.open(listFile[i].getPath());
					 int byteRead = 0;
					 byte[] buffer = new byte[256];
					 while ((byteRead = in.read(buffer)) > 0) {
							out.write(buffer, 0, byteRead);
							finish = true;
						}
					 in.close();


				}
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//保存之后进行连接和剪枝
			try {
				deletePath(outfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}       
			return finish;
		}
		public static void deletePath(String pathStr) throws IOException{
			Configuration conf = new Configuration();
			Path path = new Path(pathStr);
			FileSystem hdfs = path.getFileSystem(conf);
			hdfs.delete(path ,true);

		}

	}
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
            String line = value.toString().toLowerCase();
            int tcount = line.indexOf("\t");
            if(tcount >= 0){
                line = line.substring(tcount,line.length()).trim().replaceAll("\t", "").toLowerCase();
            }
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

    public static class Combine extends MapReduceBase implements
            Reducer<Text, IntWritable, Text, IntWritable> {

        @Override
        public void reduce(Text key, Iterator<IntWritable> values,
                OutputCollector<Text, IntWritable> output, Reporter report)
                throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();
            }

            output.collect(key, new IntWritable(sum));

        }

    }

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
        JobConf conf = new JobConf(getConf(), AprioriMapReduce.class);
        conf.setJobName("apriori");

        conf.setMapperClass(Map.class);
        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(IntWritable.class);

        // conf.setCombinerClass(Reduce.class);

        conf.setCombinerClass(Combine.class);

        conf.setReducerClass(Reduce.class);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        // conf.setInputFormat(TextInputFormat.class);
        // conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));
        conf.set("map.items.file", args[0]);
        conf.set("map.record.file", args[5]);
        conf.set("map.record.supportnum", args[3]);
        conf.set("map.record.isDirectory", args[4]);

        JobClient.runJob(conf);
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