package cn.itcast.dw.realtime.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ECharts要求的结构
 * Created by: mengyao 
 * 2019年8月28日
 */
public class ResultBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2541718397950100311L;
	private List<Map<String, String>> visitor;
	private List<Map<String, String>> convert;
	private WeekSale weekSale;
	private OrderNum orderNum;
	private Sales sales;
	private List<Map<String, Object>> top8orderNum;
	private WeekOrderFinish weekOrderFinish;
	private Top4sale top4sale;
	private List<ArrayList<ChinaMap>> chinaMap;
	
	public ResultBean() {
		super();
	}
	public List<Map<String, String>> getVisitor() {
		return visitor;
	}
	public void setVisitor(List<Map<String, String>> visitor) {
		this.visitor = visitor;
	}
	public List<Map<String, String>> getConvert() {
		return convert;
	}
	public void setConvert(List<Map<String, String>> convert) {
		this.convert = convert;
	}
	public WeekSale getWeekSale() {
		return weekSale;
	}
	public void setWeekSale(WeekSale weekSale) {
		this.weekSale = weekSale;
	}
	public OrderNum getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(OrderNum orderNum) {
		this.orderNum = orderNum;
	}
	public Sales getSales() {
		return sales;
	}
	public void setSales(Sales sales) {
		this.sales = sales;
	}
	public List<Map<String, Object>> getTop8orderNum() {
		return top8orderNum;
	}
	public void setTop8orderNum(List<Map<String, Object>> top8orderNum) {
		this.top8orderNum = top8orderNum;
	}
	public WeekOrderFinish getWeekOrderFinish() {
		return weekOrderFinish;
	}
	public void setWeekOrderFinish(WeekOrderFinish weekOrderFinish) {
		this.weekOrderFinish = weekOrderFinish;
	}
	public Top4sale getTop4sale() {
		return top4sale;
	}
	public void setTop4sale(Top4sale top4sale) {
		this.top4sale = top4sale;
	}
	public List<ArrayList<ChinaMap>> getChinaMap() {
		return chinaMap;
	}
	public void setChinaMap(List<ArrayList<ChinaMap>> chinaMap) {
		this.chinaMap = chinaMap;
	}
	@Override
	public String toString() {
		return "{\"visitor\":" + visitor + ", convert\":" + convert + ", weekSale\":" + weekSale + ", orderNum\":"
				+ orderNum + ", sales\":" + sales + ", top8orderNum\":" + top8orderNum + ", weekOrderFinish\":"
				+ weekOrderFinish + ", top4sale\":" + top4sale + "}";
	}
	public static class WeekSale {
		private List<String> xAxis;
		private List<Integer> week;
        private List<Integer> month;
		public WeekSale() {
			super();
		}
		public WeekSale(List<String> xAxis, List<Integer> week, List<Integer> month) {
			super();
			this.xAxis = xAxis;
			this.week = week;
			this.month = month;
		}
		public List<String> getxAxis() {
			return xAxis;
		}
		public void setxAxis(List<String> xAxis) {
			this.xAxis = xAxis;
		}
		public List<Integer> getWeek() {
			return week;
		}
		public void setWeek(List<Integer> week) {
			this.week = week;
		}
		public List<Integer> getMonth() {
			return month;
		}
		public void setMonth(List<Integer> month) {
			this.month = month;
		}
		@Override
		public String toString() {
			return "{\"xAxis\":" + xAxis + ", week\":" + week + ", month\":" + month + "}";
		}
	}

	public static class OrderNum {
		private long mouthOrderNum;
		private long weekOrderNum;
		private long dayOrderNum;
		private ArrayList<ArrayList<HashMap<String, Object>>> data = new ArrayList<ArrayList<HashMap<String,Object>>>();
		public OrderNum() {
			super();
		}
		public OrderNum(long mouthOrderNum, long weekOrderNum, long dayOrderNum, ArrayList<ArrayList<HashMap<String,Object>>> data) {
			super();
			this.mouthOrderNum = mouthOrderNum;
			this.weekOrderNum = weekOrderNum;
			this.dayOrderNum = dayOrderNum;
			this.data = data;
		}
		public long getMouthOrderNum() {
			return mouthOrderNum;
		}
		public void setMouthOrderNum(long mouthOrderNum) {
			this.mouthOrderNum = mouthOrderNum;
		}
		public long getWeekOrderNum() {
			return weekOrderNum;
		}
		public void setWeekOrderNum(long weekOrderNum) {
			this.weekOrderNum = weekOrderNum;
		}
		public long getDayOrderNum() {
			return dayOrderNum;
		}
		public void setDayOrderNum(int dayOrderNum) {
			this.dayOrderNum = dayOrderNum;
		}
		public ArrayList<ArrayList<HashMap<String,Object>>> getData() {
			return data;
		}
		public void setData(ArrayList<ArrayList<HashMap<String,Object>>> data) {
			this.data = data;
		}
		@Override
		public String toString() {
			return "{\"mouthOrderNum\":" + mouthOrderNum + ", weekOrderNum\":" + weekOrderNum + ", dayOrderNum\":"
					+ dayOrderNum + ", data\":" + data + "}";
		}
	}
	public static class Sales {
		private String monthSale;
		private String daySale;
        private List<String> xAxis = new ArrayList<String>();
        private List<Double> data = new ArrayList<Double>();
		public Sales() {
			super();
		}
		public Sales(String monthSale, String daySale, Map<String, Double> xy) {
			super();
			this.monthSale = monthSale;
			this.daySale = daySale;
			xAxis.addAll(xy.keySet());
			data.addAll(xy.values());
		}
		public String getMonthSale() {
			return monthSale;
		}
		public void setMonthSale(String monthSale) {
			this.monthSale = monthSale;
		}
		public String getDaySale() {
			return daySale;
		}
		public void setDaySale(String daySale) {
			this.daySale = daySale;
		}
		public List<String> getxAxis() {
			return xAxis;
		}
		public void setxAxis(List<String> xAxis) {
			this.xAxis = xAxis;
		}
		public List<Double> getData() {
			return data;
		}
		public void setData(List<Double> data) {
			this.data = data;
		}
		@Override
		public String toString() {
			return "{\"monthSale\":" + monthSale + ", daySale\":" + daySale + ", xAxis\":" + xAxis + ", data\":" + data
					+ "}";
		}
	}
	public static class WeekOrderFinish {
		private List<String> xAxis = new ArrayList<String>();
        private List<Long> data = new ArrayList<Long>();
        public WeekOrderFinish() {
			super();
		}
        public WeekOrderFinish(Map<String, Long> data) {
			super();
			this.xAxis.addAll(data.keySet());
			this.data.addAll(data.values());
		}
		public List<String> getxAxis() {
			return xAxis;
		}
		public void setxAxis(List<String> xAxis) {
			this.xAxis = xAxis;
		}
		public List<Long> getData() {
			return data;
		}
		public void setData(List<Long> data) {
			this.data = data;
		}
		@Override
		public String toString() {
			return "{\"xAxis\":" + xAxis + ", data\":" + data + "}";
		}
	}
	public static class Top4sale {
		private List<String> area;
        private List<Integer> value;
		public Top4sale() {
			super();
		}
		public Top4sale(List<String> area, List<Integer> value) {
			super();
			this.area = area;
			this.value = value;
		}
		public List<String> getArea() {
			return area;
		}
		public void setArea(List<String> area) {
			this.area = area;
		}
		public List<Integer> getValue() {
			return value;
		}
		public void setValue(List<Integer> value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return "{\"area\":" + area + ", value\":" + value + "}";
		}
	}
	//name: "黑龙江",value: [127.9688,45.368,2300]
	public static class ChinaMap {
		private String name;
		private int value;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return "{\"name\":" + name + ", value\":" + value + "}";
		}
	}
}
