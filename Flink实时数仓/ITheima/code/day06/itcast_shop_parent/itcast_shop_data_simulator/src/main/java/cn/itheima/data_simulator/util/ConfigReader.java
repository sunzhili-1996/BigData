package cn.itheima.data_simulator.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件
 */
public class ConfigReader {
    private static Properties properties = new Properties();

    static {
        InputStream inputStream = Object.class.getResourceAsStream("/application.conf");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String mysql_driver = properties.get("mysql.driver").toString();
    public static String mysql_server = properties.get("mysql.server").toString();
    public static String mysql_port = properties.get("mysql.port").toString();
    public static String mysql_database = properties.get("mysql.database").toString();
    public static String mysql_username = properties.get("mysql.username").toString();
    public static String mysql_password = properties.get("mysql.password").toString();
    public static  String kafka_bootstrap_servers = properties.get("kafka.bootstrap.servers").toString();
    public static  String output_topic_cart = properties.get("output.topic.cart").toString();
    public static  String output_topic_clicklog = properties.get("output.topic.clicklog").toString();
    public static  String output_topic_comments = properties.get("output.topic.comments").toString();


    public static void main(String[] args) {
        System.out.println(mysql_driver);
        System.out.println(mysql_server);
        System.out.println(mysql_port);
        System.out.println(mysql_database);
        System.out.println(mysql_username);
        System.out.println(mysql_password);
        System.out.println(kafka_bootstrap_servers);
    }
}
