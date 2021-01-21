package cn.itheima.data_simulator;

import cn.itheima.data_simulator.util.DateUtil;
import cn.itheima.data_simulator.util.OrderGoodUtil;

import java.util.Random;

public class OrderGoodsMain {

    /**
     * 生成订单数量 = 固定订单数 + 浮动订单数
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        new OrderGoodsMain().run();
    }

    /**
     * 生成订单数据
     */
    public void run() throws InterruptedException {
        String startDate = "2020-03-01";
        String finishDate = "2020-03-30";

        // 固定订单数
        int FIX_ORDER_NUM = 1000;
        int orderNum = 10;
        int randomOrderNum = 1;
        Long createTime = DateUtil.DateStringToTime("yyyy-MM-dd", startDate);
        Long finishTime = DateUtil.DateStringToTime("yyyy-MM-dd", finishDate);
        Long endTime = 0L;
        String createDate = null;
        String endDate = null;
        Long currenttime = System.currentTimeMillis();
//        System.out.println("start time : " + currenttime);
        OrderGoodUtil orderGoods = null;
        Random r = new Random();

        while (createTime <= finishTime) {
            long startTime4Day = System.currentTimeMillis();
            endTime = createTime + 1000*60*60*24; //86400000L
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

            // 生成订单明细
            //orderGoods.insertOrderGoods();
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
            // 修改订单付款的付款价格
//            orderGoods.updateOrderTotalMoney();
//
//            // 修改订单付款的支付方式
//            orderGoods.updateOrderpaytype();
//
//            // 修改订单配送地址
//            orderGoods.updateOrderAddress();

            createTime = endTime;

            System.out.println(" 当前订单创建日期 ： " + createDate + "    当前订单结束日期 : " + endDate + " 共计生成订单数量为 : " + orderNum + " 运行时长 ： " + (System.currentTimeMillis() - startTime4Day));

            Thread.sleep(1000);
        }
        Long finishprocesstime = System.currentTimeMillis();
//        System.out.println("end time : " + finishprocesstime);
//        System.out.println(" run time : " + (finishprocesstime - currenttime));
    }
}
