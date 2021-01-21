package cn.itcast.shop.realtime.etl.async

import cn.itcast.canal.bean.CanalRowData
import cn.itcast.shop.realtime.etl.bean.{DimGoodsCatDBEntity, DimGoodsDBEntity, DimOrgDBEntity, DimShopsDBEntity, OrderGoodsWideEntity}
import cn.itcast.shop.realtime.etl.utils.RedisUtil
import org.apache.flink.configuration.Configuration
import org.apache.flink.runtime.concurrent.Executors
import org.apache.flink.streaming.api.scala.async.{ResultFuture, RichAsyncFunction}
import redis.clients.jedis.Jedis

import scala.concurrent.{ExecutionContext, Future}

/**
 * 异步查询订单明细数据与维度数据进行关联
 * 根据订单明细的事实表与维度表进行关联，所以需要redis，从而打开关闭数据源，使用RichAsyncFunction
 * 使用异步IO的目的是为了提高吞吐量
 */
class AsyncOrderDetailRedisRequest extends RichAsyncFunction[CanalRowData, OrderGoodsWideEntity]{
  //定义redis的对象
  var jedis:Jedis = _

  //初始化数据源，打开连接
  override def open(parameters: Configuration): Unit = {
    //获取redis的连接
    jedis = RedisUtil.getJedis()
    //指定维度数据所在的数据库的索引
    jedis.select(1)
  }

  //释放资源，关闭连接
  override def close(): Unit = {
    if(jedis.isConnected){
      jedis.close()
    }
  }

  /**
   * 连接redis超时的操作，默认会抛出异常，一旦重写了该方法，则执行方法的逻辑
   * @param input
   * @param resultFuture
   */
  override def timeout(input: CanalRowData, resultFuture: ResultFuture[OrderGoodsWideEntity]): Unit = {
    println("订单明细实时拉宽操作的时候，与维度数据进行关联操作超时了")
  }

  /**
   * 定义异步回调的上下文对象
   */
  implicit lazy val executor = ExecutionContext.fromExecutor(Executors.directExecutor())

  //异步操作，对数据流中的每一条数据进行处理，但是这个是一个异步的操作
  override def asyncInvoke(rowData: CanalRowData, resultFuture: ResultFuture[OrderGoodsWideEntity]): Unit = {
    //发起异步请求，获取结束的Fature
    Future {
      if(!jedis.isConnected){
        jedis = RedisUtil.getJedis()
        jedis.select(1)
      }
      //1：根据商品id获取商品的详细信息（itcast_shop:dim_goods）
      val goodsJson: String = jedis.hget("itcast_shop:dim_goods", rowData.getColumns.get("goodsId"))
      //将商品的json字符串解析成商品的样例类
      val dimGoods: DimGoodsDBEntity = DimGoodsDBEntity(goodsJson)

      //2：根据商品表的店铺id获取店铺的详细信息（itcast_shop:dim_shops）
      val shopJson = jedis.hget("itcast_shop:dim_shops", dimGoods.shopId.toString)
      //将店铺的字段串转换成店铺的样例类
      val dimShop: DimShopsDBEntity = DimShopsDBEntity(shopJson)

      //3：根据商品的id获取商品的分类信息
      //3.1：获取商品的三级分类信息
      val thirdCatJson: String = jedis.hget("itcast_shop:dim_goods_cats", dimGoods.getGoodsCatId.toString)
      val dimThirdCat: DimGoodsCatDBEntity = DimGoodsCatDBEntity(thirdCatJson)

      //3.2：获取商品的二级分类信息
      val secondCatJson: String = jedis.hget("itcast_shop:dim_goods_cats", dimThirdCat.parentId)
      val dimSecondCat: DimGoodsCatDBEntity = DimGoodsCatDBEntity(secondCatJson)

      //3.3：获取商品的三级分类信息
      val firstCatJson: String = jedis.hget("itcast_shop:dim_goods_cats", dimSecondCat.parentId)
      val dimFirstCat: DimGoodsCatDBEntity = DimGoodsCatDBEntity(firstCatJson)

      //4：根据店铺表的区域id找到组织机构数据
      //4.1：根据区域id获取城市数据
      val cityJson: String = jedis.hget("itcast_shop:dim_org", dimShop.areaId.toString)
      val dimOrgCity: DimOrgDBEntity = DimOrgDBEntity(cityJson)

      //4.2：根据区域的父id获取大区数据
      val regionJson: String = jedis.hget("itcast_shop:dim_org", dimOrgCity.parentId.toString)
      val dimOrgRegion: DimOrgDBEntity = DimOrgDBEntity(regionJson)

      //构建订单明细宽表数据对象，返回
      val orderGoodsWideEntity = OrderGoodsWideEntity(
        rowData.getColumns.get("ogId").toLong,
        rowData.getColumns.get("orderId").toLong,
        rowData.getColumns.get("goodsId").toLong,
        rowData.getColumns.get("goodsNum").toLong,
        rowData.getColumns.get("goodsPrice").toDouble,
        dimGoods.goodsName,
        dimShop.shopId,
        dimThirdCat.catId.toInt,
        dimThirdCat.catName,
        dimSecondCat.catId.toInt,
        dimSecondCat.catName,
        dimFirstCat.catId.toInt,
        dimFirstCat.catName,
        dimShop.areaId,
        dimShop.shopName,
        dimShop.shopCompany,
        dimOrgCity.orgId,
        dimOrgCity.orgName,
        dimOrgRegion.orgId.toInt,
        dimOrgRegion.orgName)

      //异步请求回调
      resultFuture.complete(Array(orderGoodsWideEntity))
    }
   }
}
