package cn.itcast.dw.realtime.beans;

public class WeekBean {

	private String day;
	private String week;
	private long value;
	private int sort;

	public WeekBean() {
		super();
	}

	public WeekBean(String day, String week, long value, int sort) {
		super();
		this.day = day;
		this.week = week;
		this.value = value;
		this.sort = sort;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	@Override
	public String toString() {
		return "{\"day\":" + day + ", week\":" + week + ", value\":" + value + ", sort\":" + sort + "}";
	}

}
