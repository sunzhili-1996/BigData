package cn.itcast.shop.realtime.etl.process

import java.io.File

import cn.itcast.canal.util.ip.IPSeeker
import cn.itcast.shop.realtime.etl.`trait`.MQBaseETL
import cn.itcast.shop.realtime.etl.bean.{ClickLogEntity, ClickLogWideEntity}
import cn.itcast.shop.realtime.etl.utils.DateUtil.{date2DateStr, datetime2date}
import cn.itcast.shop.realtime.etl.utils.GlobalConfigUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import nl.basjes.parse.httpdlog.HttpdLoglineParser
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.scala._

/**
 * 点击流日志的实时ETL操作
 * 需要将点击流日志对象转换成拓宽后的点击流对象，增加省份、城市、时间字段
 * @param env
 */
class ClickLogDataETL(env: StreamExecutionEnvironment) extends MQBaseETL(env){
  /**
   * 根据业务抽取出来process方法，因为所有的ETL都有操作方法
   */
  override def process(): Unit = {
    /**
     * 实现步骤：
     * 1：获取点击流日志的数据源
     * 2：将nginx的点击流日志字符串转换成点击流对象
     * 3：对点击流对象进行实时拉宽操作，返回拉宽后的点击流实体对象
     * 4：将拉宽后点点击流实体类转换成json字符串
     * 5：将json字符串写入到kafka集群，供Druid进行实时的摄取操作
     */

    //1：获取点击流日志的数据源
    val clickLogDataStream: DataStream[String] = getKafkaDataStream(GlobalConfigUtil.`input.topic.click_log`)

    //2：将nginx的点击流日志字符串转换成点击流对象
    val clickLogWideEntityDataStream: DataStream[ClickLogWideEntity] = etl(clickLogDataStream)

    //4：将拉宽后点点击流实体类转换成json字符串
    val clickLogJsonDataStream: DataStream[String] = clickLogWideEntityDataStream.map(log => {
      //将拉宽后的点击流对象样例类转换成json字符串
      JSON.toJSONString(log, SerializerFeature.DisableCircularReferenceDetect)
    })

    //打印测试
    clickLogJsonDataStream.printToErr("点击流数据>>>")

    //5：将json字符串写入到kafka集群，供Druid进行实时的摄取操作
    clickLogJsonDataStream.addSink(kafkaProducer(GlobalConfigUtil.`output.topic.clicklog`))
  }

  /**
   * 将点击流日志字符串转换成拉宽后的点击流对象
   * @param clickLogDataStream
   */
  def etl(clickLogDataStream: DataStream[String]) ={
    /**
     * 实现步骤：
     * 1：将点击流日志字符串转换成点击流对象，使用logparsing解析
     * 2：根据ip地址，获取到ip地址对应的省份、城市、访问时间等信息，需要一个ip地址库，传递ip地址返回对应的省份和城市信息
     * 3：将获取到的省份和城市作为拉宽后对象的参数传递进去，将拉宽后的点击流对象返回
     */
    //1：将点击流日志字符串转换成点击流对象，使用logparsing解析
    val clickLogEntityDataStream: DataStream[ClickLogEntity] = clickLogDataStream.map(new RichMapFunction[String, ClickLogEntity] {
      //定义数据格式化的解析器对象
      var parser: HttpdLoglineParser[ClickLogEntity] = _

      //这个方法只被初始化一次，一般用于初始化外部资源
      override def open(parameters: Configuration): Unit = {
        //实例化解析器
        parser = ClickLogEntity.createClickLogParse()
      }

      //对数据一条条的进行转换
      override def map(value: String): ClickLogEntity = {
        //将点击流字符串转换成点击流对象返回
        ClickLogEntity(value, parser)
      }
    })

    //2：根据ip地址，获取到ip地址对应的省份、城市、访问时间等信息，需要一个ip地址库，传递ip地址返回对应的省份和城市信息
    val clickLogWideDataStream: DataStream[ClickLogWideEntity] = clickLogEntityDataStream.map(new RichMapFunction[ClickLogEntity, ClickLogWideEntity] {
      //定义ip获取省份城市的实例对象
      var ipSeeker: IPSeeker = _

      //初始化操作，读取分布式缓存文件
      override def open(parameters: Configuration): Unit = {
        //读取分布式缓存文件
        val dataFile: File = getRuntimeContext.getDistributedCache.getFile("qqwry.dat")
        //初始化Ipseeker的实例
        ipSeeker = new IPSeeker(dataFile)
      }

      //对数据进行条条的处理
      override def map(clickLogEntity: ClickLogEntity): ClickLogWideEntity = {
        //拷贝点击流日志对象到拉宽后的点击流对象中
        val clickLogWideEntity: ClickLogWideEntity = ClickLogWideEntity(clickLogEntity)

        //根据ip地址获取省份、城市信息
        val country: String = ipSeeker.getCountry(clickLogWideEntity.ip)
        //江苏省常州市
        var areaArray: Array[String] = country.split("省")

        if(areaArray.size>1){
          //表示非直辖市
          clickLogWideEntity.province = areaArray(0)+"省"
          clickLogWideEntity.city = areaArray(1)
        }else{
          //表示直辖市
          //上海市闵行区
          areaArray = country.split("市")
          if(areaArray.length>1){
            clickLogWideEntity.province =  areaArray(0)+"市"
            clickLogWideEntity.city = areaArray(1)
          }else{
            clickLogWideEntity.province =  areaArray(0)
            clickLogWideEntity.city = ""
          }
        }

        clickLogWideEntity.requestDateTime = date2DateStr(datetime2date("05/Sep/2010:11:27:50 +0200"), "yyyy-MM-dd HH:mm:ss")
        clickLogWideEntity
      }
    })

    //返回拉宽后的点击流对象
    clickLogWideDataStream
  }
}
