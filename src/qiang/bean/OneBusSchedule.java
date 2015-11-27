package qiang.bean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import qiang.util.FileUtil;
import qiang.util.TimeFormatUtil;
import qiang.util.bean.SortComparators;


/**
 * 用于统计发车一次的上车情况
 * 比如  630路车，从保福寺，到xxx方向，也就是 0 方向，每个车站的上下车人数。
 * 还有所有属于该车次的ic打卡记录
 * 
 * @author jq
 *
 */
public class OneBusSchedule {
	String busId;
	int dir;  
	int countSum;
	long firstIcMarkTime;
	long lastIcTradTime;
	Map<Integer, Integer> stationUpIcCount = new TreeMap<Integer, Integer>();
	Map<Integer, Integer> stationOffIcCount = new TreeMap<Integer, Integer>();
	
	// 存放当前车次运送的ic卡信息记录
	List<OneTrip> thisBusScheduleTrips = new ArrayList<>();
	
	
	/**
	 * dir 从小站到大站 就是 0 方向。map的时候就是使用forward的。
	 * @param busId
	 * @param dir
	 */
	public OneBusSchedule(String busId,int dir){
		this.busId = busId;
		this.dir  = dir;
		countSum = 0;
		firstIcMarkTime  = Long.MAX_VALUE;
		lastIcTradTime   = Long.MIN_VALUE;
	}
	
	
	// 判断当前车次的所有的ic卡记录 ic station的趋势，是1 2 3 4 还是 8 7 3 2 降序的。 
	// 是为了方便有相同时间不同icstation的排序。
	// 通过记录相邻的数据之差，正数 和 负数个数来判断。
	public int tripIcStationTrend(){

		int size = thisBusScheduleTrips.size();
		if(size == 1) return 0;
		OneTrip first = thisBusScheduleTrips.get(0);
		OneTrip second;
		int positiveCount = 0 ;
		int negativeCount  = 0 ;
		int diff;
		for(int i =1; i < size; i++){
			second = thisBusScheduleTrips.get(i);
			diff = second.getMarkstation() - first.getMarkstation();
			if(diff > 0){
				positiveCount++;
			}else{
				if(diff < 0){
					negativeCount++;
				}
			}
			first = second;
		}
		if(positiveCount == 0 || negativeCount == 0) return 0;
		if(positiveCount > negativeCount){
			return 1;
		}
		if(positiveCount < negativeCount){
			return -1;
		}
		System.err.println("正数和负数相同。。。。。。怎么办。。。。");
		return 5;
	}
		
		
	
	/**
	 * 注意： 下面两个函数，都先排序 调用。
	 * 排序遇到的问题：
	 * 按时间排序，由于是按照分钟来的。。时间相同的，站点不同？？的怎么办？
	 * 
	 * 是为了将波峰，波谷消除掉。
	 * 
	 * @return
	 */
	static String tempScheLog = "F:\\公交线路数据\\处理结果\\logSche.txt";
	public boolean busSchedultTripsFilterByMarkTimeStaion(){
		this.thisBusScheduleTrips.sort(SortComparators.markTimeComparator);
		// 判断一下按照上车时间排序好以后的数据，上车站点是否是单调不减，或者单调不增的。
		
		int tread = tripIcStationTrend();
		
		Comparator<OneTrip> com = null;
		if(tread == 1){
			com = SortComparators.markTimeIcStationUpComparator;
		}else{
			if(tread == -1){
				com = SortComparators.markTimeIcStationDownComparator;
			}else{
				if(tread > 1){
					System.err.println("无法判断趋势");
					// return false;
				}
			}
		}
		
		if(com != null){
			this.thisBusScheduleTrips.sort(com);
		}
			
		boolean up = true;
		OneTrip first = thisBusScheduleTrips.get(0);
		OneTrip second = first,cur;
		int i ;
		for( i =1; i < thisBusScheduleTrips.size();i++){
			cur = thisBusScheduleTrips.get( i);
			if(cur.getMarkstation() < second.getMarkstation()){
				up = false;break;
			}
			second = cur;
		}
		boolean down = true;
		int j;
		for(j = 1;j<thisBusScheduleTrips.size();j++){
			cur = thisBusScheduleTrips.get(j);
			if(cur.getMarkstation() > second.getMarkstation()){
				down = false;break;
			}
			second = cur;
		}
		if(!down &&  !up){
			System.out.println( "不正确的车次分配");
			FileUtil tempF = new FileUtil(tempScheLog,true);
			tempF.writeLine("不正确的车次分配：");
			if(com == null){
				tempF.writeLine("正负相同");
			}
			for(OneTrip onetrip:thisBusScheduleTrips){
				tempF.writeLine(onetrip.toStringOneLine());
				System.out.println(onetrip.toStringOneLine());
			}
		}
		
		return true;
	}
	/**
	 * 由于trip
	 * @return
	 */
	public boolean busSchedultTripsFilterByTradeStation(){
		this.thisBusScheduleTrips.sort(SortComparators.tradeTimeComparator);
		
		
		
		
		
		return true;
	}
	
	public void setFirstIcMarkTime(long markTime){
		this.firstIcMarkTime = this.firstIcMarkTime < markTime ? this.firstIcMarkTime :markTime ;
	}
	
	public void setLastIcTradTime(long tradeTime){
		this.lastIcTradTime = this.lastIcTradTime < tradeTime ? tradeTime:this.lastIcTradTime;
	}
	
	public void increaseCount(){
		countSum++;
	}
	public void addOneTrip(OneTrip one){
		thisBusScheduleTrips.add(one);
		addOneUpOff(one.getMarkstation(), one.getTradestation());
	}
	
	public List<OneTrip> getThisBusScheduleTrips() {
		return thisBusScheduleTrips;
	}
 
	public void addOneUpOff(int markStation,int tradeStation){
		increaseCount();
		addOne(stationOffIcCount, tradeStation);
		addOne(stationUpIcCount, markStation);
	}
	private void addOne(Map<Integer, Integer> map,int station){
		if(map.containsKey(station)){
			map.put(station, map.get(station)+1);
		}else{
			map.put(station,1);
		}
	}
	
	
	public String getBusId() {
		return busId;
	}


	public void setBusId(String busId) {
		this.busId = busId;
	}


	public int getDir() {
		return dir;
	}


	public void setDir(int dir) {
		this.dir = dir;
	}


	public int getCountSum() {
		return countSum;
	}


	public void setCountSum(int countSum) {
		this.countSum = countSum;
	}


	public long getFirstIcMarkTime() {
		return firstIcMarkTime;
	}


	public long getLastIcTradTime() {
		return lastIcTradTime;
	}

 

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(busId+"\t");
		sb.append(dir+"\t");
		sb.append(countSum+"\t");
		sb.append(TimeFormatUtil.changeTimeStampToString(firstIcMarkTime)+"\t");
		sb.append(TimeFormatUtil.changeTimeStampToString(lastIcTradTime )+"\t");
		for(int s:stationUpIcCount.keySet()){
			sb.append(s+","+stationUpIcCount.get(s)+",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("\t");
		for(int s:stationOffIcCount.keySet()){
			sb.append(s+","+stationOffIcCount.get(s)+",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
 }
