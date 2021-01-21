package cn.itheima.data_simulator.simulator;

import cn.itheima.data_simulator.base.DBSimulator;
import cn.itheima.data_simulator.util.ConfigReader;
import cn.itheima.data_simulator.util.KafkaUtil;
import com.alibaba.fastjson.JSON;
import javafx.util.Pair;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public class CommentsData extends DBSimulator {
    private static final String[] COMMENTS_ARRAY = {
            "触摸感觉挺好，外观时尚，功能比较前卫。整体效果大方时尚",
            "外形外观：比较大",
            "外观不错，音效不错，性价比高，值得购买的一款机器",
            "电视不错，双十一买的。",
            "尺寸大小：看着刚刚好",
            "安装同步：安装小哥哥够速度",
            "功能效果：苹果原相机拍出来的 爽歪歪 清晰度超棒的诶 很是喜欢? 赶紧推荐身边朋友买 老品牌 值得信赖 护眼款",
            "小孩子爱看电视 保护眼睛 很好喔 爱可爱了爱了",
            "尺寸大小：大小正好 因为客厅小 买的是55寸 根据自家面积大小去选择合适的尺寸吧",
            "安装同步：电视机一到 预约的师傅货到了当天就来了 服务态度挺好的",
    };


    @Override
    public void sendToKafka() throws SQLException {
        KafkaProducer producer = KafkaUtil.producer;
        String topic = ConfigReader.output_topic_comments;
        // 写入到kafka中
        ProducerRecord<String, String> record = null;

        //获取要写入的数据内容
        Pair<String, String> messagePair = getInputLine();

        try {
            record = new ProducerRecord<String, String>(topic,messagePair.getKey(), messagePair.getValue());
            producer.send(record);
            producer.flush();
            System.out.println("发送评论日志消息>>> ("+ messagePair.getKey() + ", " + messagePair.getValue() + ")");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Pair<String, String> getInputLine() throws SQLException {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Pair<Integer, String> userInfo = getUserInfo();
        if(userInfo == null){
            return null;
        }

        Pair<Long, Long> orderGoodsInfo = getOrderGoodsInfo();

        Comments comments = new Comments();
        comments.setUserId(userInfo.getKey().toString());
        comments.setUserName(userInfo.getValue());
        comments.setOrderGoodsId(orderGoodsInfo.getKey().toString());
        comments.setGoodsId(orderGoodsInfo.getValue());
        comments.setStarScore(random.nextInt(1, 5));
        comments.setComments(COMMENTS_ARRAY[random.nextInt(COMMENTS_ARRAY.length)]);
        comments.setImageViedoJSON("[\"itcast.com/t1/99554/6/1122/267221/5dba725bE3a436c24/434bf88bc0a2a108.jpg\"]");
        comments.setTimestamp(System.currentTimeMillis());

        //System.out.println("评论信息>>>"+JSON.toJSONString(comments));
        //构建点击流日志信息
        return new Pair<>(userInfo.getKey().toString(), JSON.toJSONString(comments)) ;
    }

    //获取用户信息列表
    public Pair<Integer, String> getUserInfo() throws SQLException {
        Pair<Integer, String> userInfo = null;
        String sql = "SELECT * FROM itcast_users ORDER BY rand() LIMIT 1";
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                userInfo = new Pair<>(rs.getInt("userId"), rs.getString("userName"));
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    //获取订单明细信息列表
    public Pair<Long, Long> getOrderGoodsInfo() throws SQLException {
        Pair<Long, Long> goodsInfo = null;
        String sql = "SELECT * FROM itcast_order_goods ORDER BY rand() LIMIT 1";
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                goodsInfo = new Pair<>(rs.getLong("ogId"), rs.getLong("goodsId"));
            }
            rs.close();
            statement.close();
        }  catch (SQLException e) {
            e.printStackTrace();
        }
        return goodsInfo;
    }

    public static void main(String[] args) {
        Comments comments = new Comments();
        comments.setUserId("wstmart");
        comments.setUserName("100001");
        comments.setOrderGoodsId("1");
        comments.setStarScore(5);
        comments.setComments("双十一给我媳妇买的，收到货后，摄像头有异物，联系客服换货，处理速度很快，换了新的，这个紫色真的很好看，玻璃的感觉特别剔透，很适合女生用，媳妇很喜欢，功能强大双摄，双卡双待，广角，拍照清晰，夜景模式，无线充电，买了快充头，充电快，电池抗用，非常好用");
        comments.setImageViedoJSON("[\"itcast.com/t1/99554/6/1122/267221/5dba725bE3a436c24/434bf88bc0a2a108.jpg\",\"itcast.com/t1/99554/6/1122/267221/5dba725bE3a436c24/as8df989asd.jpg\"]");
        comments.setTimestamp(System.currentTimeMillis());

        System.out.println(JSON.toJSONString(comments));
    }
}

class Comments {
    private String userId;              // 用户ID
    private String userName;            // 用户名
    private String orderGoodsId;        // 订单明细ID
    private Integer starScore;          // 评分
    private String comments;            // 评论
    private Long goodsId;               // 商品id
    private String imageViedoJSON;      // 图片、视频JSON
    private Long timestamp;             // 评论时间

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderGoodsId() {
        return orderGoodsId;
    }

    public void setOrderGoodsId(String orderGoodsId) {
        this.orderGoodsId = orderGoodsId;
    }

    public Integer getStarScore() {
        return starScore;
    }

    public void setStarScore(Integer starScore) {
        this.starScore = starScore;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getImageViedoJSON() {
        return imageViedoJSON;
    }

    public void setImageViedoJSON(String imageViedoJSON) {
        this.imageViedoJSON = imageViedoJSON;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}