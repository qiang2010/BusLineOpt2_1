package qiang.slot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import qiang.util.FileUtil;

/**
 * 这里就是对一天中，分时段统计各个车站的上车人数，以及下车人数。
 * @author jq
 *
 */
public class DayPartationCount {


	public static void main(String[] args) {
		int day = 20150803;
		for(;day<20150810;day++){
			DayPartationCount sd = new DayPartationCount();
			sd.countOneDay(""+day);	
		}
	}
	String day;
	// 将一天进行划分，统计每个时间段内的各个线路，车站的人流量
	ArrayList<SlotIcDetail> allSlots = null;
	Set<String> lineFilterSet = new HashSet<>(); // 只考虑这些线路
	public void countOneDay(String day){
		this.day = day;
		//lineFilterSet.add("630");
		//lineFilterSet.add("005");
		
		allSlots = new ArrayList<>();
		initSlots();
		
		// 然后遍历一遍数据集合，做相应统计
		staticsLineDetail(day);
		Map<String,Station>  stationCount = loadStationDetailCorLine();
		
		// 输出统计的数据
		writeToFileTabSpace();

		

//		writeToFileInJsonFormat();
	}
	String ansPath = "F:\\公交线路数据\\处理结果\\车站ic打卡人数分时段统计\\";
	void writeToFileTabSpace(){
		String piecesFileName  = "车站上车统计.txt";
		FileUtil ansFile = new FileUtil(ansPath+day+piecesFileName);
		Station tmp;
		for(String key:stationCount.keySet()){
//			System.out.print(key+"\t");
			tmp = stationCount.get(key);
//			System.out.println(tmp.toString());
			ansFile.writeLine(key+"\t"+tmp.toString());
		}
	}
	
	
	void writeToFileInJsonFormat(){
		Station tmp;
		// 根据结果生成json结果
		JSONArray arrayOn = new JSONArray();
		JSONArray arrayOff = new JSONArray();
		for(int i = 0;i<timeSeg.length-1;i++){
			JSONArray oneTimeSlotArrayOn = new JSONArray();
			JSONArray oneTimeSlotArrayOff = new JSONArray();
			
			for(String key:stationCount.keySet()){
				tmp = stationCount.get(key);
				JSONObject oneOn = new JSONObject();
				JSONObject oneOff = new JSONObject();
				oneOn.put("lng", tmp.getlng());
				oneOff.put("lng", tmp.getlng());
				oneOn.put("lat", tmp.getlat());
				oneOff.put("lat", tmp.getlat());
				if(tmp.getCountOn().get(i) !=0){
					oneOn.put("count", tmp.getCountOn().get(i));
					oneTimeSlotArrayOn.add(oneOn);
				}
				if( tmp.getCountOff().get(i)!=0){
					oneOff.put("count", tmp.getCountOff().get(i));
					oneTimeSlotArrayOff.add(oneOff);
				}
			}
			arrayOff.add(oneTimeSlotArrayOff);
			arrayOn.add(oneTimeSlotArrayOn);
			
			
		}
		//System.out.println(array.toString());
		FileUtil jsonFile = new FileUtil(ansPath+day+"onCountWithoutZeros.txt");
		jsonFile.writeLine( "var points = " +arrayOn.toString());
		FileUtil jsonOffFile = new FileUtil(ansPath+day+"offCountWithoutZeros.txt");
		jsonOffFile.writeLine( "var points = "+arrayOff.toString());
	}
	
	// 对数组初始化
	void initSlots(){
		if(allSlots == null) {
			allSlots = new ArrayList<>();
		}
		for(int i =0; i < timeSeg.length-1;i++){
			allSlots.add(new SlotIcDetail());
		}
	}
	
	
	static String icCardDataPath = "F:\\公交线路数据\\icCardData\\";
	static SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	static Calendar cal = Calendar.getInstance();
	static String []timeSeg = {
		"000000",
		"010000","020000","030000","040000","050000","060000",
		"070000","080000","090000","100000","110000","120000",
		"130000","140000","150000","160000","170000","180000",
		"190000","200000","210000","220000","230000","235959",};//,"0600","0900","1700","1800"};
	
	

	
	public  void staticsLineDetail(String filePre){
		try {
			setTimeSegs(filePre);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		FileUtil fileUtil = new FileUtil(icCardDataPath+filePre+".csv");
		String line1,line2;
		fileUtil.readLine();
		long timeFilter = 0;
		try {
			timeFilter =format.parse(filePre+"000000").getTime()/1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int count =0;
		int exp =0;
		while((line1 = fileUtil.readLine())!=null){
			if(line1.length() < 130 )
				line2 = fileUtil.readLine();
			else line2 = "";
			String []split = (line1+line2).split("\",\"");
			long [] times = changeToTimestamp(split[4], split[3]);
			if(times !=null){
				// 过滤
				if(times[0] > timeFilter && times[1] < timeFilter+24*3600 && times[0] < times[1] && times[1] - times[0] >30 ){
					String lineNu = split[7].substring(split[7].length()-3,split[7].length());
					
					if(!lineFilterSet.isEmpty() &&!lineFilterSet.contains(lineNu))continue;
					int slot = turnTimestampToTimeSeg(times[0]);
					SlotIcDetail tempSlot = allSlots.get(slot);
					tempSlot.addOnePieceIcInfo(lineNu, Integer.parseInt(split[5]),Integer.parseInt(split[6]));
				}else exp++;
			}else{
				exp++;
			}
			count++;
			if(count %10000==0)System.out.println(count);;
		}
		System.out.println(count + " "+ exp);
	}
	
	// 统计各个线路上的所有站点的固有信息Station
	String stationFile = "车站信息.txt";
	// key : 车站的中文名字
	// value: station类，包括经过该车站的线路，以及上下车人数统计。
	Map<String,Station>  stationCount;
	public Map<String,Station> loadStationDetailCorLine(){
		stationCount = new HashMap<>();
		
		FileUtil file = new FileUtil(icCardDataPath+stationFile);
		String tempLine,lineNum;
		String []splits;
		Station sta;
		SlotIcDetail tempSl;
		int base;
		Map<String,LineCount>  countAllLine;
		LineCount lineCount;
		Set<String> visited = new HashSet<>();
		int countSum = 0;
		while((tempLine = file.readLine())!= null){
			splits = tempLine.trim().split("\\s+");
			base  = 0;
			if(splits.length < 13){
				base = 4;
			}
			if(splits.length <11){
				System.out.println(tempLine);
			}
			lineNum = lineNumFormat(splits[5-base].trim());
			if(!lineFilterSet.isEmpty() && !lineFilterSet.contains(lineNum))continue;
			String key  = lineNum+"_"+splits[9-base];
			if(visited.contains(key))continue;
			visited.add(key);
			String stationName = splits[9-base]; 
			if(stationCount.containsKey(stationName)){
				sta = stationCount.get(stationName);
			}else{
				sta = new Station(stationName, Double.parseDouble(splits[13-base]), Double.parseDouble(splits[14-base]));				
				sta.setStationId(splits[8-base]);
				sta.setStationName(splits[9-base]);
				stationCount.put(stationName, sta);
			}
			//sta.increaseCountOn(allSlots.get);
			// 对所有的时段处理
			for(int j = 0 ; j< allSlots.size();j++){
				tempSl = allSlots.get(j);
				countAllLine = tempSl.getCountAllLine();
				if(!countAllLine.containsKey(lineNum))continue;
				lineCount = countAllLine.get(lineNum);
				countSum += lineCount.getUpCount(Integer.parseInt(splits[10-base]));
				sta.increaseCountOff(j, lineCount.getOffCount(Integer.parseInt(splits[10-base])));
				sta.increaseCountOn(j, lineCount.getUpCount(Integer.parseInt(splits[10-base])));
			}
		}
		System.out.println(countSum);
		return stationCount;
	}
	
	// 站位信息中的线路信息和IC打卡不一样，需要补上0
	private String lineNumFormat(String in){
		if(in == null || in.length()==0) return "000";
		int s = in.length();
		if(s >= 3) return in;
		int dif = 3-s;
		StringBuilder sb = new StringBuilder();
		for(int i =0;i<dif;i++){
			sb.append("0");
		}
		sb.append(in);
		return sb.toString();
	}
	
	public int turnTimestampToTimeSeg(String timestamp){
		long time = -1;
		try {
			time = changeStringToTimestamp(timestamp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return turnTimestampToTimeSeg(time);
	}
	public int turnTimestampToTimeSeg(long timestamp){
		int i;
		for( i=0;i<timeSegs.length;i++){
			if(timestamp < timeSegs[i])break;
		}
 
//		if(i==timeSegs.length){
//			return timeSegs.length-1;
//		}
		return i-1;
		
	}
	long []timeSegs;
	public void setTimeSegs(String filePre) throws Exception{
		int s = timeSeg.length;
		this.timeSegs = new long[s];
		int i=0;
		for(i=0;i<s;i++){
			long st = changeStringToTimestamp(filePre+timeSeg[i]);
			this.timeSegs[i] = st;
		}
		
	}
	
	
	long[]changeToTimestamp(String marktime,String tradeTime){
		long []ans = new long[2];
		try {
			ans[0] = changeStringToTimestamp(marktime);
			ans[1] = changeStringToTimestamp(tradeTime);
		} catch (Exception e) {
			return null;
		}
		return ans;
	}
	public static long changeStringToTimestamp (String time)throws Exception{
		Date date = format.parse(time);
		cal.setTime(date);
		//cal.set(Calendar.SECOND, 0);
		return cal.getTimeInMillis()/1000;
	}

	public static String[] getTimeSeg() {
		return timeSeg;
	}

	public static void setTimeSeg(String[] timeSeg) {
		DayPartationCount.timeSeg = timeSeg;
	}

	public long[] getTimeSegs() {
		return timeSegs;
	}

	public void setTimeSegs(long[] timeSegs) {
		this.timeSegs = timeSegs;
	}

 

}
