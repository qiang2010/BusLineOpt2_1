package qiang.util.bean;

import java.util.Comparator;

import qiang.bean.OneTrip;

public class SortComparators {

	
	public static Comparator<OneTrip> markTimeComparator =  new Comparator<OneTrip>() {
		@Override
		public int compare(OneTrip t1, OneTrip t2) {
			return (int)(t1.getMarkTime() - t2.getMarkTime());
			//return  (int)(t1.getTradeTime()-t2.getTradeTime());
		}
	};
	
	public static Comparator<OneTrip> markTimeIcStationUpComparator =  new Comparator<OneTrip>() {
		@Override
		public int compare(OneTrip t1, OneTrip t2) {
			int f = (int)(t1.getMarkTime() - t2.getMarkTime());
			if(f !=0) return f;
			return t1.getMarkstation() - t2.getMarkstation();
			//return  (int)(t1.getTradeTime()-t2.getTradeTime());
		}
	};
	public static Comparator<OneTrip> markTimeIcStationDownComparator =  new Comparator<OneTrip>() {
		@Override
		public int compare(OneTrip t1, OneTrip t2) {
			int f = (int)(t1.getMarkTime() - t2.getMarkTime());
			if(f !=0) return f;
			return t2.getMarkstation() - t1.getMarkstation();
			//return  (int)(t1.getTradeTime()-t2.getTradeTime());
		}
	};
	
	public static Comparator<OneTrip> tradeTimeComparator =  new Comparator<OneTrip>() {
		@Override
		public int compare(OneTrip t1, OneTrip t2) {
			return (int)(t1.getTradeTime() - t2.getTradeTime());
			//return  (int)(t1.getTradeTime()-t2.getTradeTime());
		}
	};
	public static Comparator<OneTrip> tradeTimeIcStationUpComparator =  new Comparator<OneTrip>() {
		@Override
		public int compare(OneTrip t1, OneTrip t2) {
			int f =(int)(t1.getTradeTime() - t2.getTradeTime());
			if(f != 0) return f;
			return t1.getMarkstation() - t2.getMarkstation();
					
			//return  (int)(t1.getTradeTime()-t2.getTradeTime());
		}
	};
	public static Comparator<OneTrip> tradeTimeIcStationDownComparator =  new Comparator<OneTrip>() {
		@Override
		public int compare(OneTrip t1, OneTrip t2) {
			int f =(int)(t1.getTradeTime() - t2.getTradeTime());
			if(f != 0) return f;
			return t2.getMarkstation() - t1.getMarkstation();
			//return  (int)(t1.getTradeTime()-t2.getTradeTime());
		}
	};
	
}
