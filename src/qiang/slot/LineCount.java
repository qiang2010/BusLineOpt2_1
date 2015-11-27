package qiang.slot;

import java.util.Map;
import java.util.TreeMap;

import qiang.bean.OneTrip;
import qiang.util.TimeFormatUtil;

public class LineCount {

	int countSum;
	long firstIcMarkTime;
	long lastIcTradTime;
	Map<Integer, Integer> stationUpIcCount = new TreeMap<Integer, Integer>();
	Map<Integer, Integer> stationOffIcCount = new TreeMap<Integer, Integer>();
	public LineCount(){
		countSum = 0;
		firstIcMarkTime  = Long.MAX_VALUE;
		lastIcTradTime   = Long.MIN_VALUE;
	}
	public int getUpCount(int station){
		if(stationUpIcCount.containsKey(station)){
			return stationUpIcCount.get(station);
		}
		return 0;
	}
	public int getOffCount(int station){
		if(stationOffIcCount.containsKey(station)){
			return stationOffIcCount.get(station);
		}
		return 0;
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
		addOneUpOff(one.getMarkstation(), one.getTradestation());
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

	public String toString(){
		StringBuilder sb = new StringBuilder();
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
