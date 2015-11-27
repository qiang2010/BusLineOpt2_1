package qiang.bean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import qiang.util.TimeFormatUtil;
import qiang.util.bean.LineStation;

public class OneTrip {

	String cardId;
	String cardType;
	String tradeType;
	
	long tradeTime;
	long markTime;
	
	int tradestation;
	int realTredeStation;
	int markstation;
	int realMarkStation;
	String lineId;
	String busId;
	String markLineId;
	String markBusId;
	int ppd;
	
	LineStation realMarkStationDetail;
	LineStation realTreadStationDetail;
	
	// 标记该ic记录是否是有效的。
	boolean isValid ;

	public OneTrip(){
		this.isValid = true;
		
	}
	
	public double [] getMarkStaionGPSLat_Lng(){
		if(realMarkStationDetail == null)return null;
		double [] gps= new double [2];
		gps[0] = realMarkStationDetail.getLat();
		gps[1] = realMarkStationDetail.getLng();
		return gps;
	}
	public String getRealMarkStationGPSLat_LngStringTab(){
		if(realMarkStationDetail == null) return "";
		return realMarkStationDetail.getLat()+"\t"+realMarkStationDetail.getLng();
	}
	
	public String getRealTradeStationGPSLat_LngStringTab(){
		if(realTreadStationDetail == null) return "";
		return realTreadStationDetail.getLat()+"\t"+realTreadStationDetail.getLng();
	}
	
	public double [] getTradeStationGPSLat_Lng(){
		double []gps = new double[2];
		gps[0] = realTreadStationDetail.getLat();
		gps[1] = realTreadStationDetail.getLng();
		return gps;
	}
 
	public LineStation getRealMarkStationDetail() {
		return realMarkStationDetail;
	}



	public void setRealMarkStationDetail(LineStation realMarkStationDetail) {
		this.realMarkStationDetail = realMarkStationDetail;
	}



	public LineStation getRealTreadStationDetail() {
		return realTreadStationDetail;
	}



	public void setRealTreadStationDetail(LineStation realTreadStationDetail) {
		this.realTreadStationDetail = realTreadStationDetail;
	}



	public boolean isValid() {
		return isValid;
	}



	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}



	public static OneTrip tripFactory(String line){
		String []split = line.split("\",\"");
		if(split.length < 11) return null;
		try {
			// && split[8].equals(split[10])
			if(split[2].equals("06") && split[7].equals(split[9]) )
				return new OneTrip(split[0].substring(1),
					split[1],split[2],split[3],split[4],Integer.parseInt(split[5]),Integer.parseInt(split[6]),split[7],split[8],split[9],split[10],Integer.parseInt(split[11]));
			return null;
		} catch (Exception e) {
			//System.out.println("exception!");
			return null;
		}
	}
 
 private OneTrip(String cardId, String cardType, String tradeType,
			String tradeTime, String markTime, int tradestation, int markstation,
			String lineId, String busId, String markLineId, String markBusId,
			int ppd) throws Exception {
		super();
		this.isValid = true;
		this.cardId = cardId;
		this.cardType = cardType;
		this.tradeType = tradeType;
		this.tradeTime =changeStringToTimestamp(tradeTime);
		this.markTime = changeStringToTimestamp(markTime);
		this.realMarkStationDetail = null;
		this.realTreadStationDetail = null;
		if(this.markTime > this.tradeTime) throw new Exception();
		if(this.tradeTime - this.markTime > 4*60*60){
		//	System.out.println( tradeTime + " " + markTime );
			throw new Exception();
		}
		if(this.tradeTime < this.markTime){
			throw new Exception();
		}
		if(this.tradeTime - this.markTime < 40){
			throw new Exception();
		}
		if(markstation == tradestation){
			throw new Exception();
		}
		// 数据文件出错，这里需要交换两个station的位置
		this.tradestation = markstation;
		this.markstation = tradestation;
		this.lineId = lineId;
		this.busId = busId;
		this.markLineId = markLineId;
		this.markBusId = markBusId;
		this.ppd = ppd;
		this.realMarkStation = -1;
		this.realTredeStation = -1;
		this.realMarkStationDetail = null;
		this.realTreadStationDetail = null;
	}

 
 
  OneTrip(String cardId, String cardType, String tradeType,
			long tradeTime, long markTime, int tradestation, int markstation,
			String lineId, String busId, String markLineId, String markBusId,
			int ppd) {
		super();
		this.cardId = cardId;
		this.isValid = true;
		this.cardType = cardType;
		this.tradeType = tradeType;
		this.tradeTime =tradeTime;
		this.markTime = markTime;
		this.tradestation = markstation;
		this.markstation = tradestation;
		this.lineId = lineId;
		this.busId = busId;
		this.markLineId = markLineId;
		this.markBusId = markBusId;
		this.ppd = ppd;
		this.realMarkStation = -1;
		this.realTredeStation = -1;
	}

	
	public String getCardId() {
	return cardId;
}

public void setCardId(String cardId) {
	this.cardId = cardId;
}

public String getCardType() {
	return cardType;
}


public int getRealTredeStation() {
	return realTredeStation;
}

public void setRealTredeStation(int realTredeStation) {
	this.realTredeStation = realTredeStation;
}

public int getRealMarkStation() {
	return realMarkStation;
}

public void setRealMarkStation(int realMarkStation) {
	this.realMarkStation = realMarkStation;
}

public void setCardType(String cardType) {
	this.cardType = cardType;
}

public String getTradeType() {
	return tradeType;
}

public void setTradeType(String tradeType) {
	this.tradeType = tradeType;
}

public long getTradeTime() {
	return tradeTime;
}

public void setTradeTime(long tradeTime) {
	this.tradeTime = tradeTime;
}

public long getMarkTime() {
	return markTime;
}

public void setMarkTime(long markTime) {
	this.markTime = markTime;
}

public int getTradestation() {
	return tradestation;
}

public void setTradestation(int tradestation) {
	this.tradestation = tradestation;
}

public int getMarkstation() {
	return markstation;
}

public void setMarkstation(int markstation) {
	this.markstation = markstation;
}

public String getLineId() {
	return lineId;
}

public void setLineId(String lineId) {
	this.lineId = lineId;
}

public String getBusId() {
	return busId;
}

public void setBusId(String busId) {
	this.busId = busId;
}

public String getMarkLineId() {
	return markLineId;
}

public void setMarkLineId(String markLineId) {
	this.markLineId = markLineId;
}

public String getMarkBusId() {
	return markBusId;
}

public void setMarkBusId(String markBusId) {
	this.markBusId = markBusId;
}

public int getPpd() {
	return ppd;
}

public void setPpd(int ppd) {
	this.ppd = ppd;
}

public static SimpleDateFormat getFormat() {
	return format;
}

public static void setFormat(SimpleDateFormat format) {
	OneTrip.format = format;
}

public static Calendar getCal() {
	return cal;
}

public static void setCal(Calendar cal) {
	OneTrip.cal = cal;
}


	static SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	static Calendar cal = Calendar.getInstance();
	public static long changeStringToTimestamp (String time)throws Exception{
			Date date = format.parse(time);
			cal.setTime(date);
			
			//cal.set(Calendar.SECOND, 0);
			return cal.getTimeInMillis()/1000;
	}


 
	
 

	@Override
	public String toString() {
		return "OneTrip [cardId=" + cardId + ", cardType=" + cardType
				+ ", tradeType=" + tradeType + ", tradeTime=" + tradeTime
				+ ", markTime=" + markTime + ", tradestation=" + tradestation
				+ ", realTredeStation=" + realTredeStation + ", markstation="
				+ markstation + ", realMarkStation=" + realMarkStation
				+ ", lineId=" + lineId + ", busId=" + busId + ", markLineId="
				+ markLineId + ", markBusId=" + markBusId + ", ppd=" + ppd
				+ ", realMarkStationDetail=" + getRealMarkStationGPSLat_LngStringTab()
				+ ", realTreadStationDetail=" + getRealTradeStationGPSLat_LngStringTab()
				+ ", isValid=" + isValid + "]";
	}

	public String toStringOneLine() {
		return cardId + "\t" + cardType
				+ "\t" + tradeType 
				+ "\t" + TimeFormatUtil.changeTimeStampToString( markTime)
				+ "\t" + TimeFormatUtil.changeTimeStampToString(tradeTime) 
				+ "\t" + markstation 
				+ "\t" + realMarkStation 
				+ "\t" + realMarkStationDetail.getName()
				+ "\t" + getRealMarkStationGPSLat_LngStringTab()
 				+ "\t" + tradestation
 				+ "\t" + realTredeStation
 				+ "\t" + realTreadStationDetail.getName()
 				+ "\t" + getRealTradeStationGPSLat_LngStringTab()
 				+ "\t" + lineId
				+ "\t" + busId 
				+ "\t" + markLineId
				+ "\t" + markBusId 
				+ "\t" + ppd;
	}
}
