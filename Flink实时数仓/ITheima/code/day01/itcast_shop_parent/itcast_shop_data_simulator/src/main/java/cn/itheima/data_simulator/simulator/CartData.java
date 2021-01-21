package cn.itheima.data_simulator.simulator;

import cn.itheima.data_simulator.base.DBSimulator;
import cn.itheima.data_simulator.util.ConfigReader;
import cn.itheima.data_simulator.util.KafkaUtil;
import com.alibaba.fastjson.JSON;
import javafx.util.Pair;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CartData extends DBSimulator {

    @Override
    public void sendToKafka() throws SQLException {
        KafkaProducer producer = KafkaUtil.producer;
        String topic = ConfigReader.output_topic_cart;
        // 写入到kafka中
        ProducerRecord<String, String> record = null;

        //获取要写入的数据内容
        Pair<String, String> messagePair = getInputLine();

        try {
            record = new ProducerRecord<String, String>(topic,messagePair.getKey(), messagePair.getValue());
            producer.send(record);
            producer.flush();
            System.out.println("发送购物车日志消息>>> ("+ messagePair.getKey() + ", " + messagePair.getValue() + ")");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //获取商品信息列表
    public List<Long> getGoodsList(){
        List<Long> list = new ArrayList<Long>(16808);
        String sql = "SELECT * FROM itcast_goods ORDER BY GoodsId DESC LIMIT 0,10";
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getLong("goodsId"));
            }
            rs.close();
            statement.close();
        }  catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //获取access log日志
    @Override
    public Pair<String, String> getInputLine() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Long>  goodsList = getGoodsList();
        if(goodsList.size() == 0){
            return null;
        }

        //商品id
        Long goodsId;
        goodsId = goodsList.get(random.nextInt(goodsList.size()));

        Cart cart = new Cart();
        cart.setGoodsId(goodsId.toString());
        cart.setUserId("100208");
        cart.setCount(1);
        cart.setGuid(UUID.randomUUID().toString());
        cart.setIp(getIp());
        cart.setAddTime(System.currentTimeMillis());

        //构建点击流日志信息
        return new Pair<>(goodsId.toString(), JSON.toJSONString(cart)) ;
    }


    public static void main(String[] args) {
        CartData cartData = new CartData();
        //{"addTime":"Mon Dec 16 18:01:41 CST 2019","count":1,"goodsId":"100106","guid":"fd3e6beb-2ce3-4eda-bcaa-bc6221f85016","ip":"123.125.71.102","userId":"100208"}
        Cart cart = new Cart();
        cart.setGoodsId("115400");
        cart.setUserId("100208");
        cart.setCount(1);
        cart.setGuid(UUID.randomUUID().toString());
        cart.setIp(cartData.getIp());
        cart.setAddTime(System.currentTimeMillis());
        System.out.println(JSON.toJSONString(cart));
    }
}

class Cart {
    private String goodsId;     // 商品ID
    private String userId;      // 用户ID
    private Integer count;      // 商品数量
    private String guid;        // 客户端唯一识别号
    private Long addTime;       // 添加到购物车时间
    private String ip;          // ip地址

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
