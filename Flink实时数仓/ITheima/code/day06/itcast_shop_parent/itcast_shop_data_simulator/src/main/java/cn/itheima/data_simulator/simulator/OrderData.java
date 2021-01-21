package cn.itheima.data_simulator.simulator;

import cn.itheima.data_simulator.base.LogSimulator;
import cn.itheima.data_simulator.util.ConfigReader;
import cn.itheima.data_simulator.util.DateUtil;
import cn.itheima.data_simulator.util.TimeDifference;

import java.sql.*;
import java.util.Date;
import java.util.Random;

public class OrderData extends LogSimulator {
    private static Connection conn;
    private static String driver = ConfigReader.mysql_driver;
    private static String url = "jdbc:mysql://" + ConfigReader.mysql_server
            + ":" + ConfigReader.mysql_port
            + "/" + ConfigReader.mysql_database
            + "?useSSL=false&characterEncoding=UTF-8";
    private static String user = ConfigReader.mysql_username;
    private static String password = ConfigReader.mysql_password;
    private PreparedStatement ps;
    private Statement statement = null;
    private ResultSet rs = null;


    private String startDate = "2020-03-01";
    private String finishDate = "2020-03-30";

    static {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造方法
     * @param startDate
     * @param finishDate
     */
    public OrderData(String startDate, String finishDate) {
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    @Override
    public void sendToKafka() {
        TimeDifference timeDifference = new TimeDifference();
        Date date = timeDifference.randomDate(this.startDate,this.finishDate);
        insertOrder(date);
    }

    /**
     * 插入一批数据
     *
     * createtime 2019-09-07 13:31:49
     * payTime
     */
    public void insertOrder(Date createDate) {
        String sql = "INSERT INTO itcast_shop.itcast_orders (orderNo,userId,goodsMoney,totalMoney,areaId,userName,userAddress,orderunique,createTime,payTime,isFromCart,platform,realTotalMoney,isPay) VALUES(?,?,'0.12','0.12',?,'徐牧','江苏省 无锡市 北塘区','8021250f-0e82-48dd-bc23-d9cb2908bfc0',?,?,?,?,?,?)";

        Random random = new Random();
        Long createTime = createDate.getTime();
        Long payTime = 0L;
        String createDateString = null;
        String payDateString = null;

        try {
            ps = (PreparedStatement) conn.prepareStatement(sql);
            createDateString = DateUtil.timeToDateString(createTime,"yyyy-MM-dd HH:mm:ss");

            // 获取结束时间
            payTime = random.nextInt(3600000) + createTime;
            payDateString = DateUtil.timeToDateString(payTime,"yyyy-MM-dd HH:mm:ss");
            ps.setInt(1, random.nextInt(1000));
            ps.setString(2, String.valueOf(random.nextInt(34)));
            ps.setInt(3, random.nextInt(100));
            ps.setString(4, createDateString);
            ps.setString(5, payDateString);
            ps.setInt(6, random.nextInt(2));
            ps.setInt(7, random.nextInt(6));
            ps.setDouble(8, Double.valueOf(random.nextInt(1000)));
            ps.setInt(9, random.nextInt(2));
            ps.execute();

            // 获取插入的订单Id
            String sqlLastInsertId = "SELECT LAST_INSERT_ID()";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlLastInsertId);
            while(resultSet.next()) {
                int orderId = resultSet.getInt(1);
                System.out.println("生成一条新的订单，订单编号>>>"+orderId);
            }
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }
}
