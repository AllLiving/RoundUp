package myapriori;

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

public class Assitance {

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