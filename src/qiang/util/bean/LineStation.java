package qiang.util.bean;

/**
 *  一条线路上，一个站点的信息
 *   
 */
public class LineStation {

	String name;
//	int id;
	double lat;
	double lng;
	
	public LineStation(double lat, double lng) {
		super();
		//this.name = name;
//		this.id = id;
		this.lat = lat;
		this.lng = lng;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	public int getId() {
//		return id;
//	}
//	public void setId(int id) {
//		this.id = id;
//	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	
	
}
