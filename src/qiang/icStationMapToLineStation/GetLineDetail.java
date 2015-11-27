package qiang.icStationMapToLineStation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import qiang.util.FileUtil;
import qiang.util.LineNumMapInfo;
import qiang.util.bean.LineBean;
import qiang.util.bean.LineDetailBean;
import qiang.util.bean.OnePieceInfoOfLineFile;

/*
 * 获取线路 的各个车站的信息，包括
 * ic站点 和 线路站点的映射
 * line id 和line name的映射
 * 各个站点的GPS
 * 
 */
public class GetLineDetail {
	public static void main(String[] args) {
		staticsLineDetail();
	}
	/**
	 * 根据读取的线路的各个站点映射信息，统计一下ic 站点和 线路站点一一对应的比例。
	 */
	public static void staticsLineDetail(){
		
		Map<String,LineDetailBean>  detail = loadStationDetail();
		System.out.println(detail.size());
//		//FileUtil ff = new FileUtil("F:\\公交线路数据\\映射结果\\ic站点和station的映射关系表.txt");
//		for(String key:detail.keySet()){
//			//System.out.println(key+"\t"+detail.get(key).toString());
//			//ff.writeLine(key+"\t"+detail.get(key).toString());
//		}
		System.out.println("站点信息表中的站点数量："+allStationsNameSet909.size());
		System.out.println("站点信息表中映射到ic线路清单站点数量："+mappedStationsNameSet764.size());
	}
	
	
	
	static String icCardDataPath = "F:\\公交线路数据\\icCardData\\";
	static String stationFile = "车站信息.txt";
	static Map<String,LineDetailBean> allLines = new TreeMap<>();
	static HashMap<String,String> allStationsNameSet909 = new HashMap<>();
	static HashMap<String,String> mappedStationsNameSet764 = new HashMap<>();
	
	
	
	public static  Map<String,LineDetailBean> loadStationDetail(){
		
		FileUtil file = new FileUtil(icCardDataPath+stationFile);
		String tempLine;
		String []splits;
		LineDetailBean tempLineDetail;
		int base;
		LineBean lineBean;
		String lastLineName=null;
		String curLineName;
		List<OnePieceInfoOfLineFile> oneLinePieces = new ArrayList<>(); 
		OnePieceInfoOfLineFile tempPiece;
		int oneToOneCount = 0;
		int allStationsCount = 0;
		int allIcCount = 0;
		int lineCount=0;
		countMapped=0;
		// 首先将一个线路的所有站点信息都读入。再判断该线路，两个方向的站点分布。
		while((tempLine = file.readLine())!= null){
			splits = tempLine.trim().split("\\s+");
			base  = 0;
			if(splits.length < 13){
				base = 4;
			}
			if(splits.length <11){
				System.out.println(tempLine);
			}
			
			curLineName = splits[5-base].trim();
			if(lastLineName == null ){
				lastLineName = curLineName;
			}
			tempPiece = new OnePieceInfoOfLineFile(Integer.parseInt(splits[10-base]), Integer.parseInt(splits[11-base]), Double.parseDouble(splits[13-base]), Double.parseDouble(splits[14-base]));
			tempPiece.setStationName(splits[9-base]);
			// 所有的站点名称
			allStationsNameSet909.put(splits[9-base],splits[13-base]+"\t"+splits[14-base]);
			// 仍然是同一条线路
			if(curLineName.equals(lastLineName)){
				oneLinePieces.add(tempPiece);
				
			}else{ // 一条新的路线
				// 处理读取的一条线路的所有数据
				// lastName 就是当前道路的名称
				lineCount++;
				lineBean = lineNameMapToId(lastLineName);
				if(lineBean!=null){
					tempLineDetail = new LineDetailBean(lastLineName, lineBean);
					//将两个方向的站点区分开。\
					// 由于站点的 变化可能是1 2 3 4 5 1 2 3 4 5 6
					// 还可能是 6 5 4 3 2  1 6 5 4 3 2 1
					// 采用的方法： 首先获取第二个减去第一个的差，为1或者-1，否则这个线路就是有问题的。 
					// 然后在第一个的基础上加上这个差，就是下一个，在加上就是下下个。直到不相等。
					//需要注意的事情是： icstation有可能是大到小， 
					OnePieceInfoOfLineFile first = oneLinePieces.get(0);
					int firstIC = first.getIcId();
					int lastFirstIc=0;
					OnePieceInfoOfLineFile second = oneLinePieces.get(1);
					int diff = second.getStationId() - first.getStationId();
					tempLineDetail.addForwardOnePieceInfo(first);
					tempLineDetail.addForwardOnePieceInfo(second);
					mappedStationsNameSet764.put(first.getStationName(),first.getLat()+"\t"+first.getLng());
					mappedStationsNameSet764.put(second.getStationName(),second.getLat()+"\t"+second.getLng());
					int i;
					for( i = 2;i<oneLinePieces.size();i++){
						first = second;
						second = oneLinePieces.get(i);
						mappedStationsNameSet764.put(second.getStationName(),second.getLat()+"\t"+second.getLng());
						if(second.getStationId() == first.getStationId()+diff){
							tempLineDetail.addForwardOnePieceInfo(second);
							lastFirstIc = second.getIcId();
						}else{
							tempLineDetail.addBackwardOnePieceInfo(second);
							break;
						}
					}
					i++;
					// 
					for( ;i<oneLinePieces.size();i++){
						tempLineDetail.addBackwardOnePieceInfo(oneLinePieces.get(i));
						mappedStationsNameSet764.put(oneLinePieces.get(i).getStationName(),oneLinePieces.get(i).getLat()+"\t"+oneLinePieces.get(i).getLng());
					}
					oneToOneCount += tempLineDetail.getOneToOneIcIdMapCount();
					allStationsCount += tempLineDetail.getAllStationsIdNum();
					allIcCount += tempLineDetail.getAllIcStationNum();
					if(firstIC > lastFirstIc){ // 第二种情况。 14路车
						tempLineDetail.changeDir() ;
					}
					for(String id:lineBean.getLineIds())
						allLines.put(id, tempLineDetail);
					//System.out.println(tempLineDetail.toString());
				}else{
					System.out.println("null:\t"+lastLineName);
				}
				// 更新
				lastLineName = curLineName;
				oneLinePieces.clear();
				oneLinePieces.add(tempPiece); 
			}
		}
		System.out.println("oneToOneCount"+"\t"+"allStationsCount"+"\t"+"allIcCount");
		System.out.println("oneToOneCount:"+oneToOneCount+"\t"+allStationsCount+"\t"+allIcCount);
		System.out.println(oneToOneCount*1.0/allStationsCount);
		System.out.println("两个表对应上的线路\t"+countMapped);
		return allLines;
	}
	/**
	 * 给定line name ，返回映射关系
	 * 比如 专46 ==》 ？
	 * 
	 * 有些数据不存在，无法映射，这里如果无法映射，就在前面补上0，比如926线路，是没有对应的line id的，所以
	 * 
	 * @param lineName
	 * @return
	 */
	static Map<String,LineBean> lineNameToLineIdMap  = LineNumMapInfo.getLineIdMap();;
	static Set<String> lineNameMapFailed = new HashSet<>();
	static int countMapped = 0;
	static LineBean lineNameMapToId(String lineName){
		
		if(lineNameToLineIdMap.containsKey(lineName)){
			countMapped++;
			return lineNameToLineIdMap.get(lineName);
		}
//		lineNameMapFailed.add(lineName);
//		// 没有映射 比如 926 如果，名字很短我们在前面补上0，否则就lineName
//		int s = lineName.length();
//		if(s <4 && isNum(lineName) ){
//			StringBuilder id =new StringBuilder();
//			for(int i = 0 ; i < 5-s;i++){
//				id.append("0");
//			}
//			id.append(lineName);
//			LineBean tempB = new LineBean(id.toString(), "", "");
//			return tempB;
//		}
//		return new LineBean(lineName, "", "");
			
		return null;
	}
	static boolean isNum(String lineName){
		try {
			Integer.parseInt(lineName);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public static Map<String, LineDetailBean> getAllLines() {
		return allLines;
	}
	public static void setAllLines(Map<String, LineDetailBean> allLines) {
		GetLineDetail.allLines = allLines;
	}
	public static HashMap<String, String> getAllStationsNameSet909() {
		return allStationsNameSet909;
	}
	public static void setAllStationsNameSet909(
			HashMap<String, String> allStationsNameSet909) {
		GetLineDetail.allStationsNameSet909 = allStationsNameSet909;
	}
	public static HashMap<String, String> getMappedStationsNameSet764() {
		return mappedStationsNameSet764;
	}
	public static void setMappedStationsNameSet764(
			HashMap<String, String> mappedStationsNameSet764) {
		GetLineDetail.mappedStationsNameSet764 = mappedStationsNameSet764;
	}
    
	
}
