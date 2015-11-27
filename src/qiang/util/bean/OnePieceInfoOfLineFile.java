package qiang.util.bean;


/**
 * 
 * 为了方便统计，本类的一个对象表示： 站位信息表中的一条信息。
 * 
 * 1333	第一客运分公司	1335	一队	5191735326844542021	315	5344298811497204630	315(德胜门-新都东站)	5320166861921505710	德胜门	1	1	2110	116.3732469895	39.9479715054
 * 
 * @author jq
 * 
 *
 */
public class OnePieceInfoOfLineFile {
	int stationId;
	int icId;
	double lat;
	double lng;
	String stationName = null;
	public OnePieceInfoOfLineFile(int stationId, int icId, double lat,
			double lng) {
		super();
		this.stationId = stationId;
		this.icId = icId;
		this.lat = lat;
		this.lng = lng;
	}
	
	
	
	public String getStationName() {
		return stationName;
	}



	public void setStationName(String stationName) {
		this.stationName = stationName;
	}



	public int getStationId() {
		return stationId;
	}
	public int getIcId() {
		return icId;
	}
	public double getLat() {
		return lat;
	}
	public double getLng() {
		return lng;
	}
	
}
