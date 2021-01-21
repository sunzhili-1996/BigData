package cn.itcast.shop.realtime.etl.`trait`
import cn.itcast.shop.realtime.etl.utils.KafkaProps
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
import org.apache.flink.streaming.api.scala._

/**
 * 根据数据的来源不同，可以抽象出来两个抽象类
 * 该类主要是消费日志数据和购物车以及评论数据，这类数据存储的就是字符串，所以直接使用kafka的String反序列化即可
 */
abstract class MQBaseETL(env:StreamExecutionEnvironment) extends BaseETL[String] {
  /**
   * 根据业务可以抽取出来kafka读取方法，因为所有的ETL都会操作kafka
   *
   * @param topic
   * @return
   */
  override def getKafkaDataStream(topic: String): DataStream[String] = {
    //创建消费者对象，从kafka中消费数据，消费到的数据是字符串类型
    val kafkaProducer: FlinkKafkaConsumer011[String] = new FlinkKafkaConsumer011[String](
      topic,
      new SimpleStringSchema(),
      KafkaProps.getKafkaProperties()
    )

    //将消费者对象添加到数据源
    val logDataStream: DataStream[String] = env.addSource(kafkaProducer)

    //返回消费到的数据
    logDataStream
  }
}
