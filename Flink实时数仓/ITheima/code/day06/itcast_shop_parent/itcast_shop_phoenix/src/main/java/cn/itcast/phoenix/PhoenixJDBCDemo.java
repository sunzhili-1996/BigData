package cn.itcast.phoenix;

import java.sql.*;

/**
 * 使用jdbc的方式连接Phoenix
 */
public class PhoenixJDBCDemo {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        //1：注册驱动
        //2：创建连接
        //3：创建statement
        //4：执行sql查询
        //5：循环遍历查询的结果集
        //6：关闭连接
        //1：注册驱动
        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");

        //2：创建连接
        Connection connection = DriverManager.getConnection("jdbc:phoenix:node1:2181", "", "");

        //3：创建statement
        Statement statement = connection.createStatement();

        //4：执行sql查询
        ResultSet resultSet = statement.executeQuery("select *from \"dwd_order_detail\"");

        //5：循环遍历查询的结果集
        while (resultSet.next()){
            String rowid = resultSet.getString("rowid");
            String ogId = resultSet.getString("ogId");
            String orderId = resultSet.getString("orderId");
            String goodsId = resultSet.getString("goodsId");
            String goodsNum = resultSet.getString("goodsNum");
            String goodsPrice = resultSet.getString("goodsPrice");
            String goodsName = resultSet.getString("goodsName");
            String shopId = resultSet.getString("shopId");
            String goodsThirdCatId = resultSet.getString("goodsThirdCatId");
            String goodsThirdCatName = resultSet.getString("goodsThirdCatName");
            String goodsSecondCatId = resultSet.getString("goodsSecondCatId");
            String goodsSecondCatName = resultSet.getString("goodsSecondCatName");
            String goodsFirstCatId = resultSet.getString("goodsFirstCatId");
            String goodsFirstCatName = resultSet.getString("goodsFirstCatName");
            String areaId = resultSet.getString("areaId");
            String shopName = resultSet.getString("shopName");
            String shopCompany = resultSet.getString("shopCompany");
            String cityId = resultSet.getString("cityId");
            String cityName = resultSet.getString("cityName");
            String regionId = resultSet.getString("regionId");
            String regionName = resultSet.getString("regionName");

            System.out.print(rowid);
            System.out.print(ogId);
            System.out.print(orderId);
            System.out.print(goodsId);
            System.out.print(goodsNum);
            System.out.print(goodsPrice);
            System.out.print(goodsName);
            System.out.print(shopId);
            System.out.print(goodsThirdCatId);
            System.out.print(goodsThirdCatName);
            System.out.print(goodsSecondCatId);
            System.out.print(goodsSecondCatName);
            System.out.print(goodsFirstCatId);
            System.out.print(goodsFirstCatName);
            System.out.print(areaId);
            System.out.print(shopName);
            System.out.print(shopCompany);
            System.out.print(cityId);
            System.out.print(cityName);
            System.out.print(regionId);
            System.out.print(regionName);
            System.out.println();
        }
        //6：关闭连接
        statement.close();
        connection.close();
    }
}
