package cn.itcast.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * 
 * Created by: mengyao
 * 2019年7月16日
 */
public class JsonUtil {

	public static void main(String[] args) {
		System.out.println(isJSONValid("xxxxiiss"));
	}
	
	/**
	 * 校验字符串是否为正确的json格式
	 * @param jsonStr
	 * @return
	 */
	public static boolean isJSONValid(String jsonStr) {
		try {
			JSONObject.parseObject(jsonStr);
		} catch (JSONException ex) {
			try {
				JSONObject.parseArray(jsonStr);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 对象转json
	 * @param obj
	 * @return
	 */
	public static String obj2Json(Object obj) {
		return obj2Json(obj, false);
	}
	
	public static String obj2Json(Object obj, boolean format) {
		return JSON.toJSONString(obj, format);
	}
	
	/**
	 * 对象转map
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> obj2Map(Object obj) {
		JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
		return JSON.parseObject(JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat));
	}
	
	/**
	 * list转json
	 * @param list
	 * @return
	 */
	public static String list2Json(Collection<?> list) {
		return JSON.toJSONString(list, true);
	}
	
	/**
	 * list转jsonArr
	 * @param list
	 * @return
	 */
	public static List<String> list2Jsons(Collection<?> list) {
		List<String> arr = new ArrayList<>();
		list.forEach(obj-> arr.add(JSON.toJSONString(obj, true)));
		return arr;
	}
	
	/**
	 * map转json
	 * @param map
	 * @return
	 */
	public static String map2Json(Map<?, ?> map) {
		return JSON.toJSONString(map, true);
	}
	
	/**
	 * json转对象
	 * @param jsonStr
	 * @param caz
	 * @return
	 */
	public static Object json2Obj(String jsonStr, Class<?> caz) {
		if (null == jsonStr || jsonStr.isEmpty()) {
			return null;
		}
		return JSON.parseObject(jsonStr, caz);
	} 
	
	/**
	 * json转list
	 * @param jsonStr
	 * @param caz
	 * @return
	 */
	public static List<?> json2Array(String jsonStr, Class<?> caz) {
		if (null == jsonStr || jsonStr.isEmpty()) {
			return null;
		}
		return JSON.parseArray(jsonStr, caz);
	} 
	
}
