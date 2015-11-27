package qiang.slot;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * 这里是统计所有站点的上车人数。
 * 1. 首先遍历一遍文件，就可以获得各个线路，以其线路上各个站点的上车人数。
 * 2. 最后再将相同车站的站点合并。
 * 
 * @author jq
 *
 */
public class SlotIcDetail {
	// Key : lineNO 
	// value : 该线路的详细统计
	Map<String,LineCount>  countAllLine;
	int countSum;
	public SlotIcDetail(){
		countSum = 0;
		countAllLine = new HashMap<String,LineCount> ();
		
	}
	
	public void increaseCount(){
		countSum++;
	}
	public boolean addOnePieceIcInfo(String lineNo,int markStation,int tradeStation){
		
		LineCount tempL;
		
		if(countAllLine.containsKey(lineNo)){
			tempL = countAllLine.get(lineNo);

		}else{
			 tempL = new LineCount();
			countAllLine.put(lineNo, tempL);
		}
		tempL.addOneUpOff(markStation, tradeStation);
		increaseCount();
		return true;
	}

	public Map<String, LineCount> getCountAllLine() {
		return countAllLine;
	}

	public void setCountAllLine(Map<String, LineCount> countAllLine) {
		this.countAllLine = countAllLine;
	}

	public int getCountSum() {
		return countSum;
	}

	public void setCountSum(int countSum) {
		this.countSum = countSum;
	}
	
}
