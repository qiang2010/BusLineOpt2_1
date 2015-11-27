package qiang.util.bean;

import java.util.HashSet;
import java.util.Set;

/**
 *  车站对应表中的map 使用的
 * @author jq
 *
 */
public class LineBean {
	String lineid;
	Set<String> lineIds;
	String startStation;
	String endStation;
	
	public LineBean(String lineid, String startStation, String endStation) {
		super();
		lineIds = new HashSet<String>();
		lineIds.add(lineid);
		this.lineid = lineid;
		this.startStation = startStation;
		this.endStation = endStation;
	}
	
	public Boolean addOneLineId(String lineId){
		lineIds.add(lineId);
		return true;
	}
	
	
	
	public Set<String> getLineIds() {
		return lineIds;
	}

	public void setLineIds(Set<String> lineIds) {
		this.lineIds = lineIds;
	}

	public String getLineid() {
		return lineid;
	}
	public void setLineid(String lineid) {
		this.lineid = lineid;
	}
	public String getStartStation() {
		return startStation;
	}
	public void setStartStation(String startStation) {
		this.startStation = startStation;
	}
	public String getEndStation() {
		return endStation;
	}
	public void setEndStation(String endStation) {
		this.endStation = endStation;
	}
	
	
}
