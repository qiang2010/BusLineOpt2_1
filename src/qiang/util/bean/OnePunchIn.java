package qiang.util.bean;

import qiang.bean.OneTrip;
import qiang.util.TimeFormatUtil;


/**
 * 一次打卡记录
 * ： 包含 打卡时间、打卡ic车站、Boolean是上车打卡还是下次打卡、Onetrip引用，具体是哪一个trip。
 * @author jq
 *
 */
public class OnePunchIn {

	
	long punchTime;
	int icStation;
	boolean onOrOff;
	OneTrip oneTirp;
	
	
	
	
	public void setTripRealStation(int mappedStation,LineStation mappedLineStation){
		// true 表示的上车
		if(onOrOff){
			this.oneTirp.setRealMarkStation(mappedStation);
			this.oneTirp.setRealMarkStationDetail(mappedLineStation);
		}else{
			this.oneTirp.setRealTredeStation(mappedStation);
			this.oneTirp.setRealTreadStationDetail(mappedLineStation);
		}
	}
	
	
	public OnePunchIn(long punchTime, int icStation, boolean onOrOff,
			OneTrip oneTirp) {
		super();
		this.punchTime = punchTime;
		this.icStation = icStation;
		this.onOrOff = onOrOff;
		this.oneTirp = oneTirp;
	}
	public long getPunchTime() {
		return punchTime;
	}
	public void setPunchTime(long punchTime) {
		this.punchTime = punchTime;
	}
	public int getIcStation() {
		return icStation;
	}
	public void setIcStation(int icStation) {
		this.icStation = icStation;
	}
	public boolean isOnOrOff() {
		return onOrOff;
	}
	public void setOnOrOff(boolean onOrOff) {
		this.onOrOff = onOrOff;
	}
	public OneTrip getOneTirp() {
		return oneTirp;
	}
	public void setOneTirp(OneTrip oneTirp) {
		this.oneTirp = oneTirp;
	}
	@Override
	public String toString() {
		return "OnePunchIn [punchTime=" + TimeFormatUtil.changeTimeStampToString(punchTime) + ", icStation="
				+ icStation + ", onOrOff=" + onOrOff + "]";
	}
	
	public String toStringLine(){
		return TimeFormatUtil.changeTimeStampToString(punchTime) 
				+ "\t"+ icStation 
				+ "\t" + onOrOff ;
	}
	
}
