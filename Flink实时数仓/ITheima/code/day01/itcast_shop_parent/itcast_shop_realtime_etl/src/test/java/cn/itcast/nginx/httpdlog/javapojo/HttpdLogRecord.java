package cn.itcast.nginx.httpdlog.javapojo;

import lombok.Getter;
import lombok.Setter;

/**
 * 这个bean就是解析log参数以后，赋值给的对象
 * 定义属性，属性的数量跟业务需要有关
 */
public class HttpdLogRecord {
    /**
     * @Getter @Setter
     * 表示生成属性的set和get方法
     */
    @Getter @Setter private String connectionClientUser = null;
    @Getter @Setter private String connectionClientHost = null;
    @Getter @Setter private String method = null;
    @Getter @Setter private String status = null;
}
