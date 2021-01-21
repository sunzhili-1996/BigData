package cn.itheima.data_simulator.base;

import java.sql.SQLException;

public interface Simulator {

    //设置延迟时间
    Long delay= 1000L;

    /**
     * 单条生成
     * @return JSON字符串
     */
    void sendToKafka() throws SQLException, InterruptedException;
}
