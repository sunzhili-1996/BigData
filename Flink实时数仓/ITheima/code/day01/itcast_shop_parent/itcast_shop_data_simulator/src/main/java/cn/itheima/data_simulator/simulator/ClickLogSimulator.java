package cn.itheima.data_simulator.simulator;

import cn.itheima.data_simulator.base.LogSimulator;
import cn.itheima.data_simulator.util.ConfigReader;
import cn.itheima.data_simulator.util.KafkaUtil;
import javafx.util.Pair;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * cd8faac9-9f9c-4844-83e5-cdb764876e13 210.30.51.226 - [19/Dec/2019:03:45:39 +0800] "GET /linux/installing-my-new-server/vmware-server HTTP/1.1" 502 2811 "https:////www.sina.com.cn" "Mozilla/5.0 (Windows NT 6.1; rv:32.0) Gecko/20100101 Firefox/32.0"
 */
public class ClickLogSimulator extends LogSimulator {
    private static final String[] REFER_DOMAIN_ARRAY = {
            "\"http://www.search.cn\"",        // 搜索页面
            "\"https://www.itcast.cn\"",                // 首页
            "\"https://www.baidu.com\"",                // 百度
            "\"https://www.zhihu.com\"",                // 知乎
            "\"https:////www.sina.com.cn\"",            // 新浪
            "\"https://weibo.com\"",                    // 微博
            "\"https://music.163.com\"",                // 网易云音乐
            "\"https://y.qq.com\"",                     // QQ音乐
            "\"https://www.douyin.com\"",               // 抖音
            "\"https://www.toutiao.com\"",              // 今日头条
    };

    //请求资源信息
    private static final String[] REQUESTS = {
            "\"POST /join_form HTTP/1.1\"",
            "\"GET /join_form HTTP/1.1\"",
            "\"POST /login_form HTTP/1.1\"",
            "\"GET /linux/installing-my-new-server/vmware-server HTTP/1.1\"",
            "\"GET /acl_users/credentials_cookie_auth/require_login?came_from=http%3A//howto.basjes.nl/join_form HTTP/1.1\"",
            "\"GET /login_form HTTP/1.1\"",
            "\"POST /login_form HTTP/1.1\""
    };

    //请求状态
    private static final String[] REQUEST_STATUS = {
            "200",
            "404",
            "502"
    };

    //用户访问信息
    private static final String[] USER_AGENTS = {
            "\"Mozilla/5.0 (Windows NT 6.1; rv:32.0) Gecko/20100101 Firefox/32.0\"",
            "\"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Dragon/36.1.1.21 Chrome/36.0.1985.97 Safari/537.36\""
    };

    public static void main(String[] args) {
        ClickLogSimulator clickLogSimulator = new ClickLogSimulator();
        System.out.println(clickLogSimulator.getInputLine());
    }

    //获取access log日志
    public Pair<String, String> getInputLine() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String guid = getGUID();

        //构建点击流日志信息
        return new Pair<>(guid, getGUID() + " "
                + getIp() + " "
                + "-" + " "
                + "["+getTrackTime()+"]" + " "
                + REQUESTS[random.nextInt(REQUESTS.length)] + " "
                + REQUEST_STATUS[random.nextInt(REQUEST_STATUS.length)] + " "
                + random.nextInt(1, 10000) + " "
                + REFER_DOMAIN_ARRAY[random.nextInt(REFER_DOMAIN_ARRAY.length)] + " "
                + USER_AGENTS[random.nextInt(USER_AGENTS.length)]) ;
    }

    @Override
    public void sendToKafka() throws SQLException {
        KafkaProducer producer = KafkaUtil.producer;
        String topic = ConfigReader.output_topic_clicklog;
        // 写入到kafka中
        ProducerRecord<String, String> record = null;

        //获取要写入的数据内容
        Pair<String, String> messagePair = getInputLine();

        try {
            record = new ProducerRecord<String, String>(topic,messagePair.getKey(), messagePair.getValue());
            producer.send(record);
            producer.flush();
            System.out.println("发送点击流日志消息>>> ("+ messagePair.getKey() + ", " + messagePair.getValue() + ")");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //获取访问时间
    public String getTrackTime(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    /**
     * 获取用户id
     * @return
     */
    public String getGUID(){
        String guid = UUID.randomUUID().toString();
        return guid;
    }

    //获取ip
    public String getIp() {
        // ip范围
        int[][] range = {{607649792, 608174079}, // 36.56.0.0-36.63.255.255
                {1038614528, 1039007743}, // 61.232.0.0-61.237.255.255
                {1783627776, 1784676351}, // 106.80.0.0-106.95.255.255
                {2035023872, 2035154943}, // 121.76.0.0-121.77.255.255
                {2078801920, 2079064063}, // 123.232.0.0-123.235.255.255
                {-1950089216, -1948778497}, // 139.196.0.0-139.215.255.255
                {-1425539072, -1425014785}, // 171.8.0.0-171.15.255.255
                {-1236271104, -1235419137}, // 182.80.0.0-182.92.255.255
                {-770113536, -768606209}, // 210.25.0.0-210.47.255.255
                {-569376768, -564133889}, // 222.16.0.0-222.95.255.255
        };

        Random rdint = new Random();
        int index = rdint.nextInt(10);
        String ip = num2ip(range[index][0] + new Random().nextInt(range[index][1] - range[index][0]));
        return ip;
    }

    /*
     * 将十进制转换成IP地址
     */
    public String num2ip(int ip) {
        int[] b = new int[4];
        String x = "";
        b[0] = (int) ((ip >> 24) & 0xff);
        b[1] = (int) ((ip >> 16) & 0xff);
        b[2] = (int) ((ip >> 8) & 0xff);
        b[3] = (int) (ip & 0xff);
        x = Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "." + Integer.toString(b[3]);
        return x;
    }
}
