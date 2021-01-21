package cn.itcast.shop.realtime.etl.process

import cn.itcast.canal.bean.CanalRowData
import cn.itcast.shop.realtime.etl.`trait`.MysqlBaseETL
import cn.itcast.shop.realtime.etl.bean.{DimGoodsCatDBEntity, DimGoodsDBEntity, DimOrgDBEntity, DimShopCatDBEntity, DimShopsDBEntity}
import cn.itcast.shop.realtime.etl.utils.RedisUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import redis.clients.jedis.Jedis

/**
 * 增量更新维度数据到redis中
 * @param env
 */
case class SyncDimData(env: StreamExecutionEnvironment) extends MysqlBaseETL(env){
  /**
   * 根据业务抽取出来process方法，因为所有的ETL都有操作方法
   *
   */
  override def process(): Unit = {
    /**
     * 实现步骤：
     * 1：获取数据源
     * 2：过滤出来维度表
     * 3：处理同步过来的数据，更新到redis中
     */
    //1：获取数据源
    val canalDataStream: DataStream[CanalRowData] = getKafkaDataStream();

    //2：过滤出来维度表
    val dimRowDataStream: DataStream[CanalRowData] = canalDataStream.filter {
      rowData => {
        rowData.getTableName match {
          case "itcast_goods" => true
          case "itcast_shops" => true
          case "itcast_goods_cats" => true
          case "itcast_org" => true
          case "itcast_shop_cats" => true
          //一定要加上else，否则会抛出异常
          case _ => false
        }
      }
    }

    //3：处理同步过来的数据，更新到redis中
    dimRowDataStream.addSink(new RichSinkFunction[CanalRowData] {
      //定义redis的对象
      var jedis:Jedis = _

      //这个方法只被执行一次，一般用于初始化外部数据源
      override def open(parameters: Configuration): Unit = {
        //获取一个连接
        jedis = RedisUtil.getJedis()
        //维度数据在第二个数据库中
        jedis.select(1)
      }

      //只被调用一次，用于释放数据源
      override def close(): Unit = {
        //如果jedis的是已连接状态
        if(jedis.isConnected){
          jedis.close()
        }
      }

      //处理数据，一条条的处理数据
      override def invoke(rowData: CanalRowData, context: SinkFunction.Context[_]): Unit = {
        //根据操作类型的不同，调用不同的业务逻辑实现数据的更新
        rowData.getEventType match {
          case eventType if(eventType == "insert" || eventType == "update") => updateDimData(rowData)
          case "delete" => deleteDimData(rowData)
          case _ =>
        }
      }

      /**
       * 更新维度数据
       * @param rowData
       */
      def updateDimData(rowData:CanalRowData):Unit = {
        //区分出来是操作的那张维度表
        rowData.getTableName match {
          case "itcast_goods" =>{
            //如果是商品维度表更新
            val goodsId = rowData.getColumns.get("goodsId").toLong
            val goodsName = rowData.getColumns.get("goodsName")
            val shopId = rowData.getColumns.get("shopId").toLong
            val goodsCatId = rowData.getColumns.get("goodsCatId").toInt
            val shopPrice = rowData.getColumns.get("shopPrice").toDouble

            //需要将获取到的商品维度表数据写入到redis中
            //redis是一个k/v数据库，需要需要将以上五个字段封装成json结构保存到redis中
            val goodsDBEntity: DimGoodsDBEntity = DimGoodsDBEntity(goodsId, goodsName, shopId, goodsCatId, shopPrice)
            val json: String = JSON.toJSONString(goodsDBEntity, SerializerFeature.DisableCircularReferenceDetect)
            jedis.hset("itcast_shop:dim_goods", goodsId.toString, json)
          }
          case "itcast_shops" =>{
            //如果是店铺维度表更新
            val shopId = rowData.getColumns.get("shopId").toInt
            val areaId = rowData.getColumns.get("areaId").toInt
            val shopName = rowData.getColumns.get("shopName")
            val shopCompany = rowData.getColumns.get("shopCompany")

            val dimShop = DimShopsDBEntity(shopId, areaId, shopName, shopCompany)
            jedis.hset("itcast_shop:dim_shops", shopId + "", JSON.toJSONString(dimShop, SerializerFeature.DisableCircularReferenceDetect))
          }
          case "itcast_goods_cats" =>{
            //如果是商品分类维度表更新
            val catId = rowData.getColumns.get("catId")
            val parentId = rowData.getColumns.get("parentId")
            val catName = rowData.getColumns.get("catName")
            val cat_level = rowData.getColumns.get("cat_level")

            val entity = DimGoodsCatDBEntity(catId, parentId, catName, cat_level)
            jedis.hset("itcast_shop:dim_goods_cats", catId, JSON.toJSONString(entity, SerializerFeature.DisableCircularReferenceDetect))

          }
          case "itcast_org" =>{
            //如果是组织机构维度表更新
            val orgId = rowData.getColumns.get("orgId").toInt
            val parentId = rowData.getColumns.get("parentId").toInt
            val orgName = rowData.getColumns.get("orgName")
            val orgLevel = rowData.getColumns.get("orgLevel").toInt

            val entity = DimOrgDBEntity(orgId, parentId, orgName, orgLevel)
            jedis.hset("itcast_shop:dim_org", orgId + "", JSON.toJSONString(entity, SerializerFeature.DisableCircularReferenceDetect))
          }
          case "itcast_shop_cats" =>{
            //如果是门店商品分类维度表更新
            val catId = rowData.getColumns.get("catId")
            val parentId = rowData.getColumns.get("parentId")
            val catName = rowData.getColumns.get("catName")
            val cat_level = rowData.getColumns.get("catSort")

            val entity = DimShopCatDBEntity(catId, parentId, catName, cat_level)
            jedis.hset("itcast_shop:dim_shop_cats", catId, JSON.toJSONString(entity, SerializerFeature.DisableCircularReferenceDetect))
          }
        }
      }

      /**
       * 删除维度数据
       * @param rowData
       */
      def deleteDimData(rowData:CanalRowData)= {

        //区分出来是操作的那张维度表
        rowData.getTableName match {
          case "itcast_goods" =>{
            //如果是商品维度表更新
            jedis.hdel("itcast_shop:dim_goods", rowData.getColumns.get("goodsId"))
          }
          case "itcast_shops" =>{
            //如果是店铺维度表更新
            jedis.hdel("itcast_shop:dim_shops",  rowData.getColumns.get("shopId"))
          }
          case "itcast_goods_cats" =>{
            //如果是商品分类维度表更新
            jedis.hdel("itcast_shop:dim_goods_cats", rowData.getColumns.get("catId"))

          }
          case "itcast_org" =>{
            //如果是组织机构维度表更新
            jedis.hdel("itcast_shop:dim_org", rowData.getColumns.get("orgId"))
          }
          case "itcast_shop_cats" =>{
            //如果是门店商品分类维度表更新
            jedis.hdel("itcast_shop:dim_shop_cats", rowData.getColumns.get("catId"))
          }
        }
      }
    })
  }
}
