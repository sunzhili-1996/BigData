package cn.itcast.dw.realtime.beans;

public class AreaBean {

	private long id;
	private String code;
	private String parentCode;
	private int level;
	private String name;
	private String lon;
	private String lat;
	public AreaBean() {
		super();
	}
	public AreaBean(String code, String parentCode, int level, String name, String lon, String lat) {
		super();
		this.code = code;
		this.parentCode = parentCode;
		this.level = level;
		this.name = name;
		this.lon = lon;
		this.lat = lat;
	}
	public AreaBean(long id, String code, String parentCode, int level, String name, String lon, String lat) {
		super();
		this.id = id;
		this.code = code;
		this.parentCode = parentCode;
		this.level = level;
		this.name = name;
		this.lon = lon;
		this.lat = lat;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	@Override
	public String toString() {
		return "{\"code\":" + code + ", parentCode\":" + parentCode + ", level\":" + level + ", name\":" + name
				+ ", lon\":" + lon + ", lat\":" + lat + "}";
	}
	
}
