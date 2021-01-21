package cn.itcast.shop.realtime.etl.process

import java.util.concurrent.TimeUnit

import cn.itcast.canal.bean.CanalRowData
import cn.itcast.shop.realtime.etl.`trait`.MysqlBaseETL
import cn.itcast.shop.realtime.etl.async.AsyncOrderDetailRedisRequest
import cn.itcast.shop.realtime.etl.bean.OrderGoodsWideEntity
import cn.itcast.shop.realtime.etl.utils.{GlobalConfigUtil, HbaseUtil}
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import org.apache.flink.streaming.api.scala.{AsyncDataStream, DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Connection, Put, Table}
import org.apache.hadoop.hbase.util.Bytes

/**
 * 订单明细的实时ETL操作
 * 1）将订单明细数据的事实表和维度表的数据关联后写入到hbase中
 * 2）将拉宽后的订单数据保存到kafka中，供Druid进行实时摄取
 *
 * 为什么要存储两份：
 * 1）保存到hbase的数据，是明细数据，可以持久化的存储的，将来需要分析明细数据的时候，可以查询
 * 2）保存到kafka的数据，是有时效性的，会根据kafka设置的保留策略过期删除数据，摄取到Durid以后，就不在是明细数据了
 *    已经对明细数据进行了预聚合操作
 * Druid=kylin+hbase
 *
 * @param env
 */
case class OrderDetailDataETL(env: StreamExecutionEnvironment) extends MysqlBaseETL(env){
  /**
   * 根据业务抽取出来process方法，因为所有的ETL都有操作方法
   */
  override def process(): Unit = {
    /**
     * 实现步骤：
     * 1：获取canal中的订单明细数据，过滤出来订单明细表的数据，将CanalRowData转换成OrderGoods样例类
     * 2：将订单明细表的数据进行实时的拉宽操作
     * 3：将拉宽后的订单明细表的数据转换成json字符串写入到kafka集群，供Druid进行实时的摄取
     * 4：将拉宽后的订单明细表的数据写入到hbase中，供后续订单明细详情数据的查询
     */

    //1：获取canal中的订单明细数据，过滤出来订单明细表的数据，将CanalRowData转换成OrderGoods样例类
    val orderGoodsCanalDataStream: DataStream[CanalRowData] = getKafkaDataStream().filter(_.getTableName == "itcast_order_goods")

    orderGoodsCanalDataStream.printToErr("订单明细>>>")
    //2：将订单明细表的数据进行实时的拉宽操作
    /**
     * orderGoodsCanalDataStream：要关联的数据源
     * AsyncOrderDetailRedisRequest：异步请求的对象
     * [1]：超时时间
     * [TimeUnit.SECONDS]：超时的时间单位
     * [100]：异步io的最大并发数
     */
    val orderGoodsWideEntityDataStream: DataStream[OrderGoodsWideEntity] = AsyncDataStream.unorderedWait(orderGoodsCanalDataStream, new AsyncOrderDetailRedisRequest(),
      100, TimeUnit.SECONDS, 100)

    orderGoodsWideEntityDataStream.printToErr("拉宽后的订单明细数据>>>")

    //3：将拉宽后的订单明细表的数据转换成json字符串写入到kafka集群，供Druid进行实时的摄取
    val orderGoodsWideJsonDataStream: DataStream[String] = orderGoodsWideEntityDataStream.map(orderGoods => {
      JSON.toJSONString(orderGoods, SerializerFeature.DisableCircularReferenceDetect)
    })

    //4：将转换后的json格式的订单明细数据写入到kafka集群
    orderGoodsWideJsonDataStream.addSink(kafkaProducer(GlobalConfigUtil.`output.topic.order_detail`))

    //5：将拉宽后的订单明细数据保存到hbase中
    orderGoodsWideEntityDataStream.addSink(new RichSinkFunction[OrderGoodsWideEntity] {
      //定义hbase的连接对象
      var connection:Connection = _
      //定义hbase的表
      var table:Table = _

      //初始化资源，获取连接对象
      override def open(parameters: Configuration): Unit = {
        //初始化hbase的连接对象
        connection = HbaseUtil.getPool().getConnection
        //初始化要写入的表名
        table = connection.getTable(TableName.valueOf(GlobalConfigUtil.`hbase.table.orderdetail`))
      }

      //关闭连接,释放资源
      override def close(): Unit = {
        if(table!=null) table.close()
        if(!connection.isClosed) {
          //将连接放回到连接池
          HbaseUtil.getPool().returnConnection(connection)
        }
      }

      //将数据一条条的写入到hbase
      override def invoke(orderGoodsWideEntity: OrderGoodsWideEntity, context: SinkFunction.Context[_]): Unit = {
        //构建Put对象
        //使用订单明细id作为rowkey
        var rowKey: Array[Byte] = Bytes.toBytes(orderGoodsWideEntity.getOgId)
        val put = new Put(rowKey)
        //创建列族
        var family: Array[Byte] = Bytes.toBytes(GlobalConfigUtil.`hbase.table.family`)

        val ogIdCol = Bytes.toBytes("ogId")
        val orderIdCol = Bytes.toBytes("orderId")
        val goodsIdCol = Bytes.toBytes("goodsId")
        val goodsNumCol = Bytes.toBytes("goodsNum")
        val goodsPriceCol = Bytes.toBytes("goodsPrice")
        val goodsNameCol = Bytes.toBytes("goodsName")
        val shopIdCol = Bytes.toBytes("shopId")
        val goodsThirdCatIdCol = Bytes.toBytes("goodsThirdCatId")
        val goodsThirdCatNameCol = Bytes.toBytes("goodsThirdCatName")
        val goodsSecondCatIdCol = Bytes.toBytes("goodsSecondCatId")
        val goodsSecondCatNameCol = Bytes.toBytes("goodsSecondCatName")
        val goodsFirstCatIdCol = Bytes.toBytes("goodsFirstCatId")
        val goodsFirstCatNameCol = Bytes.toBytes("goodsFirstCatName")
        val areaIdCol = Bytes.toBytes("areaId")
        val shopNameCol = Bytes.toBytes("shopName")
        val shopCompanyCol = Bytes.toBytes("shopCompany")
        val cityIdCol = Bytes.toBytes("cityId")
        val cityNameCol = Bytes.toBytes("cityName")
        val regionIdCol = Bytes.toBytes("regionId")
        val regionNameCol = Bytes.toBytes("regionName")

        put.addColumn(family, ogIdCol, Bytes.toBytes(orderGoodsWideEntity.ogId.toString))
        put.addColumn(family, orderIdCol, Bytes.toBytes(orderGoodsWideEntity.orderId.toString))
        put.addColumn(family, goodsIdCol, Bytes.toBytes(orderGoodsWideEntity.goodsId.toString))
        put.addColumn(family, goodsNumCol, Bytes.toBytes(orderGoodsWideEntity.goodsNum.toString))
        put.addColumn(family, goodsPriceCol, Bytes.toBytes(orderGoodsWideEntity.goodsPrice.toString))
        put.addColumn(family, goodsNameCol, Bytes.toBytes(orderGoodsWideEntity.goodsName.toString))
        put.addColumn(family, shopIdCol, Bytes.toBytes(orderGoodsWideEntity.shopId.toString))
        put.addColumn(family, goodsThirdCatIdCol, Bytes.toBytes(orderGoodsWideEntity.goodsThirdCatId.toString))
        put.addColumn(family, goodsThirdCatNameCol, Bytes.toBytes(orderGoodsWideEntity.goodsThirdCatName.toString))
        put.addColumn(family, goodsSecondCatIdCol, Bytes.toBytes(orderGoodsWideEntity.goodsSecondCatId.toString))
        put.addColumn(family, goodsSecondCatNameCol, Bytes.toBytes(orderGoodsWideEntity.goodsSecondCatName.toString))
        put.addColumn(family, goodsFirstCatIdCol, Bytes.toBytes(orderGoodsWideEntity.goodsFirstCatId.toString))
        put.addColumn(family, goodsFirstCatNameCol, Bytes.toBytes(orderGoodsWideEntity.goodsFirstCatName.toString))
        put.addColumn(family, areaIdCol, Bytes.toBytes(orderGoodsWideEntity.areaId.toString))
        put.addColumn(family, shopNameCol, Bytes.toBytes(orderGoodsWideEntity.shopName.toString))
        put.addColumn(family, shopCompanyCol, Bytes.toBytes(orderGoodsWideEntity.shopCompany.toString))
        put.addColumn(family, cityIdCol, Bytes.toBytes(orderGoodsWideEntity.cityId.toString))
        put.addColumn(family, cityNameCol, Bytes.toBytes(orderGoodsWideEntity.cityName.toString))
        put.addColumn(family, regionIdCol, Bytes.toBytes(orderGoodsWideEntity.regionId.toString))
        put.addColumn(family, regionNameCol, Bytes.toBytes(orderGoodsWideEntity.regionName.toString))

        //3：执行put操作
        table.put(put)
      }
    })
  }
}
