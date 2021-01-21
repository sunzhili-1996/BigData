package cn.itcast.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import cn.itcast.dw.realtime.beans.WeekBean;

/**
 * 
 * Created by: mengyao
 * 2019年9月5日
 */
public class DateUtil {

	private static final ThreadLocal<SimpleDateFormat> FORMATTER_YMD = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
	private static Calendar CALENDAR = Calendar.getInstance();
	
	public static void main(String[] args) {
		// 近6天，含今天，共7天
		int twNum = 6;
		// 本周近一周
		LinkedList<WeekBean> twDay = new LinkedList<WeekBean>();
		for (int i = twNum; i >= 0; i--) {
			String day = DateUtil.latelyNday(i);
			String week = DateUtil.dayForWeek(day);
			twDay.add(new WeekBean(day, week, 0L, i));
		}
		// 上周
		int lwNum = 7;
		// 上周的近一周
		LinkedList<WeekBean> lwDay = new LinkedList<WeekBean>();
		for (int i = twNum; i >= 0; i--) {
			String day = DateUtil.latelyNday(7, i);
			String week = DateUtil.dayForWeek(day);
			lwDay.add(new WeekBean(day, week, 0L, i));
		}
		LinkedHashMap<String, Long> map = new LinkedHashMap<String, Long>();
		twDay.stream().sorted((o1,o2) -> o2.getSort().compareTo(o1.getSort()));
		twDay.forEach(b->{
			map.put(b.getWeek(), b.getValue());
		});
		System.out.println(map);
	}

	/**
	 * 获取当前日期的近N天的日期
	 * @param nDay
	 * @return yyyy-MM-dd
	 */
	public static String latelyNday(int nDay) {
		CALENDAR.setTime(new Date());
		CALENDAR.add(Calendar.DATE, - nDay);
		Date date = CALENDAR.getTime();
		String latelyNday = FORMATTER_YMD.get().format(date);
		return latelyNday;
	}
	
	/**
	 * 获取指定日期的近N天的日期，例如上周环比：从前7天开始，再计算过去7天
	 * @param appointNday 指定从前几天的日期开始
	 * @param nDay 前几天的过去N天
	 * @return
	 */
	public static String latelyNday(int appointNday, int nDay) {
		try {
			SimpleDateFormat sdf = FORMATTER_YMD.get();
			String appointNdayStr = latelyNday(appointNday);
			Date cdate = sdf.parse(appointNdayStr);
			CALENDAR.setTime(cdate);
			CALENDAR.add(Calendar.DATE, - nDay);
			Date date = CALENDAR.getTime();
			String latelyNday = FORMATTER_YMD.get().format(date);
			return latelyNday;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取当前日期的近N月的日期
	 * @param nMonth
	 * @return yyyy-MM-dd
	 */
	public static String latelyNmonth(int nMonth) {
		CALENDAR.setTime(new Date());
		CALENDAR.add(Calendar.MONTH, - nMonth);
		Date date = CALENDAR.getTime();
		String latelyNday = FORMATTER_YMD.get().format(date);
		return latelyNday;
	}

	/**
	 * 获取当前日期的近N月的日期
	 * @param nYear
	 * @return yyyy-MM-dd
	 */
	public static String latelyNyear(int nYear) {
		CALENDAR.setTime(new Date());
		CALENDAR.add(Calendar.YEAR, - nYear);
		Date date = CALENDAR.getTime();
		String latelyNday = FORMATTER_YMD.get().format(date);
		return latelyNday;
	}
	
	/**
	 * 获取日期所属的星期
	 * @param day yyyy-MM-dd格式的日期字符串
	 * @return
	 */
	public static String dayForWeek(String day) {
        try {
			Date tmpDate = FORMATTER_YMD.get().parse(day);  
			Calendar cal = Calendar.getInstance(); 
			String[] weekDays = {"周日","周一","周二","周三","周四","周五","周六"};
			try {
			    cal.setTime(tmpDate);
			} catch (Exception e) {
			    e.printStackTrace();
			}
			int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
			if (w < 0)
			    w = 0;
			return weekDays[w];
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return null;
    }  
	
}
