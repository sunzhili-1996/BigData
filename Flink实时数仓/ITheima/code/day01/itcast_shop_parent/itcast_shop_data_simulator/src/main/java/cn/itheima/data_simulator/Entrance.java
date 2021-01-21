package cn.itheima.data_simulator;

import cn.itheima.data_simulator.simulator.*;
import cn.itheima.data_simulator.util.OrderGoodUtil;
import org.junit.Test;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.sql.SQLException;

/**
 * 数据模拟器
 */
@Component
@EnableScheduling
public class Entrance {
    //生成一条数据
    @Test
    public void sendSingleSim() throws SQLException {
        // 1. 生成点击流访问日志
        ClickLogSimulator clickLogSimulator= new ClickLogSimulator();
        //生成一条数据
        clickLogSimulator.sendToKafka();
//
//        // 2. 生成添加到购物车消息
//        CartData cartData = new CartData();
//        //生成一条数据
//        cartData.sendToKafka();
//
//        // 3. 商品消息
//        GoodsData goodsData = new GoodsData();
//        goodsData.sendToKafka();
//
//        // 4. 买家评价消息
//        CommentsData commentsData = new CommentsData();
//        //生成一条数据
//        commentsData.sendToKafka();

//        //5. 订单明细数据
//        String startDate = "2020-03-01 00:00:00";
//        String finishDate = "2020-03-30 00:00:00";
//
//        OrderData orderData = new OrderData(startDate, finishDate);
//        orderData.sendToKafka();
    }

//    //批量生成数据
//    @Test
//    @Scheduled(cron = "00/1 * * * * ?")
//    public void sendBatchClickLogSim() throws SQLException {
//        // 1. 生成点击流访问日志
//        ClickLogSimulator clickLogSimulator= new ClickLogSimulator();
//        //批量生成数据
//        clickLogSimulator.sendToKafka();
//    }

//    //模拟生成购物车数据
//    @Test
//    @Scheduled(cron = "00/1 * * * * ?")
//    public void sendBatchCartSim() throws SQLException {
//        // 2. 生成添加到购物车消息
//        CartData cartData = new CartData();
//        //批量生成数据
//        cartData.sendToKafka();
//    }

//    //模拟生成商品消息
//    @Test
//    @Scheduled(cron = "00/1 * * * * ?")
//    public void sendBatchGoodsSim() throws SQLException {
//        // 3. 商品消息
//        GoodsData goodsData = new GoodsData();
//        goodsData.sendToKafka();
//    }
//
//    //模拟生成买家评价消息
//    @Test
//    @Scheduled(cron = "00/1 * * * * ?")
//    public void sendBatchCommentsSim() throws SQLException {
//        // 4. 买家评价消息
//        CommentsData commentsData = new CommentsData();
//        //批量生成数据
//        commentsData.sendToKafka();
//    }
//
    //生成订单数据
    @Test
    @Scheduled(cron = "00/1 * * * * ?")
    public  void sendOrderData() throws InterruptedException {
        String startDate = "2020-03-01 00:00:00";
        String finishDate = "2020-03-23 00:00:00";

        OrderData orderData = new OrderData(startDate, finishDate);
        orderData.sendToKafka();
    }

}
