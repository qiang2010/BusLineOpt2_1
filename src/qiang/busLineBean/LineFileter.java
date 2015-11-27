package qiang.busLineBean;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import qiang.util.FileUtil;


/**
 * 主要用于将3 4 5 6 7 8 9号的数据中共同出现的lineno过滤出来
 * 
 * 由于我们已经将大的数据集合分割了，所以可以根据文件夹下的文件名称（也就是lineno）判断。
 * @author jq
 *
 */
public class LineFileter {
	
	
	public static void main(String[] args) {
		filter();
	}

	static Set<String> intersection;
	static Set<String> diff;
	
	
	static String path = "F:\\公交线路数据\\处理结果\\";
	static int day = 20150803;
	static String ansFile = "给定一周线路编号交集";
	
	static{
		filter();
	}
	
	public static Set<String> filter(){
		Set<String> first = new TreeSet<String>();
		Set<String> firstTmp ;
		Set<String> cur = new TreeSet<String>();
		Set<String> remove = new TreeSet<String>();
		File f = new File(path+day);
		String []fileNames;
		if(f.exists() && f.isDirectory()){
			fileNames = f.list();
			System.out.println();
			for(String name:fileNames){
				first.add(name);//name.substring(0, name.length()-4)
			}
		}
		firstTmp = first;
		// 获取下一天。如果数据天数发生改变这里，需要重写
		day++;
		int dayTH = 20150807;
		for(int i = day;i < dayTH+1;i++ ){
			f = new File(path+i);
			if(f.exists() && f.isDirectory()){
				fileNames = f.list();
				for(String name:fileNames){
					if(first.contains(name)){
						cur.add(name);
					}else{
						remove.add(name);
					}
				}
				first = cur;
				cur = new TreeSet<String>();
			}
		}
		for(String t:firstTmp){
			if(!first.contains(t)){
				remove.add(t);
			}
		}
		intersection = first;
		diff = remove;
		System.out.println(first.size());
		System.out.println(remove.size());
		
//		FileUtil fileUtil = new FileUtil(path+ansFile+"\\"+"intersection5.txt");
//		for(String t:first){
//			fileUtil.writeLine(t.substring(0, t.length()-4));
//		}
//		fileUtil = new FileUtil(path+ansFile+"\\"+"differenceSet5.txt");
//		for(String t:remove){
//			fileUtil.writeLine(t.substring(0, t.length()-4));
//		}
		return first;
	}
	public static Set<String> getIntersection() {
		return intersection;
	}
	public static void setIntersection(Set<String> intersection) {
		LineFileter.intersection = intersection;
	}
	public static Set<String> getDiff() {
		return diff;
	}
	public static void setDiff(Set<String> diff) {
		LineFileter.diff = diff;
	}
	
    
	
}
