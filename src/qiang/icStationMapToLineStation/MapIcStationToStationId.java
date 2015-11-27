package qiang.icStationMapToLineStation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import qiang.bean.OneBusSchedule;
import qiang.bean.OneTrip;
import qiang.icCardDataPartitaionAndMap.AnalysisOneBusLine;
import qiang.util.FileUtil;
import qiang.util.bean.LineDetailBean;
import qiang.util.bean.OnePunchIn;
import qiang.util.bean.PunchInComparators;

/**
 * 
 * 传入两个参数
 * 一个是ic 和id的一对多映射关系
 * 一个是一趟车次的所有ic卡信息。
 * （只是对上车时间聚类，然后分配站点 ）
 * @author jq
 *
 */

public class MapIcStationToStationId {
	
 
	// Key : line id
	// value 是详细信息。
	static Map<String,LineDetailBean>  detail =GetLineDetail.loadStationDetail();
	static long timeInterThreshold = 30*60;
	static String icMapDifferFileName = "F:\\公交线路数据\\映射结果\\+"+AnalysisOneBusLine.day+"+ic映射为空differ大于15.txt";
	static LineDetailBean curLineDetail;
	static int dir ;
	public static void  mapUpOffIcStationToStationId(OneBusSchedule oneSche,String lineId){
		// 当前车次的所有ic卡信息
		if(!detail.containsKey(lineId)){
			
			System.out.println("没有line id 对应的车站信息，无法完成映射："+lineId);
			return;
		}
		
		curLineDetail = detail.get(lineId);

		// 如果是0，那么使用forward，否则使用backward映射。
		dir = oneSche.getDir();
		// 得到按照站点升序，站点相同的按照时间升序的顺序排列。
		ArrayList<OnePunchIn> punchList = sortPunchInList(oneSche.getThisBusScheduleTrips(), PunchInComparators.punchInStationUpTimeUpComparator);
		int begin =0,end=0;
		OnePunchIn beginPu ;
		int size =punchList.size(); 
		List<Integer> mappedStations;
		int icSta;
		//boolean needFilter = false;
		LinkedList<OnePunchIn> tempPunchList;
		for(end=0;end<size && begin<size;){
			beginPu = punchList.get(begin);
			icSta = beginPu.getIcStation();
			tempPunchList = new LinkedList<OnePunchIn>();
			tempPunchList.add(beginPu);
			end = begin+1;
			while(end<size && punchList.get(end).getIcStation() == icSta){
				tempPunchList.add(punchList.get(end));
				end++;
			}
			// begin 和end 找到。
			int differ = 1;
			mappedStations = curLineDetail.getIcMappedIds(icSta, dir);
			//mappedLineStations = curLineDetail.getMappedLineStation(icSta,dir);
			while(mappedStations == null || mappedStations.size()== 0){
				mappedStations = curLineDetail.getIcMappedIds(icSta+differ, dir);
				if(mappedStations == null || mappedStations.size()== 0){
					mappedStations = curLineDetail.getIcMappedIds(icSta-differ, dir);
				}
				differ++;
				if(differ>5){
					break;
				}
			}
			if(differ >5 ){
				System.out.println(icSta+"\t"+dir+"需要映射的集合为空。");
				if(AnalysisOneBusLine.writeOpen){
					FileUtil ff = new FileUtil("F:\\公交线路数据\\映射结果\\"+AnalysisOneBusLine.day+"ic映射为空differ大于5.txt",true);
					ff.writeLine(lineId+"\t"+oneSche.toString());
				}
				//System.out.println(lineId+oneSche.toString());
				//continue;
			}else{
				// 过滤
				//System.out.println("filter");
				filterIcPunchs(tempPunchList);
			
				// 映射
				map(tempPunchList,mappedStations);
			}
			
			begin = end;
			//end++;
		}
//		if(needFilter){
//			for(OnePunchIn one : punchList){
//				System.err.println(one.toStringLine()+"\t"+one.getOneTirp().toStringOneLine());
//			}
//		}
	}
	
	static void map(LinkedList<OnePunchIn> filterPunchResult,List<Integer> mappedStations){
		
		int currentIcCount = filterPunchResult.size();
		int mappedSize = mappedStations.size();
		// 过滤后的剩余的ic卡记录数据如果为1。
		if(currentIcCount == 1){
			if(mappedSize == 1){
				int ms = mappedStations.get(0);
				for(int j = 0;j<currentIcCount;j++){
					filterPunchResult.get(j).setTripRealStation(ms,curLineDetail.getMappedLineStation(ms, dir));
				}
			}else{
				// 这条数据，需要根据上下文映射。这里我们首先随机选择一个。
				int choose =(int)( Math.random()*mappedSize);
				filterPunchResult.get(0).setTripRealStation(mappedStations.get(choose),curLineDetail.getMappedLineStation(mappedStations.get(choose), dir));
			}
		}else{// 过滤后，ic卡的数量仍然大于1
			// 过滤过错误数据后,开始分堆，聚类
			if(mappedSize == 1){
				int ms = mappedStations.get(0);
				for(int j = 0;j<currentIcCount;j++){
					filterPunchResult.get(j).setTripRealStation(ms,curLineDetail.getMappedLineStation(ms, dir));
				}
			}else{ // 这里就是大于1了。
				int cultersNum = Integer.MAX_VALUE;
				// 使用并查集 来合并。
				int []findSet = new int[currentIcCount];
				//初始化并查集
				int findSetSize = 0;
				for(int k = 0 ; k < currentIcCount;){
					findSet[k] = -1;
					findSetSize++;
					int bk = k+1;
					while(bk < currentIcCount && filterPunchResult.get(bk).getIcStation() == filterPunchResult.get(k).getIcStation() ){
						findSet[k]--;
						findSet[bk] = k;
						bk++;
					}
					k = bk;
				}
				 //并查集的数量大于 需要映射的。需要合并并查集,直到大小相同。
				while(mappedSize < findSetSize){
					
					long min = Long.MAX_VALUE;
					int minIndex= -1;
					// 记录相邻并查集的差。 使用集合内的
					LinkedList<Long> findSetTime= new LinkedList<>();
					for(int setIndex = 0 ; setIndex < findSet.length; setIndex++){ // currentIcCount
						if(findSet[setIndex] < 0){
							findSetTime.add(filterPunchResult.get(setIndex).getPunchTime());
						}
					}	
					
					long curTime = findSetTime.get(0);
					for(int j = 1 ; j<findSetTime.size();j++){
						if(Math.abs(findSetTime.get(j)-curTime) < min ){
							min = Math.abs(findSetTime.get(j)-curTime);
							minIndex = j-1;
						}
						curTime = findSetTime.get(j);
					}
					// 合并minIndex 以及minIndex+1 的两个并查集。
					int begin,end;
					int tempC=0;
					for(int setIndex = 0 ; setIndex < findSet.length; setIndex++){ // currentIcCount
						if(findSet[setIndex] < 0){
							if(tempC == minIndex)break;
							tempC++;
						}
					}	
					begin = tempC;
					while(begin<findSet.length && findSet[begin] > -1){
						begin++;
					}
					end = begin+1;
					while(end<findSet.length && findSet[end] > -1){
						end++;
					}
					
					// begin end 之间是要合并到tempC中的。
					for(int j = begin;j <end&&j<findSet.length;j++){
						findSet[j] = tempC;
						findSet[tempC]--;
					}
					findSetSize--;
				}
				
				// 并查集的大小刚好和mappedSize相同。
				if(mappedSize == findSetSize){
					int mapIndex = 0 ; 
					for(int setI = 0 ; setI<findSet.length;setI++){
						if(findSet[setI] < 0){
							findSet[setI] = mappedStations.get(mapIndex++);
							filterPunchResult.get(setI).setTripRealStation(findSet[setI],curLineDetail.getMappedLineStation(findSet[setI], dir));
						}else{
							filterPunchResult.get(setI).setTripRealStation(findSet[findSet[setI]],curLineDetail.getMappedLineStation(findSet[findSet[setI]], dir));
						}
					}
				}else{
					// 大于并查集的大小。需要选择映射。
					if(mappedSize > findSetSize){
						boolean []choose = mapChoose(mappedSize, findSetSize);
						if(choose == null){
							//break;
						}
						int mapIndex = 0 ; 
						for(int setI = 0 ; setI<findSet.length;setI++){
							if(findSet[setI] < 0){
								while(choose[mapIndex]==false)mapIndex++;
								findSet[setI] = mappedStations.get(mapIndex++);
								filterPunchResult.get(setI).setTripRealStation(findSet[setI],curLineDetail.getMappedLineStation(findSet[setI], dir));
							}else{
								filterPunchResult.get(setI).setTripRealStation(findSet[findSet[setI]],curLineDetail.getMappedLineStation(findSet[findSet[setI]], dir));
							}
						}
					} 
				}
			}
		}
	}
	// 并查集小
	static boolean []mapChoose(int mappedSize,int findSetSize){
		boolean []ans  = new boolean [mappedSize];
		int m = 0;
		int ge;
		int count =0;
		while( m < findSetSize){
			ge = (int)(Math.random()*mappedSize);
			if(!ans[ge]){
				ans[ge] = true;
				m++;
			}
			count++;
			if(count > 10000){
				System.err.println("随机数问题");
				return null;
						 
			}
		}
		return ans;
	}
			 
	
	/**
	 * 将begin 和end之间的punch 异常trip标记isValid为false
	 * @param punchList
	 * @param begin 
	 * @param end
	 * @return
	 */
	static boolean filterIcPunchs(LinkedList<OnePunchIn> punchList){
		// 相同的计价站点，如果时间过长，要被删掉。
		// 首先计算相邻tirp的时间差。
		int icCount = punchList.size();
		if(icCount < 3) return false;
		boolean f = false;
		OnePunchIn first = punchList.get(0);
		OnePunchIn second = punchList.get(1);
		if(Math.abs(second.getPunchTime() - first.getPunchTime()) >timeInterThreshold ){
			punchList.get(0).getOneTirp().setValid(false);
			punchList.remove(0);
			f = true;
		}
		first = punchList.get(punchList.size()-2);
		second = punchList.get(punchList.size()-1);
		
		if(Math.abs(second.getPunchTime() - first.getPunchTime()) >timeInterThreshold ){
			punchList.get(punchList.size()-1).getOneTirp().setValid(false);
			punchList.remove(punchList.size()-1);
			return true;
		}
		return f;
		
//		long []timeInter = new long[icCount-1];
//		boolean needFilt= false;//
//		OnePunchIn cur = punchList.get(0);
//		for(int j = 1;j<icCount-1;j++){
//			timeInter[j] = punchList.get(j).getPunchTime()-cur.getPunchTime();
//			cur = punchList.get(j);
//			if(timeInter[j] > timeInterThreshold){
//				needFilt = true;
//			}
//		}
////		/int setSize = timeInter.length;
//		int currentIcCount = icCount;
//		if(needFilt){
//			// 
//			System.err.println("存在时间差大于"+timeInterThreshold +"的数据");
//			// 如果是第一个或者最后一个存在这个情况，直接将第一个和最后一个删除。设置onetrip为false
////			for(OnePunchIn one : punchList){
////				System.err.println(one.toStringLine());
////			}
//			
//			
//			return true;
//		}
					
	}
	
	
	
	
	
	
	
	/**
	 * 将给定的 一个trip list，返回该list的上车和下车打卡的所有时间的list
	 * @param tempBusTrips
	 * @return
	 */
	public static ArrayList<OnePunchIn> sortPunchInList(List<OneTrip> tempBusTrips,Comparator<OnePunchIn> com){
		ArrayList<OnePunchIn> oneSchedultPunchIn  = new ArrayList<>();
		OnePunchIn up,down;
		for(OneTrip one:tempBusTrips){
			up = new OnePunchIn(one.getMarkTime(), one.getMarkstation(), true, one);
			down = new OnePunchIn(one.getTradeTime(), one.getTradestation(), false, one);
			oneSchedultPunchIn.add(up);
			oneSchedultPunchIn.add(down);
		}
		oneSchedultPunchIn.sort(com);
//		FileUtil fileU = new FileUtil("F:\\公交线路数据\\处理结果\\IC打卡上下车合并\\punch.txt",true);
//		fileU.writeLine(key);
//		for(OnePunchIn pun:oneSchedultPunchIn){
//			fileU.writeLine(pun.toStringLine());
//			//System.out.println(pun.toString());
//		}
		return oneSchedultPunchIn;
	}
	
	
	
}
