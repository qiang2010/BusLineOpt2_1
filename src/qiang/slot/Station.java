package qiang.slot;

import java.util.ArrayList;
import java.util.Map;

/**
 *  Station 类记录的是一个车站的详细信息，包括车站的位置
 *  名称，id等，但是不包括经过的公交车信息。
 * @author jq
 *
 */
public class Station {

	String stationId;
	Map<String,Integer>  linesPass; // 经过该站点的线路集合
	ArrayList<Integer> countOn;
	ArrayList<Integer> countOff;
	//int countOn;
 //	int countOff;
	String stationName;
	double lng;
	double lat;
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(lng+"\t");
		sb.append(lat+"\t");
		sb.append("on\t");
		for(int i =0;i<countOn.size();i++){
			sb.append(countOn.get(i)+"\t");
		}
		//sb.deleteCharAt(sb.length()-1);
		sb.append("off\t");
		for(int i =0;i<countOff.size()	;i++){
			sb.append(countOff.get(i)+"\t");
		}
		
		//sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public Map<String, Integer> getLinesPass() {
		return linesPass;
	}

	public void setLinesPass(Map<String, Integer> linesPass) {
		this.linesPass = linesPass;
	}

	public ArrayList<Integer> getCountOn() {
		return countOn;
	}



	public void setCountOn(ArrayList<Integer> countOn) {
		this.countOn = countOn;
	}



	public ArrayList<Integer> getCountOff() {
		return countOff;
	}



	public void setCountOff(ArrayList<Integer> countOff) {
		this.countOff = countOff;
	}

	public Station(String stationName,double lng,double lat){
		this.stationName = stationName;
		this.lng = lng;
		this.lat = lat;
		this.countOff = new ArrayList<Integer> ();
		this.countOn = new ArrayList<Integer> ();
		int s = DayPartationCount.getTimeSeg().length-1;
		countOn.clear();
		countOff.clear();
		for(int i =0; i<s;i++){
			this.countOff.add(0);
			this.countOn.add(0);
		}
	}
	
 

	public void increaseCountOn(int slotNum,int on) {
		this.countOn.set(slotNum,this.countOn.get(slotNum)+on) ;
	}

	public void increaseCountOff(int slotNum,int off) {
		this.countOff.set(slotNum, this.countOff.get(slotNum)+off);
	}

	
	public String getStationId() {
		return stationId;
	}
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public double getlng() {
		return lng;
	}
	public void setlng(double lng) {
		this.lng = lng;
	}
	public double getlat() {
		return lat;
	}
	public void setlat(double lat) {
		this.lat = lat;
	}
	
	
}
