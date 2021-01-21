package cn.itcast.dw.realtime.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * Created by: mengyao
 * 2019年8月29日
 */
public interface DashboardService {

	
	/**
	 * 用户日活分析
	 */
	public List<Map<String, String>> dau();
	
	/**
	 * 漏斗转化分析
	 */
	public List<Map<String, String>> convert();
	
	/**
	 * 周销售环比分析
	 * 	
	 * Map：
	 * 		key=lwd,value=lwdList
	 * 		key=lw,value=lwList
	 * 		key=twd,value=twdList
	 * 		key=tw,value=twList
	 */
	public Map<String, Object> weekSale();
	
	/**
	 * 日订单数
	 */
	public long dayOrderNum();
	
	/**
	 * 周订单数
	 */
	public long weekOrderNum();
	
	/**
	 * 月订单数
	 */
	public long monthOrderNum();
	
	/**
	 * 区域订单数
	 */
	public Map<Integer, Long> areaOrderNum();
	
	/**
	 * 月总销售额
	 */
	public String monthSale();
	
	/**
	 * 日总销售额
	 */
	public String daySale();
	
	/**
	 * 24小时销售额
	 */
	public Map<String, Double> hourSale();
	
	/**
	 * 区域订单状态占比分析（Top8区域订单的订单数）
	 */
	public Map<Integer, Long> areaOrderState();
	
	
	/**
	 * 周订单完成趋势分析（周一到周日每天的订单数）
	 */
	public Map<String, Long> weekOrderFinish();
	
	/**
	 * 今日top4地区销售排行
	 */
	public Map<Integer, Double> top4AreaSale();

	
}
