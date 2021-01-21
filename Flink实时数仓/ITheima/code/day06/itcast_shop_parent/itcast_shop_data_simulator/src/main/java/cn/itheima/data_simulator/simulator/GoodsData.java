package cn.itheima.data_simulator.simulator;

import cn.itheima.data_simulator.base.DBSimulator;
import javafx.util.Pair;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 商品数据模拟器
 */
public class GoodsData extends DBSimulator {

    @Override
    public void sendToKafka() throws SQLException {
        insertGoodsInfo();
    }

    //获取商品信息
    public void insertGoodsInfo() throws SQLException {
        Pair<Integer, String> userInfo = null;
        String sql = "INSERT INTO itcast_goods(`goodsSn`,`productNo`,`goodsName`,`goodsImg`,`shopId`,`goodsType`,`marketPrice`,`shopPrice`," +
                        " `warnStock`,`goodsStock`,`goodsUnit`,`goodsTips`,`isSale`,`isBest`,`isHot`,`isNew`,`isRecom`," +
                        " `goodsCatIdPath`,`goodsCatId`,`shopCatId1`,`shopCatId2`,`brandId`,`goodsDesc`,`goodsStatus`," +
                        " `saleNum`,`saleTime`,`visitNum`,`appraiseNum`,`isSpec`,`gallery`,`goodsSeoKeywords`,`illegalRemarks`," +
                        "        `dataFlag`,`createTime`,`isFreeShipping`,`goodsSerachKeywords`,`modifyTime`)" +
                        " SELECT `goodsSn`,`productNo`,`goodsName`,`goodsImg`,`shopId`,`goodsType`,`marketPrice`,`shopPrice`," +
                        " `warnStock`,`goodsStock`,`goodsUnit`,`goodsTips`,`isSale`,`isBest`,`isHot`,`isNew`,`isRecom`," +
                        " `goodsCatIdPath`,`goodsCatId`,`shopCatId1`,`shopCatId2`,`brandId`,`goodsDesc`,`goodsStatus`," +
                        " `saleNum`,`saleTime`,`visitNum`,`appraiseNum`,`isSpec`,`gallery`,`goodsSeoKeywords`,`illegalRemarks`," +
                        "        `dataFlag`,`createTime`,`isFreeShipping`,`goodsSerachKeywords`,`modifyTime`" +
                        " FROM itcast_goods ORDER BY RAND() LIMIT 1;";
        try {
            conn.setAutoCommit(false);//JDBC中默认是true，自动提交事务
            ps = conn.prepareStatement(sql);
            ps.execute();

            // 获取插入的订单Id
            String sqlLastInsertId = "SELECT LAST_INSERT_ID()";
            Statement statement = conn.createStatement();
            rs = statement.executeQuery(sqlLastInsertId);
            while(rs.next()) {
                int goodId = rs.getInt(1);
                ThreadLocalRandom random = ThreadLocalRandom.current();

                //修改商品上架/下架状态及上架时间
                sql = "update itcast_goods set saleTime=?, isSale=?,createTime=? where goodsId=?";
                ps = (PreparedStatement) conn.prepareStatement(sql);
                String currDateTime = timeToDateString((new Date().getTime()), "yyyy-MM-dd HH:mm:ss");
                ps.setString(1, currDateTime);
                ps.setInt(2, random.nextInt(2));
                ps.setString(3, currDateTime);
                ps.setLong(4, goodId);
                ps.execute();

                System.out.println("发送商品日志消息>>>当前订单创建日期 ： " + currDateTime);
            }
            conn.commit();//提交事务
            rs.close();
            ps.close();
        }  catch (SQLException e) {
            conn.rollback();//回滚，出现异常后两条数据都执行不成功
            e.printStackTrace();
        }finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static String timeToDateString(Long dateTime,String format){
        Date date = new Date(dateTime);

        SimpleDateFormat sdf= null;
        String dateString = null;
        sdf= new SimpleDateFormat(format);
        dateString = sdf.format(date);
        return dateString;
    }
}
class GoodsWideBean{
    private String goodsId;         //商品id
    private String goodsSn;         //商品编号
    private String productNo;       //商品货号
    private String goodsName;       //商品名称
    private String goodsImg;        //商品图片
    private String shopId;          //门店ID
    private String goodsType;       //货物类型
    private String marketPrice;     //市场价
    private String shopPrice;       //门店价
    private String warnStock;       //预警库存
    private String goodsStock;      //商品总库存
    private String goodsUnit;       //单位
    private String goodsTips;       //促销信息
    private String isSale;          //是否上架	0:不上架 1:上架
    private String isBest;          //是否精品	0:否 1:是
    private String isHot;           //是否热销产品	0:否 1:是
    private String isNew;           //是否新品	0:否 1:是
    private String isRecom;         //是否推荐	0:否 1:是
    private String goodsCatIdPath;  //商品分类ID路径	catId1_catId2_catId3
    private String goodsCatId;      //goodsCatId	最后一级商品分类ID
    private String shopCatId1;          //门店商品分类第一级ID
    private String shopCatId2;          //门店商品第二级分类ID
    private String brandId;         //品牌ID
    private String goodsDesc;       //商品描述
    private String goodsStatus;     //商品状态	-1:违规 0:未审核 1:已审核
    private String saleNum;         //总销售量
    private String saleTime;        //上架时间
    private String visitNum;        //访问数
    private String appraiseNum;     //评价书
    private String isSpec;          //是否有规格	0:没有 1:有
    private String gallery;         //商品相册
    private String goodsSeoKeywords;  //商品SEO关键字
    private String illegalRemarks;  //状态说明	一般用于说明拒绝原因

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(String shopPrice) {
        this.shopPrice = shopPrice;
    }

    public String getWarnStock() {
        return warnStock;
    }

    public void setWarnStock(String warnStock) {
        this.warnStock = warnStock;
    }

    public String getGoodsStock() {
        return goodsStock;
    }

    public void setGoodsStock(String goodsStock) {
        this.goodsStock = goodsStock;
    }

    public String getGoodsUnit() {
        return goodsUnit;
    }

    public void setGoodsUnit(String goodsUnit) {
        this.goodsUnit = goodsUnit;
    }

    public String getGoodsTips() {
        return goodsTips;
    }

    public void setGoodsTips(String goodsTips) {
        this.goodsTips = goodsTips;
    }

    public String getIsSale() {
        return isSale;
    }

    public void setIsSale(String isSale) {
        this.isSale = isSale;
    }

    public String getIsBest() {
        return isBest;
    }

    public void setIsBest(String isBest) {
        this.isBest = isBest;
    }

    public String getIsHot() {
        return isHot;
    }

    public void setIsHot(String isHot) {
        this.isHot = isHot;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public String getIsRecom() {
        return isRecom;
    }

    public void setIsRecom(String isRecom) {
        this.isRecom = isRecom;
    }

    public String getGoodsCatIdPath() {
        return goodsCatIdPath;
    }

    public void setGoodsCatIdPath(String goodsCatIdPath) {
        this.goodsCatIdPath = goodsCatIdPath;
    }

    public String getGoodsCatId() {
        return goodsCatId;
    }

    public void setGoodsCatId(String goodsCatId) {
        this.goodsCatId = goodsCatId;
    }

    public String getShopCatId1() {
        return shopCatId1;
    }

    public void setShopCatId1(String shopCatId1) {
        this.shopCatId1 = shopCatId1;
    }

    public String getShopCatId2() {
        return shopCatId2;
    }

    public void setShopCatId2(String shopCatId2) {
        this.shopCatId2 = shopCatId2;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public String getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(String goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public String getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(String saleNum) {
        this.saleNum = saleNum;
    }

    public String getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(String saleTime) {
        this.saleTime = saleTime;
    }

    public String getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(String visitNum) {
        this.visitNum = visitNum;
    }

    public String getAppraiseNum() {
        return appraiseNum;
    }

    public void setAppraiseNum(String appraiseNum) {
        this.appraiseNum = appraiseNum;
    }

    public String getIsSpec() {
        return isSpec;
    }

    public void setIsSpec(String isSpec) {
        this.isSpec = isSpec;
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public String getGoodsSeoKeywords() {
        return goodsSeoKeywords;
    }

    public void setGoodsSeoKeywords(String goodsSeoKeywords) {
        this.goodsSeoKeywords = goodsSeoKeywords;
    }

    public String getIllegalRemarks() {
        return illegalRemarks;
    }

    public void setIllegalRemarks(String illegalRemarks) {
        this.illegalRemarks = illegalRemarks;
    }

    public String getDataFlag() {
        return dataFlag;
    }

    public void setDataFlag(String dataFlag) {
        this.dataFlag = dataFlag;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIsFreeShipping() {
        return isFreeShipping;
    }

    public void setIsFreeShipping(String isFreeShipping) {
        this.isFreeShipping = isFreeShipping;
    }

    public String getGoodsSerachKeywords() {
        return goodsSerachKeywords;
    }

    public void setGoodsSerachKeywords(String goodsSerachKeywords) {
        this.goodsSerachKeywords = goodsSerachKeywords;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    private String dataFlag;        //	删除标志	-1:删除 1:有效
    private String createTime;
    private String isFreeShipping;
    private String goodsSerachKeywords;
    private String modifyTime;      //修改时间
}