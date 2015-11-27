package qiang.util.bean;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

 



/**
 * 
 * 这里包含一条线路的详细信息：
 * 
 * 记录： 站位信息表.xlsx 中每条线路的详细信息
 * 
 * @author jq
 *
 */
public class LineDetailBean {
	
	String lineName;
	String lineId;
	Set<String> lineIds = null;
	//boolean isCirle = false;
	/**
	 * dir 的用途：
	 * 值为false: 表示ic打卡记录由小到大，就是原来的映射顺序。
	 * 比如: 这样forward存放的就是下面的。
	 * id  icstation
	 * 1 	1 
	 * 2 	2 
	 * 3 	2
	 * 4 	3
	 * 
	 * 可是当
	 * id  icstation
	 * 1 	3 
	 * 2 	2 
	 * 3 	2
	 * 4 	1
	 * 4    1
	 * 3    2
	 * ....
	 * 上面这个情况的时候，我们仍然需要将由小到大的icstation，用forward的map来映射
	 * 由大到小的使用backword来映射。这个时候需要标记dir = true;
	 */
	boolean dir = false; 
	Map<Integer,List<Integer>>  forward ;
	Map<Integer,List<Integer>>  backward ;
	Map<Integer,LineStation>  forwardStations; // 每个station id对应一个站点的信息
	Map<Integer,LineStation>  backwardStations; 
	public LineDetailBean(String lineName,String lineId,Set<String> lineIds){
		this.lineId = lineId;
		this.lineName = lineName;
		this.lineIds = lineIds;
		forward = new TreeMap<>();
		backward = new TreeMap<>();
		forwardStations = new TreeMap<>();
		backwardStations = new TreeMap<>();
	}
	public LineDetailBean(String lineName,LineBean lineBean){
		this.lineId = lineBean.getLineid();
		this.lineName = lineName;
		this.lineIds = lineBean.getLineIds();
		forward = new TreeMap<>();
		backward = new TreeMap<>();
		forwardStations = new TreeMap<>();
		backwardStations = new TreeMap<>();
	}
	public LineStation getMappedLineStation(int lineStationId,int dir){
		if(dir == 0){
			
			return getForwardLineStationBase(lineStationId);
		}
		if(dir  == 1){
			
			return getBackwardLineStationBase(lineStationId);
		}
		return null;
	}
	LineStation getForwardLineStationBase(int lineStaionId){
		return getMappedLineStationBase(forwardStations, lineStaionId);
	}
	LineStation getBackwardLineStationBase(int lineStaionId){
		return getMappedLineStationBase(backwardStations, lineStaionId);
	}
	
	LineStation getMappedLineStationBase(Map<Integer,LineStation>  stationsMap,int lineStaionId){
		if(stationsMap.containsKey(lineStaionId)){
			return stationsMap.get(lineStaionId);
		}
		return null;
	}
	
	public void  changeDir(){
		//if(dir) return;
		Map<Integer,List<Integer>> temp = forward;
		forward = backward;
		backward = temp;
		dir = true;
		
		Map<Integer,LineStation>  tempStations = this.forwardStations;
		this.forwardStations = this.backwardStations;
		this.backwardStations = tempStations;
		
		
	}
	public void setDir(boolean dir ){
		this.dir = dir;
	}
	
	boolean mappedRateCount = false;
	int allStationsIdNum = 0;
	int allIcStationNum = 0;
	int oneToOneIcIdMapCount = 0;
	
	public int getOneToOneIcIdMapCount(){
		if(mappedRateCount == false){
			for(int a:forward.keySet()){
				if(forward.get(a).size()==1)oneToOneIcIdMapCount++;
				allStationsIdNum += forward.get(a).size();
			}
			for(int b:backward.keySet()){
				if(backward.get(b).size()==1)oneToOneIcIdMapCount++;
				allStationsIdNum += backward.get(b).size();
			}
			allIcStationNum += forward.size();
			allIcStationNum += backward.size();
			mappedRateCount = true;
		}
		return oneToOneIcIdMapCount;
		
	}
	
	
	
	
	public int getAllStationsIdNum() {
		if(mappedRateCount == false){
			getOneToOneIcIdMapCount();
		}
		return allStationsIdNum;
	}
	public int getAllIcStationNum() {
		if(mappedRateCount == false){
			getOneToOneIcIdMapCount();
		}
		return allIcStationNum;
	}
	public boolean isCircle(){
		return backward.isEmpty() || forward.isEmpty();
	}
	
	public List<Integer> getIcMappedIds(int icStation,int dir){
		if(dir == 0){
			
			return getForwardIcMappedIds(icStation);
		}
		if(dir  == 1){
			
			return getBackwardIcMappedIds(icStation);
		}
		return null;
	}
	
	
	public List<Integer> getForwardIcMappedIds(int icStation){
		return getMappedId(forward, icStation);
	}
	public List<Integer> getBackwardIcMappedIds(int icStation){
		return getMappedId(backward, icStation);
	}
	
	private List<Integer> getMappedId(Map<Integer,List<Integer>>  forOrBack,int station){
		
		if(forOrBack.containsKey(station)){
			return forOrBack.get(station);
		}
		return new LinkedList<Integer>();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(lineId+"\t");
		sb.append(lineName+"\t");
		sb.append("forward\t");
		mapToString(sb, forward);
		sb.append("backward\t");
		mapToString(sb, backward);
		return sb.toString();
	}
	void mapToString(StringBuilder sb,Map<Integer,List<Integer>> map){
		for(int a:map.keySet()){
			sb.append(a+",");
			for(int b:map.get(a)){
				sb.append(b+",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("\t");
		}
	}
	public boolean addForwardOnePieceInfo(OnePieceInfoOfLineFile onePiece){
		addForwardIcToStationMap(onePiece.getIcId(), onePiece.getStationId());
		addForwardStations(onePiece.getStationId(),onePiece.getStationName(), onePiece.getLat(), onePiece.getLng());
		return true;
	}
	
	public boolean addBackwardOnePieceInfo(OnePieceInfoOfLineFile onePiece){
		addBackwardIcToStationMap(onePiece.getIcId(), onePiece.getStationId());
		addBackwardStations(onePiece.getStationId(), onePiece.getStationName(),onePiece.getLat(), onePiece.getLng());
		return true;
	}
	public boolean addForwardIcToStationMap(int icStation,int stationId){
		return addIcToStationMap(forward, icStation, stationId);
	}
	public boolean addBackwardIcToStationMap(int icStation,int stationId){
		return addIcToStationMap(backward, icStation, stationId);
	}
	private boolean addIcToStationMap(Map<Integer,List<Integer>>  map ,int icStation,int stationId ){
		if(map.containsKey(icStation)){
			map.get(icStation).add(stationId);
		}else{
			map.put(icStation, new LinkedList<Integer>());
			map.get(icStation).add(stationId);
		}
		return true;
	}
	
	public boolean addForwardStations(int stationId,String stationName,double lat,double lng){
		return addOneStation(forwardStations, stationName,stationId, lat, lng);
	}
	public boolean addBackwardStations(int stationId,String stationName,double lat,double lng){
		return addOneStation(backwardStations, stationName,stationId, lat, lng);
	}
	public boolean addOneStation(Map<Integer,LineStation>  stations,String stationName,int stationId,double lat,double lng){
		LineStation temp = new LineStation(lat, lng);
		temp.setName(stationName);
		stations.put(stationId, temp);
		return true;
	}
	
	
}
