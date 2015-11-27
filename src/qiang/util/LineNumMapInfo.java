package qiang.util;

import java.util.HashMap;
import java.util.Map;

import qiang.util.bean.LineBean;


/**
 * 
 * 读取 ic卡中的lineno和实际的线路的对应关系
 * 
 * 1. 车站信息.txt 数据：
 * 1333	第一客运分公司	1343	十队	5311599517750956589	 专89	  4706638298751012656	专89(新龙城东站-清河)	4758960063881802362	邮政研究院	7	4	2123	116.3325225594	40.0576462514
 * 
 * 2. 所有IC线路清单.xls 中的数据为：
 * 24089	专89
 * 
 * 3. ic打卡数据格式：
 * 24089
 * 
 * 需要将车站信息中的“专89” 这样的通过2的xls中的对应关系，进行映射，方便处理ic卡信息
 * @author jq
 */
public class LineNumMapInfo {

	public static void main(String[] args) {
		
		Map<String,LineBean> ans = getLineIdMap();
		for(String a:ans.keySet()){
			System.out.println(a+"\t"+ans.get(a).getLineid());
		}
		System.out.println();
	}

	static String path = "F:\\公交线路数据\\icCardData\\";
	static String fileName = "车站映射关系.txt";
	// key : 专89  线路名称
	// value : LineBean 包含 lineno,起点和终点。
	public static Map<String,LineBean> getLineIdMap(){
		Map<String,LineBean> ans = new HashMap<String, LineBean>();
		
		FileUtil fileUtil = new FileUtil(path+fileName);
		String tempLine;
		String splits[];
		LineBean lineBean;
		fileUtil.readLine();
		// 1	00001	1	老山公交场站	四惠枢纽站
		while((tempLine = fileUtil.readLine())!= null){
			splits = tempLine.split("\\s+");
			if(ans.containsKey(splits[2])){
				ans.get(splits[2]).addOneLineId(splits[1]);
				System.out.println("duplicate  line: "+tempLine);
				continue;
			}
			lineBean = new LineBean(splits[1], splits[3], splits[4]);
			ans.put(splits[2], lineBean);
		}
		System.out.println("map size: "+ans.size());
		return ans;
	}
}
