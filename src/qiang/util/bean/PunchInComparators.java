package qiang.util.bean;

import java.util.Comparator;


/**
 * 
 * 对打卡记录（统一的上车下次都包括） 排序
 * 
 * @author jq
 *
 */
public class PunchInComparators {

	public static Comparator<OnePunchIn> punchInTimeUpComparator = new Comparator<OnePunchIn>() {

		@Override
		public int compare(OnePunchIn first, OnePunchIn second) {
			// TODO Auto-generated method stub
			return (int)(first.getPunchTime() - second.getPunchTime());
		}
	}; 
	public static Comparator<OnePunchIn> punchInTimeUpStationUpComparator = new Comparator<OnePunchIn>() {

		@Override
		public int compare(OnePunchIn first, OnePunchIn second) {
			// TODO Auto-generated method stub
			int f = (int)(first.getPunchTime() - second.getPunchTime());
			if( f !=0) return f;
			return first.getIcStation() - second.getIcStation();
		}
	};
	public static Comparator<OnePunchIn> punchInTimeUpStationDownComparator = new Comparator<OnePunchIn>() {

		@Override
		public int compare(OnePunchIn first, OnePunchIn second) {
			// TODO Auto-generated method stub
			int f = (int)(first.getPunchTime() - second.getPunchTime());
			if( f !=0) return f;
			return second.getIcStation() - first.getIcStation();
		}
	};
	
	/**
	 * 这样就可以将station分组，组内按照时间由小到大排序。
	 */
	public static Comparator<OnePunchIn> punchInStationUpTimeUpComparator = new Comparator<OnePunchIn>() {

		@Override
		public int compare(OnePunchIn first, OnePunchIn second) {
			// TODO Auto-generated method stub
			int f = first.getIcStation() - second.getIcStation();
			if( f !=0) return f;
			return (int)(first.getPunchTime() - second.getPunchTime());
		}
	};
	
}
