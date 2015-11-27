package qiang.icCardDataPartitaionAndMap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import qiang.bean.OneBusSchedule;
import qiang.bean.OneTrip;
import qiang.icStationMapToLineStation.GetLineDetail;
import qiang.icStationMapToLineStation.MapIcStationToStationId;
import qiang.util.FileUtil;
import qiang.util.bean.LineDetailBean;
import qiang.util.bean.SortComparators;



/**
 * 这里对一条线路做处理：
 * 1. 需要按照busid分组，然后分组后，组内按照时间排序。
 * 2. 目标是为了识别出给定busid，该车走了多少趟。以及每趟车的载客打卡信息，写入到分车次统计文件夹中。
 * 	主要是为了车次的统计。
 * 
 * @author jq
 *
 */
public class AnalysisOneBusLine {

	public static void main(String[] args) {
		int d = 20150803;
		//String day = "20150804";
		for(;d<20150804;d++){
			System.out.println(d);
			AnalysisOneBusLine  ab = new AnalysisOneBusLine();
			ab.analysisOneDay(""+d);
		}
		System.out.println(countAllIC+"\t"+countValiddAll);

	}
	public static boolean writeOpen = false;
	public static String day = null;
	String busLine;
	public void analysisOneDay(String day){
		AnalysisOneBusLine.day = day;
		File dir = new File(path+day);
		String []allFiles = null ;
		if(dir.exists() && dir.isDirectory()){
			allFiles = dir.list();
		}
		if(allFiles == null )return;
		if(writeOpen){
			File ansF= new File(ansPath+day+"busSchedult.txt");
			if(ansF.exists())ansF.delete();
			File ff = new File("F:\\公交线路数据\\映射结果\\"+AnalysisOneBusLine.day+"ic映射为空differ大于5.txt");
			if(ff.exists())ff.delete();
		}
		// 每次处理一个线路
		for(int i = 0 ; i< allFiles.length;i++){
			//System.out.println(allFiles[0]);
			analysisOneBusLine(day, allFiles[i]);
		}
		System.out.println(alllMappedStations707.size());
		
		
		
		System.out.println("allMappedGPS:"+allMappedGPS.size());
		
		// 所有站点的名字
		HashMap<String,String> allStationsNameSet909 = GetLineDetail.getAllStationsNameSet909();
		
		FileUtil gps = new FileUtil(ansPath+day+"diff"+gpsCoverFile);
		
		for(String name : allStationsNameSet909.keySet()){
			if(!allMappedGPS.containsKey(name))
				gps.writeLine(allStationsNameSet909.get(name));
		}
		gps.close();
		
		
		
	}
	
	Map<String,String> allMappedGPS = new HashMap<>();
	
	LineDetailBean lineMapInfo = null;
	public void analysisOneBusLine(String day,String lineIdFileName){
		this.busLine = lineIdFileName.substring(0,lineIdFileName.length()-4);
		this.lineMapInfo = lineIcIdMapDetail.get(this.busLine);
		// 读取给定线路的所有ic卡信息
		getTripMap(path+day+"\\"+lineIdFileName);
		// 按照时间排序，将上车站点做映射。
		sortTripMap();
	}
	
	String path = "F:\\公交线路数据\\处理结果\\";
	String ansPath = "F:\\公交线路数据\\映射结果\\";
	String gpsCoverFile = "stationGps.txt";

	
	public void sortTripMap(){
		ArrayList<OneTrip> tempBusTrips;
		File f = new File(ansPath+this.day+"sort");
		if(!f.exists()){
			f.mkdirs();
		}
		if(writeOpen){
			File f2 = new File(ansPath+this.day+"sort");
			if(!f2.exists()){
				f2.mkdirs();
			}else{
				f2.delete();
				f2.mkdir();
			}
		}
		// 获取当前线路的ic 映射关系
		//FileUtil fileU = new FileUtil("F:\\公交线路数据\\处理结果\\IC打卡上下车合并\\punch.txt",true);
		for(String key:tripMap.keySet()){
			tempBusTrips = tripMap.get(key);
			tempBusTrips.sort(SortComparators.markTimeComparator);
			//fileU.writeLine(key);
//			// 统计当前busId的每次schedule的情况
			System.out.println(key+"\t车次划分");
			ArrayList<OneBusSchedule>  allScheduleCount = countOneBusScheduleTimes(tempBusTrips);
			boolean print = false;
			for(OneBusSchedule one:allScheduleCount){
				 //tempAns = sortPunchInList(one.getThisBusScheduleTrips(),PunchInComparators.punchInStationUpTimeUpComparator);
				 MapIcStationToStationId.mapUpOffIcStationToStationId(one,this.busLine);
				 //System.out.println("new sche");
				 print = false;
				 for(OneTrip onet:one.getThisBusScheduleTrips()){
					 //System.out.println(onet.toStringOneLine());
					 if( onet.getRealMarkStation() < 0 || onet.getRealTredeStation() < 0){
						 print = true;
						 //System.err.println(onet.toStringOneLine());
					 }
				 }
				 
				 if(print && AnalysisOneBusLine.writeOpen){
					 FileUtil noMapLog = new FileUtil("F:\\公交线路数据\\映射结果\\"+day+"noMapLog.txt",true);
					 noMapLog.writeLine("one sche:");	 
					 for(OneTrip onet:one.getThisBusScheduleTrips()){
						 //System.out.println(onet.toStringOneLine());
						 if(onet.isValid() && onet.getRealMarkStation()<0 ){
							 noMapLog.writeLine(onet.toStringOneLine());
							 //System.err.println(onet.toStringOneLine());
						 }
					 }
				 }
				
			 }
			System.out.println("准备写入文件");
//			// 将收费车站和真实站点进行映射。 上车
//			for(OneBusSchedule one:allScheduleCount){
//				one.busSchedultTripsFilterByMarkTimeStaion();
//				MapIcStationToStationId.mapUpIcStationToStationId(this.lineMapInfo,one);
//			}
			
			 // 将map后的结果写入到文件中。
			writeBusScheduleToFile(allScheduleCount);
			//break;
		}
	}
	// 获取道路信息。key line id,value 详细信息
	
	static Map<String,LineDetailBean>  lineIcIdMapDetail = GetLineDetail.loadStationDetail();
	static Set<String> alllMappedStations707 = new HashSet<>();
	public void writeBusScheduleToFile(ArrayList<OneBusSchedule>  allScheduleCount){
		List<OneTrip> thisBusScheduleTrips;
		boolean write = false;
		for(OneBusSchedule one :allScheduleCount){
			if(one.getThisBusScheduleTrips().size()==0)continue;
			thisBusScheduleTrips = one.getThisBusScheduleTrips();
			for(OneTrip trip:thisBusScheduleTrips){
				if(trip.isValid() && trip.getRealMarkStation() > -1 && trip.getRealTredeStation() > -1){
					write = true;
					break;
				}
			}
			if(write)break;
		}
		if(!write)return;
		FileUtil fileUtil = null;
		if(writeOpen){
			fileUtil = new FileUtil(ansPath+day+"sort\\"+this.busLine+".txt",true);
		}
		int schedult=0;
		for(OneBusSchedule one :allScheduleCount){
			if(one.getThisBusScheduleTrips().size()==0)continue;
			if(writeOpen)
				fileUtil.writeLine(this.busLine+"\t"+schedult+"\t"+one.toString());
			schedult++;
			thisBusScheduleTrips = one.getThisBusScheduleTrips();
			for(OneTrip trip:thisBusScheduleTrips){
				if(trip.isValid() && trip.getRealMarkStation() > -1 && trip.getRealTredeStation() > -1){
					if(writeOpen)
						fileUtil.writeLine(trip.toStringOneLine());
					countValiddAll++;
					alllMappedStations707.add(trip.getRealMarkStationDetail().getName());
					alllMappedStations707.add(trip.getRealTreadStationDetail().getName());
					double [] latlng ;
					if(!allMappedGPS.containsKey(trip.getRealMarkStationDetail().getName())){
						latlng = trip.getMarkStaionGPSLat_Lng();
						allMappedGPS.put(trip.getRealMarkStationDetail().getName(),latlng[0]+"\t"+latlng[1]);
					}
					if(!allMappedGPS.containsKey(trip.getRealTreadStationDetail().getName())){
						latlng = trip.getTradeStationGPSLat_Lng();
						allMappedGPS.put(trip.getRealTreadStationDetail().getName(),latlng[0]+"\t"+latlng[1]);
					}
					
				}
			}
		}
		//fileUtil.close();
	}
	/**
	 * //这里根据排好序的上车station 和时间，统计车次 
	 * 当上车站的编号小于一直都是大于下车站的编号，那么当前就是一个方向的
	 * 当上车编号小于下车编号了，说明变换为反方向了。
	 * 通过这种乒乓来判断当前车走了几趟。
	 * 
	 * 统计两个方向分别走了多少趟，
	 * 0  是 上车station 大于 下车 station
	 * 1 是   下车大于上车
	 * @param tempBusTrips
	 * @return
	 */
	public ArrayList<OneBusSchedule> countOneBusScheduleTimes(ArrayList<OneTrip> tempBusTrips){
		ArrayList<OneBusSchedule> allScheduleCount = new ArrayList<>();
		//int []ans = new int[2];
		int s = tempBusTrips.size();
		OneTrip lastTrip = tempBusTrips.get(0);
//		ans[tripDir(lastTrip)]++;
		OneTrip curTrip;
		OneBusSchedule curS = new OneBusSchedule(lastTrip.getBusId(), tripDir(lastTrip));
		allScheduleCount.add(curS);
		curS.addOneTrip(lastTrip);
		curS.setFirstIcMarkTime(lastTrip.getMarkTime());
		curS.setLastIcTradTime(lastTrip.getTradeTime());
		for(int i = 1;i<s;i++){
			curTrip = tempBusTrips.get(i);
			if(sameDir(curTrip, lastTrip) && tripTimeSameDir(curTrip,lastTrip)  || tripTimeClose(lastTrip, curTrip)){
				curS.addOneTrip(curTrip);
				curS.setLastIcTradTime(curTrip.getTradeTime()); // 每次都要更新下车时间
				lastTrip = curTrip;
				continue;
			}
			curS = new OneBusSchedule(curTrip.getBusId(), tripDir(curTrip));
			allScheduleCount.add(curS);
			curS.addOneTrip(curTrip);
			curS.setFirstIcMarkTime(curTrip.getMarkTime());
			curS.setLastIcTradTime(curTrip.getTradeTime());
			lastTrip = curTrip;
//			ans[tripDir(curTrip)]++;
		}
		return allScheduleCount;
	}
	/**
	 *  由小站到大站编号，就是0方向。
	 * @param one
	 * @return
	 */
	int tripDir(OneTrip one){
		if(one.getMarkstation() > one.getTradestation()){
			return 1;
		}
		return 0;
	}
	boolean sameDir(OneTrip one,OneTrip two){
		int a = tripDir(one)+tripDir(two);
		if(a == 2 || a== 0)return true;
		return false;
	}
	
	// 连续的两个trip，不仅要方向一致。还要trip之间的时间间隔不能超过一定的时间> 这里目前不考虑这个情况
	public boolean tripTimeSameDir(OneTrip one,OneTrip two){
		if(Math.abs(two.getMarkTime() - one.getMarkTime()) >3600 )return false;
		return true;
	}
	// 十分钟以内就是一个车次
	public boolean tripTimeClose(OneTrip one,OneTrip two){
		if(Math.abs(two.getMarkTime() - one.getMarkTime()) < 5*60 )return true;
		return false;
	}
	
	public static int countAllIC = 0;
	public static int countValiddAll = 0;
	
	Map<String,ArrayList<OneTrip>> tripMap = null;
	public void getTripMap(String fileName){
		 tripMap = new HashMap<>();
		FileUtil fileUtil = new FileUtil(fileName);
		System.out.println(fileName);
		String tempLine;
		OneTrip tempOneTrip;
		String busId;
		ArrayList<OneTrip> tempBusTrips;
		while((tempLine = fileUtil.readLine())!=null){
			countAllIC++;
			tempOneTrip = OneTrip.tripFactory(tempLine);
			if(tempOneTrip == null)continue;
			busId = tempOneTrip.getBusId();
			if(tripMap.containsKey(busId)){
				tempBusTrips = tripMap.get(busId);
			}else{
				tempBusTrips = new ArrayList<>();
				tripMap.put(busId, tempBusTrips);
			}
			tempBusTrips.add(tempOneTrip);
		}
		System.out.println(tripMap.size());
	}
}
