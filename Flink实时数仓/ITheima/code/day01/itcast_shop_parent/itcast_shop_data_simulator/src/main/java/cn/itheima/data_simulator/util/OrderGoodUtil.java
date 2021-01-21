package cn.itheima.data_simulator.util;

import cn.itheima.data_simulator.bean.OrderObject;
import org.apache.commons.lang3.RandomUtils;
import java.sql.*;
import java.util.*;

/**
 * 生成器订单明细表
 * 订单与商品关系表
 *
 * 第一步，向itcast_orders 中插入数据, 指定订单的生成时间
 * 第二步，向 itcast_order_goods 中插入数据
 *
 * 第三步，更改订单金额
 *
 * 第四步，更改订单支付时间
 * 随机指定订单的支付类型
 *
 * 第三步，修改订单金额
 *
 */
public class OrderGoodUtil {

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
    private List<Integer> goodsIdList = null;
    private List<Integer> orderIdList = new ArrayList<Integer>();
    private List<Integer> shopIdList = null;
    private Map<Integer,List<Integer>> addressMap = null;

    // 创建日期
    private String createDate;

    // 结束日期
    private String endDate;

    // 生成订单的数量
    private int rowNum = 50000;

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public void setCreateDate(String createDate){
        this.createDate = createDate;
    }

    public void setEndDate(String endDate){
        this.endDate = endDate;
    }

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

    public OrderGoodUtil(String createDate,String endDate,int rowNum){
        this.createDate = createDate;
        this.endDate = endDate;
        this.rowNum = rowNum;

        goodsIdList = getGoods();
        orderIdList = getOrderIdList();
        shopIdList = getShopIdList();
        addressMap = getAdds();
    }

    public int getGoodsId(){
        Random r = new Random();
        r.nextInt(100);
        int val = r.nextInt(100)+100100;
//        System.out.println(val);
        return val;
    }

    public List<Integer> getGoods(){
        List<Integer> list = new ArrayList<Integer>(16808);

        String sql = "select goodsId from itcast_goods";

        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信息,元数据
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        }
        //System.out.println(list.size());

        return list;
    }

    public int getOrderId(){
        Random r = new Random();
        r.nextInt(100);
        int val = r.nextInt(100)+100100;
        return val;
    }

    public List<Integer> userId(){

        List<Integer> userIds = new ArrayList<Integer>();



        return userIds;
    }

    public List<OrderObject> getOrderMoney(){
        List<OrderObject> list = new ArrayList<OrderObject>(140000);

        String sql = "select orderId,sum(payPrice) from itcast_order_goods WHERE createtime>='"+createDate+"' and createtime<'"+endDate+"'  group by orderId";

        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信息,元数据
            while (rs.next()) {
                list.add(new OrderObject(rs.getLong(1),rs.getDouble(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    /**
     *
     * @return
     */
    public List<Integer> getOrderIdList(){

        List<Integer> list = new ArrayList<Integer>(25000);

        Long createTime = DateUtil.DateStringToTime("yyyy-MM-dd",createDate);

        Long endTime = createTime + 86400000L;

        String endDate = DateUtil.timeToDateString(endTime,"yyyy-MM-dd");

        String sql = "select orderId from itcast_orders where createTime>'"+ createDate +"' AND createTime<'"+endDate+"'";
//        String sql = "select orderId from itcast_orders";

//        System.out.println(" get orderids sql : " + sql);

        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信息,元数据
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        System.out.println(list.size());

        return list;
    }

    public List<Integer> getShopIdList(){

        List<Integer> list = new ArrayList<Integer>(25000);

        String sql = "select shopId from itcast_shops";

        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信息,元数据
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        System.out.println(list.size());

        return list;
    }

    /**
     * 随机获取shopId
     */
    public int getRandomShopId(){
        Random r = new Random();
        r.nextInt(5266);
        int val = r.nextInt(5266)+100050;

        return val;
    }

    public Map<Integer,List<Integer>> getAdds(){

        Map<Integer,List<Integer>> adds = new HashMap<Integer,List<Integer>>();

        String sql = "select userId,addressId from itcast_user_address";

        List<Integer> adlist = null;
        int oldUserId = 0;
        int newUserId = 0;

        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);

            while (rs.next()) {
                newUserId = rs.getInt(1);

                if(oldUserId==newUserId){
                    adlist.add(rs.getInt(2));
                }else {
                    adds.put(oldUserId,adlist);
                    adlist = new ArrayList<Integer>();
                    adlist.add(rs.getInt(2));
                    oldUserId = newUserId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        adds.remove(0);

        return adds;

    }

    public void updateOrderShopId(){

        String sql = "update itcast_orders set shopId=? where orderId=?";

        List<Integer> orders = orderIdList;
        List<Integer> shops = shopIdList;

        Random r = new Random();
        r.nextInt(12);
        int val = r.nextInt(12);

        try {
            ps = (PreparedStatement) conn.prepareStatement(sql);

            for(int orderId:orders){
                val = r.nextInt(12);
                ps.setInt(1, shops.get(val));
                ps.setInt(2,orderId);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }



    /**
     * 插入一批数据
     *
     * createtime 2019-09-07 13:31:49
     * payTime
     */
    public void insertOrder(){

        String sql = "INSERT INTO itcast_shop.itcast_orders (orderNo,userId,goodsMoney,totalMoney,areaId,userName,userAddress,orderunique,createTime,payTime,isFromCart,platform,realTotalMoney,isPay) VALUES(?,?,'0.12','0.12',?,'徐牧','江苏省 无锡市 北塘区','8021250f-0e82-48dd-bc23-d9cb2908bfc0',?,?,?,?,?,?)";
        Long dateTime = DateUtil.DateStringToTime("yyyy-MM-dd",createDate);
        Random random = new Random();
        Long createTime = 0L;
        Long payTime = 0L;
        String createDateString = null;
        String payDateString = null;

        try {
            ps = (PreparedStatement) conn.prepareStatement(sql);

            for(int i=0;i<1;i++){

                // 获取创建时间
                createTime = random.nextInt(50400000) + 28800000L +  dateTime;
                createDateString = DateUtil.timeToDateString(createTime,"yyyy-MM-dd HH:mm:ss");

                // 获取结束时间
                payTime = random.nextInt(3600000) + createTime;
                payDateString = DateUtil.timeToDateString(payTime,"yyyy-MM-dd HH:mm:ss");
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, String.valueOf(random.nextInt(34)));
                ps.setInt(3, random.nextInt(100));
                ps.setString(4, createDateString);
                ps.setString(5, payDateString);
                ps.setInt(6, random.nextInt(2));
                ps.setInt(7, random.nextInt(6));
                ps.setDouble(8, random.nextDouble());
                ps.setInt(9, random.nextInt(2));
                ps.execute();

                // 获取插入的订单Id
                String sqlLastInsertId = "SELECT LAST_INSERT_ID()";
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(sqlLastInsertId);
                while(resultSet.next()) {
                    int orderId = resultSet.getInt(1);
                    orderIdList.add(orderId);
                }
                System.out.println("生成一条新的订单>>>");
            }

        } catch (SQLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }

    }

    /**
     * 插入订单详细表数据
     *
     * 第一步 执行该部分程序，生成订单详情
     * 第二步，修改支付时间
     * 第三步，修改商品价格、商品名称
     * 第四部 修改订单表的支付价格
     *
     */
    public void insertOrderGoods(){

        String sql = "INSERT INTO itcast_shop.itcast_order_goods (orderId, goodsId, goodsNum, goodsPrice, goodsSpecId, goodsSpecNames, goodsName, goodsImg, extraJson, goodsType, commissionRate, goodsCode, promotionJson) VALUES (?, ?, ?, 11.60, 33, '', '', '', null, 0, 0.02, null, null)";

        List<Integer> goods = goodsIdList;

        Random r = new Random();
        r.nextInt(15808);
//        int val = r.nextInt(15808);
        int goodsOffset = r.nextInt(15808);
        int orderCount = 0;
        orderCount=r.nextInt(1);

        try {
            ps = (PreparedStatement) conn.prepareStatement(sql);

            for(int orderid : orderIdList){
                orderCount=(r.nextInt(6) + 1);
                for(int i=0;i<orderCount;i++){
                    goodsOffset = r.nextInt(15808) % goods.size();
                    ps.setInt(1, orderid);
                    ps.setInt(2, goods.get(goodsOffset));
                    ps.setInt(3, orderCount);
                    ps.execute();
                }
            }

            orderIdList.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更改订单详情的创建时间
     * 更改订单购买商品数量
     * og.goodsNum=(RAND() * 8 + 1),
     */
    public void updateOrderGoodsCreateTime(){
        String sql = "update itcast_order_goods og,itcast_orders o set og.createtime=o.createTime where og.orderId=o.orderId";

        try {
            ps = (PreparedStatement) conn.prepareStatement(sql);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }

    /**
     * 更改订单中商品价格与购买数量
     */
    public void updateOrderGoodsPriceAndNum(){
        String sql = "update itcast_order_goods og,itcast_goods g set og.goodsNum=(RAND() * 8 + 1),og.goodsPrice=g.shopPrice where og.goodsId=g.goodsId and og.createtime>=? and og.createtime<?";

        Long createTime = DateUtil.DateStringToTime("yyyy-MM-dd",createDate);
        Long endTime = createTime + 86400000L;

        String endDate = DateUtil.timeToDateString(endTime,"yyyy-MM-dd");

        try {
            ps = (PreparedStatement) conn.prepareStatement(sql);
            ps.setString(1, createDate);
            ps.setString(2, endDate);
            ps.executeUpdate();
        } catch (SQLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }


    /**
     * 修改订单明细的总价格
     */
    public void updateOrderGoodsMoney() {
        String sql = "update  itcast_order_goods og set og.payPrice=(og.goodsPrice*goodsNum) where createtime>=? and createtime<? ";

        Long createTime = DateUtil.DateStringToTime("yyyy-MM-dd",createDate);
        Long endTime = createTime + 86400000L;

        String endDate = DateUtil.timeToDateString(endTime,"yyyy-MM-dd");

        try {
            ps = (PreparedStatement) conn.prepareStatement(sql);
            ps.setString(1, createDate);
            ps.setString(2, endDate);
            ps.executeUpdate();

        } catch (SQLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }

    /**
     * 修改订单明细的支付类型
     */
    public void updateOrderpaytype() {
        String sql = "update itcast_orders set payType=RAND() * 4 where createtime>=? and createtime<? ";
        Long createTime = DateUtil.DateStringToTime("yyyy-MM-dd",createDate);
        Long endTime = createTime + 86400000L;

        String endDate = DateUtil.timeToDateString(endTime,"yyyy-MM-dd");

        try {
            ps = (PreparedStatement) conn.prepareStatement(sql);
            ps.setString(1, createDate);
            ps.setString(2, endDate);
            ps.executeUpdate();

        } catch (SQLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }

    /**
     * 更改订单支付金额,支付类型
     */
    public void updateOrderTotalMoney() {
        String sql = "update itcast_orders set totalMoney=?,realTotalMoney=? where orderId=?";
        List<OrderObject> orderObjects = getOrderMoney();
        try {
            ps = (PreparedStatement) conn.prepareStatement(sql);
            for (OrderObject order : orderObjects) {
                ps.setDouble(1, order.getTotalMoney());
                ps.setDouble(2, order.getTotalMoney());
                ps.setLong(3, order.getOrderId());
//                    ps.setInt(3, orderCount);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }

    public void updateOrderAddress(){
        Map<Integer,List<Integer>> adds = addressMap;
        List<Integer> orderIds = orderIdList;
        String sql = "update itcast_orders set userAddressId=?,userId=? where orderId = ?";

        int userId = 0;
        int addsId = 0;

        int userLong = adds.size();
        List<Integer> addIds = null;
        try {
            ps = (PreparedStatement) conn.prepareStatement(sql);
            for(Integer orderId : orderIds){
                userId = RandomUtils.nextInt(100201,105200);
                addIds =adds.get(userId);
                addsId = addIds.get(RandomUtils.nextInt(0,addIds.size()));
                ps.setInt(1,addsId);
                ps.setInt(2,userId);
                ps.setInt(3,orderId);
                ps.executeUpdate();
//                System.out.println("addsId : " + addsId +   "     userId : " + userId + " orderId : " + orderId );
            }
        } catch (SQLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }
}
