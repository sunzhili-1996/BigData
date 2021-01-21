package cn.itcast.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis工具类
 * 
 * @author mengyao
 * @date 2019-05-28
 */
public class RedisUtil implements Serializable {
	
	private static final long serialVersionUID = -8924898642817729421L;
	private static JedisPool jedisPool;
	private static String host = "localhost";
	private static int port = 6379;
	private static int timeout = 10*1000;
	private Jedis client;

	
	private RedisUtil() {
	}
	
	public static RedisUtil build() {
		return build(host, port, timeout);
	}
	
	public static RedisUtil build(String host, int port) {
		return build(host, port, timeout);
	}
	
	public static RedisUtil build(String host, int port, int timeout) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(1024);
		config.setMaxIdle(10);
		config.setMinIdle(10);
		config.setMaxWaitMillis(1000);
		config.setMaxWaitMillis(1000);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);
		jedisPool = new JedisPool(config,host,port,timeout);
		return new RedisUtil();
	}
	
	private Jedis getClient() {
		if (client == null || !client.isConnected()) {
			this.client = jedisPool.getResource();			
		}
		return client;
	}
	
	public void close() {
		if (!jedisPool.isClosed()) {
			jedisPool.close();
		}
	}
	
	/**
	 * 删除key
	 * 
	 * @param key
	 */
	public void delete(String key) {
		getClient().del(key);
	}

	/**
	 * 批量删除key
	 * 
	 * @param keys
	 */
	public void delete(Collection<String> keys) {
		getClient().del((String[])keys.toArray());
	}

	/**
	 * 序列化key
	 * 
	 * @param key
	 * @return
	 */
	public byte[] dump(String key) {
		return getClient().dump(key);
	}

	/**
	 * 是否存在key
	 * 
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key) {
		return getClient().exists(key);
	}

	/**
	 * 设置过期时间
	 * 
	 * @param key
	 * @param timeout: seconds
	 * @return
	 */
	public Long expire(String key, int timeout) {
		return getClient().expire(key, timeout);
	}

	/**
	 * 设置过期时间
	 * 
	 * @param key
	 * @param date
	 * @return
	 */
	public Long expireAt(String key, long milliseconds) {
		return getClient().expireAt(key, milliseconds);
	}

	/**
	 * 查找匹配的key
	 * 
	 * @param pattern
	 * @return
	 */
	public Set<String> keys(String pattern) {
		return getClient().keys(pattern);
	}

	/**
	 * 将当前数据库的 key 移动到给定的数据库 db 当中
	 * 
	 * @param key
	 * @param dbIndex
	 * @return
	 */
	public Long move(String key, int dbIndex) {
		return getClient().move(key, dbIndex);
	}

	/**
	 * 移除 key 的过期时间，key 将持久保持
	 * 
	 * @param key
	 * @return
	 */
	public Long persist(String key) {
		return getClient().persist(key);
	}

	/**
	 * 从当前数据库中随机返回一个 key
	 * 
	 * @return
	 */
	public String randomKey() {
		return getClient().randomKey();
	}

	/**
	 * 修改 key 的名称
	 * 
	 * @param oldKey
	 * @param newKey
	 */
	public void rename(String oldKey, String newKey) {
		getClient().rename(oldKey, newKey);
	}

	/**
	 * 返回 key 所储存的值的类型
	 * 
	 * @param key
	 * @return
	 */
	public String type(String key) {
		return getClient().type(key);
	}

	/** -------------------string相关操作--------------------- */

	/**
	 * 设置指定 key 的值
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		getClient().set(key, value);
	}

	/**
	 * 获取指定 key 的值
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return getClient().get(key);
	}

	/**
	 * 返回 key 中字符串值的子字符
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public String getRange(String key, long start, long end) {
		return getClient().getrange(key, start, end);
	}

	/**
	 * 获取正则匹配的key集合
	 * @param keyPattern
	 * @return
	 */
	public Set<String> getKeys(String keyPattern) {
		return getClient().keys(keyPattern);
	}
	
	/**
	 * 获取匹配key的value集合
	 * @param keys
	 * @return
	 */
	public List<String> getValues(Collection<String> keys) {
		return getClient().mget(keys.toArray(new String[] {}));
	}
	
	/**
	 * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String getAndSet(String key, String value) {
		return getClient().getSet(key, value);
	}

	/**
	 * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)
	 * 
	 * @param key
	 * @param offset
	 * @return
	 */
	public Boolean getBit(String key, long offset) {
		return getClient().getbit(key, offset);
	}

	/**
	 * 批量获取
	 * 
	 * @param keys
	 * @return
	 */
	public List<String> multiGet(Collection<String> keys) {
		return getClient().mget((String[])keys.toArray());
	}

	/**
	 * 设置ASCII码, 字符串'a'的ASCII码是97, 转为二进制是'01100001', 此方法是将二进制第offset位值变为value
	 * 
	 * @param key
	 * @param postion
	 *            位置
	 * @param value
	 *            值,true为1, false为0
	 * @return
	 */
	public boolean setBit(String key, long offset, boolean value) {
		return getClient().setbit(key, offset, value);
	}

	/**
	 * 将值 value 关联到 key ，并将 key 的过期时间设为 timeout
	 * 
	 * @param key
	 * @param value
	 * @param timeout
	 *            过期时间
	 * @param unit
	 *            时间单位, 天:TimeUnit.DAYS 小时:TimeUnit.HOURS 分钟:TimeUnit.MINUTES
	 *            秒:TimeUnit.SECONDS 毫秒:TimeUnit.MILLISECONDS
	 */
	public void setEx(String key, String value, long timeout) {
		getClient().psetex(key, timeout, value);
	}

	/**
	 * 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始
	 * 
	 * @param key
	 * @param value
	 * @param offset
	 *            从指定位置开始覆写
	 */
	public void setRange(String key, String value, long offset) {
		getClient().setrange(key, offset, value);
	}

	/**
	 * 获取字符串的长度
	 * 
	 * @param key
	 * @return
	 */
	public Long size(String key) {
		return getClient().hlen(key);
	}

	/**
	 * 批量添加
	 * 
	 * @param maps
	 */
	public void multiSet(Map<String, String> maps) {
		maps.forEach((k,v)->{
			getClient().set(k,v);			
		});
	}

	/**
	 * 增加(自增长), 负数则为自减
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long incrBy(String key, long increment) {
		return getClient().incrBy(key, increment);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Double incrByFloat(String key, double increment) {
		return getClient().incrByFloat(key, increment);
	}

	/**
	 * 追加到末尾
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long append(String key, String value) {
		return getClient().append(key, value);
	}

	/** -------------------hash相关操作------------------------- */

	/**
	 * 获取存储在哈希表中指定字段的值
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public Object hGet(String key, String field) {
		return getClient().hget(key, field);
	}

	/**
	 * 获取Set集合数量
	 * @param key
	 * @return
	 */
	public Long sCard(String key) {
		return getClient().scard(key);
	}

	/**
	 * 获取所有给定字段的值
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, String> hGetAll(String key) {
		return getClient().hgetAll(key);
	}

	/**
	 * 获取所有给定字段的值
	 * 
	 * @param key
	 * @param fields
	 * @return
	 */
	public List<String> hMultiGet(String key, Collection<Object> fields) {
		return getClient().hmget(key, (String[])fields.toArray());
	}

	public void hPut(String key, String hashKey, String value) {
		getClient().hset(key, hashKey, value);
	}

	public void hPutAll(String key, Map<String, String> maps) {
		getClient().hmset(key, maps);
	}
	
	public static void main(String[] args) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(1024);
		config.setMaxIdle(10);
		config.setMaxWaitMillis(1000);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);
		jedisPool = new JedisPool(config,"bigdata-cdh01",6379,10000);
		Jedis client = jedisPool.getResource();
//		System.out.println(client.get("q_visitor"));
//		System.out.println(client.mget("q_visitor"));
		System.out.println(client.keys("*"));
		Set<String> keys = client.keys("dim_og_*");
		String[] a=new String[]{};
		String[] array = keys.toArray(a);
		System.out.println(client.mget(array));
		System.out.println(keys);
//		System.out.println(client.hgetAll("q_visitor"));
		
		client.close();
		jedisPool.close();
		
	}

}