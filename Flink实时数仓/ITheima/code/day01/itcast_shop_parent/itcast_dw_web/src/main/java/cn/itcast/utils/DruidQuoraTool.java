package cn.itcast.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Created by: mengyao
 * 2019年7月29日
 */
public class DruidQuoraTool {

	//日订单数
	public static final String DAY_ORDER_NUMBER = "dayOrderNumber";
	//月订单数
	public static final String MONTH_ORDER_NUMBER = "monthOrderNumber";
	//年订单数
	public static final String YEAR_ORDER_NUMBER = "yearOrderNumber";
	//周订单数
	public static final String WEEK_ORDER_NUMBER = "weekOrderNumber";
	//各区域订单数
	public static final String AREA_ORDER_NUMBER = "areaOrderNumber";
	//各区域日订单数占比
	public static final String AREA_DAY_ORDER_FINISH = "areaDayOrderFinish";
	//周订单完成占比
	public static final String AREA_WEEK_ORDER_FINISH = "areaWeekOrderFinish";
	//小时销售额
	public static final String HOUR_SALES = "hourSales";
	//月总销售额
	public static final String MONTH_SALES = "monthSales";
	//年总销售额
	public static final String YEAR_SALES = "yearSales";
	//uv,pv,独立ip数
	public static final String VISITOR = "visitor";
	
	@SuppressWarnings("serial")
	public static Map<String, String> sqlMap = new HashMap<String, String>(){{
		put(DAY_ORDER_NUMBER, "SELECT SUM(\"count\") FROM \"dws_od1\" WHERE \"__time\" >= CURRENT_TIMESTAMP - INTERVAL '1' DAY");
		put(MONTH_ORDER_NUMBER, "SELECT SUM(\"count\") FROM \"dws_od1\" WHERE \"__time\" >= CURRENT_TIMESTAMP - INTERVAL '1' MONTH");
		put(YEAR_ORDER_NUMBER, "SELECT SUM(\"count\") FROM \"dws_od1\" WHERE \"__time\" >= CURRENT_TIMESTAMP - INTERVAL '1' YEAR");
		// SELECT "__time",sum("count") FROM "dws_od1" where "__time" CURRENT_TIMESTAMP - INTERVAL '7' DAY AND (CURRENT_TIMESTAMP - INTERVAL '7' DAY)  - INTERVAL '7' DAY GROUP BY "__time"
		put(WEEK_ORDER_NUMBER, "SELECT \"__time\",sum(\"count\") FROM \"dws_od1\" where \"__time\" >= CURRENT_TIMESTAMP - INTERVAL '7' DAY GROUP BY \"__time\"");
		put(AREA_ORDER_NUMBER, "SELECT \"areaId\",SUM(\"count\") AS \"curHourSales\" FROM \"dws_od1\" WHERE \"__time\" >= CURRENT_TIMESTAMP - INTERVAL '1' DAY GROUP BY \"areaId\"");
		put(AREA_DAY_ORDER_FINISH, "SELECT \"areaId\", SUM(\"count\") AS \"areaFinshOrderNum\" FROM \"dws_od1\" WHERE orderStatus=2 AND \"__time\" >= CURRENT_TIMESTAMP - INTERVAL '1' DAY GROUP BY \"areaId\" ORDER BY \"areaFinshOrderNum\" DESC LIMIT 10");
		put(AREA_WEEK_ORDER_FINISH, "SELECT EXTRACT(DAY FROM \"__time\") AS \"latelyDay\", SUM(\"count\") AS \"areaFinshOrderNum\" FROM \"dws_od1\" WHERE orderStatus=2 AND \"__time\" >= CURRENT_TIMESTAMP - INTERVAL '7' DAY GROUP BY EXTRACT(DAY FROM \"__time\") ORDER BY \"areaFinshOrderNum\" DESC LIMIT 10");
		put(HOUR_SALES, "SELECT EXTRACT(HOUR FROM \"__time\") AS \"curHour\",SUM(\"totalMoney\") AS \"curHourSales\" FROM \"dws_od1\" WHERE \"__time\" >= CURRENT_TIMESTAMP - INTERVAL '1' HOUR GROUP BY EXTRACT(HOUR FROM \"__time\")");
		put(MONTH_SALES, "SELECT SUM(\"totalMoney\") AS \"curMonthSales\" FROM \"dws_od1\" WHERE \"__time\" >= CURRENT_TIMESTAMP - INTERVAL '1' MONTH");
		put(YEAR_SALES, "SELECT SUM(\"totalMoney\") AS \"curMonthSales\" FROM \"dws_od1\" WHERE \"__time\" >= CURRENT_TIMESTAMP - INTERVAL '1' YEAR");
	}};
	
}
