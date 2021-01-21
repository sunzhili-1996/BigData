package cn.itheima.data_simulator.simulator;

import cn.itheima.data_simulator.base.DBSimulator;
import cn.itheima.data_simulator.util.DateUtil;
import cn.itheima.data_simulator.util.OrderGoodUtil;

import java.sql.SQLException;
import java.util.Random;

public class OrderGoodsData extends DBSimulator {

    @Override
    public void sendToKafka() throws SQLException {
        String startDate = DateUtil.timeToDateString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        String finishDate = DateUtil.timeToDateString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        // 固定订单数
        int FIX_ORDER_NUM = 1;
        //随机订单数
        int randomOrderNum = 2;
        markOrderGoods(startDate, finishDate, FIX_ORDER_NUM, randomOrderNum);
    }

    //生成订单数据
    public void markOrderGoods(String startDate, String finishDate, int FIX_ORDER_NUM, int randomOrderNum) {
        Long createTime = DateUtil.DateStringToTime("yyyy-MM-dd", startDate);
        Long finishTime = DateUtil.DateStringToTime("yyyy-MM-dd", finishDate);
        Long endTime = 0L;
        int orderNum = 10;
        String createDate = null;
        String endDate = null;
        Long currenttime = System.currentTimeMillis();
        //System.out.println("start time : " + currenttime);
        OrderGoodUtil orderGoods = null;
        Random r = new Random();

        while (createTime <= finishTime) {
            long startTime4Day = System.currentTimeMillis();
            endTime = createTime + 86400000L;
            createDate = DateUtil.timeToDateString(createTime, "yyyy-MM-dd");
            endDate = DateUtil.timeToDateString(endTime, "yyyy-MM-dd");

            orderNum = FIX_ORDER_NUM + r.nextInt(randomOrderNum);

            if (orderGoods != null) {
                orderGoods.setCreateDate(createDate);
                orderGoods.setEndDate(endDate);
                orderGoods.setRowNum(orderNum);
            } else {
                orderGoods = new OrderGoodUtil(createDate, endDate, orderNum);
            }

            // 生成订单
            orderGoods.insertOrder();

//            // 生成订单明细
//            orderGoods.insertOrderGoods();
//
//            // 更改订单详情的创建时间
//            orderGoods.updateOrderGoodsCreateTime();
//
//            // 更改订单中商品价格与购买数量
//            orderGoods.updateOrderGoodsPriceAndNum();
//
//            // 修改订单明细的总价格
//            orderGoods.updateOrderGoodsMoney();
//
//            //orderGoods.getOrderMoney();
//
//            // 修改订单付款的付款价格
//            orderGoods.updateOrderTotalMoney();
//
//            // 修改订单付款的支付方式
//            orderGoods.updateOrderpaytype();
//
//            // 修改订单配送地址
//            orderGoods.updateOrderAddress();

            createTime = endTime;
            System.out.println("发送订单明细日志消息>>>当前订单创建日期 ： " + createDate + "    当前订单结束日期 : " + endDate + " 共计生成订单数量为 : " + orderNum + " 运行时长 ： " + (System.currentTimeMillis() - startTime4Day));
        }
        Long finishprocesstime = System.currentTimeMillis();
//        System.out.println("end time : " + finishprocesstime);
//        System.out.println(" run time : " + (finishprocesstime - currenttime));
    }
}
