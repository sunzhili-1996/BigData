package cn.itcast.nginx.httpdlog.javapojo;

import com.typesafe.config.ConfigException;
import nl.basjes.parse.core.Parser;
import nl.basjes.parse.core.exceptions.DissectionFailure;
import nl.basjes.parse.core.exceptions.InvalidDissectorException;
import nl.basjes.parse.core.exceptions.MissingDissectorsException;
import nl.basjes.parse.httpdlog.HttpdLoglineParser;

/**
 * 实现案例
 */
public class HttpdLogPasingDemo {
    /**
     * 定义日志的解析规则
     */
    public static String getLogFormat(){
        //return "%u %h %l %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\" \"%{Cookie}i\" \"%{Addr}i\"";
        return  "%u %h %l %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\"";
    }

    /**
     * 定义需要解析的数据样本
     * @return
     */
    public static String getInputLine(){
        //return "2001-980:91c0:1:8d31:a232:25e5:85d 222.68.172.190 - [05/Sep/2010:11:27:50 +0200] \"GET /images/my.jpg HTTP/1.1\" 404 23617 \"http://www.angularjs.cn/A00n\" \"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; nl-nl) AppleWebKit/533.17.8 (KHTML, like Gecko) Version/5.0.1 Safari/533.17.8\" \"jquery-ui-theme=Eggplant; BuI=SomeThing; Apache=127.0.0.1.1351111543699529\" \"beijingshi\"";
            return "2001:980:91c0:1:8d31:a232:25e5:85d 222.68.172.190 - [05/Sep/2010:11:27:50 +0200] \"GET /images/my.jpg HTTP/1.1\" 404 23617 \"http://www.angularjs.cn/A00n\" \"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; nl-nl) AppleWebKit/533.17.8 (KHTML, like Gecko) Version/5.0.1 Safari/533.17.8\"";

    }

    /**
     * 根据定义的日志规则解析数据
     */
    public void run() throws InvalidDissectorException, MissingDissectorsException, DissectionFailure, NoSuchMethodException {
        //创建一个日志的解析器
        Parser<HttpdLogRecord> parser = new HttpdLoglineParser<>(HttpdLogRecord.class, getLogFormat());
        //建立解析出来的参数名与方法名的映射关系
        parser.addParseTarget("setConnectionClientHost", "IP:connection.client.host");
        parser.addParseTarget("setConnectionClientUser", "STRING:connection.client.user");
        parser.addParseTarget("setMethod", "HTTP.METHOD:request.firstline.original.method");
        parser.addParseTarget("setStatus", "STRING:request.status.last");

        Main main = new Main();
        main.printAllPossibles(getLogFormat());
        //实例化bean对象
        HttpdLogRecord record = new HttpdLogRecord();

        //根据定义的解析规则解析样本数据
        parser.parse(record, getInputLine());
        System.out.println(record.getConnectionClientHost());
        System.out.println(record.getConnectionClientUser());
        System.out.println(record.getMethod());
        System.out.println(record.getStatus());
    }
    public static void main(String[] args) throws MissingDissectorsException, NoSuchMethodException, DissectionFailure, InvalidDissectorException {
            new HttpdLogPasingDemo().run();
    }
}
