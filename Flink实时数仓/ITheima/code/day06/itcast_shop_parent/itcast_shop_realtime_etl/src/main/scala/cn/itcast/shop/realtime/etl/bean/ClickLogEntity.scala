package cn.itcast.shop.realtime.etl.bean

import lombok.Getter
import nl.basjes.parse.core.Parser
import nl.basjes.parse.httpdlog.HttpdLoglineParser

import scala.beans.BeanProperty

/**
 * 定义点击流日志的类，数据长kafka中消费出来的点击流日志是如下格式：
 * 2001:980:91c0:1:8d31:a232:25e5:85d 222.68.172.190 - [05/Sep/2010:11:27:50 +0200] \"GET /images/my.jpg HTTP/1.1\" 404 23617 \"http://www.angularjs.cn/A00n\" \"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; nl-nl) AppleWebKit/533.17.8 (KHTML, like Gecko) Version/5.0.1 Safari/533.17.8\"
 * 需要使用scala的样例类进行解析，将解析好的日志赋值给样例类，后续在进行业务处理的时候比较方便使用
 */
class ClickLogEntity {
    //用户id信息
    private[this] var _connectionClientUser: String = _
    def setConnectionClientUser (value: String): Unit = { _connectionClientUser = value }
    def getConnectionClientUser = { _connectionClientUser }

    //ip地址
    private[this] var _ip: String = _
    def setIp (value: String): Unit = { _ip = value }
    def getIp = {  _ip }

    //请求时间
    private[this] var _requestTime: String = _
    def setRequestTime (value: String): Unit = { _requestTime = value }
    def getRequestTime = { _requestTime }

    //请求方式
    private[this] var _method:String = _
    def setMethod(value:String) = {_method = value}
    def getMethod = {_method}

    //请求资源
    private[this] var _resolution:String = _
    def setResolution(value:String) = { _resolution = value}
    def getResolution = { _resolution }

    //请求协议
    private[this] var _requestProtocol: String = _
    def setRequestProtocol (value: String): Unit = { _requestProtocol = value }
    def getRequestProtocol = { _requestProtocol }

    //响应码
    private[this] var _responseStatus: String = _
    def setRequestStatus (value: String): Unit = { _responseStatus = value }
    def getRequestStatus = { _responseStatus }

    //返回的数据流量
    private[this] var _responseBodyBytes: String = _
    def setResponseBodyBytes (value: String): Unit = { _responseBodyBytes = value }
    def getResponseBodyBytes = { _responseBodyBytes }

    //访客的来源url
    private[this] var _referer: String = _
    def setReferer (value: String): Unit = { _referer = value }
    def getReferer = { _referer }

    //客户端代理信息
    private[this] var _userAgent: String = _
    def setUserAgent (value: String): Unit = { _userAgent = value }
    def getUserAgent = { _userAgent }

    //跳转过来页面的域名:HTTP.HOST:request.referer.host
    private[this] var _referDomain: String = _
    def setReferDomain (value: String): Unit = { _referDomain = value }
    def getReferDomain = { _referDomain }
}
//传递点击流日志解析出来赋值scala类
object ClickLogEntity{
    //1：定义点击流日志的解析规则
    val getLogFormat: String = "%u %h %l %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\""

    /**
     *  传递点击流日志的字符串和解析器，返回解析后的对象
     * @return
     */
    def apply(clickLog:String, parser: HttpdLoglineParser[ClickLogEntity]): ClickLogEntity = {
      val clickLogEntity = new ClickLogEntity
      parser.parse(clickLogEntity, clickLog)

      //将解析后的点击流日志对象返回
      clickLogEntity
    }

    //2：创建解析器
    def createClickLogParse()={
      //1：创建解析器
      val parser: HttpdLoglineParser[ClickLogEntity] = new HttpdLoglineParser[ClickLogEntity](classOf[ClickLogEntity], getLogFormat)

      //2：建立对象的方法名与参数名的映射关系
      parser.addParseTarget("setConnectionClientUser", "STRING:connection.client.user")
      parser.addParseTarget("setIp", "IP:connection.client.host")
      parser.addParseTarget("setRequestTime", "TIME.STAMP:request.receive.time.last")
      parser.addParseTarget("setMethod", "HTTP.METHOD:request.firstline.method")
      parser.addParseTarget("setResolution", "HTTP.URI:request.firstline.uri")
      parser.addParseTarget("setRequestProtocol", "HTTP.PROTOCOL:request.firstline.protocol")
      parser.addParseTarget("setRequestStatus", "STRING:request.status.last")
      parser.addParseTarget("setResponseBodyBytes", "BYTES:response.body.bytes")
      parser.addParseTarget("setReferer", "HTTP.URI:request.referer")
      parser.addParseTarget("setUserAgent", "HTTP.USERAGENT:request.user-agent")
      parser.addParseTarget("setReferDomain", "HTTP.HOST:request.referer.host")

      //返回点击流日志的解析器
      parser
    }

  def main(args: Array[String]): Unit = {
    //定义点击流日志的数据样本
    val logline = "2001:980:91c0:1:8d31:a232:25e5:85d 222.68.172.190 - [05/Sep/2010:11:27:50 +0200] \"GET /images/my.jpg HTTP/1.1\" 404 23617 \"http://www.angularjs.cn/A00n\" \"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; nl-nl) AppleWebKit/533.17.8 (KHTML, like Gecko) Version/5.0.1 Safari/533.17.8\""

    //解析数据
    val clickLogEntity = new ClickLogEntity()
    val parser: Parser[ClickLogEntity] = createClickLogParse()
    parser.parse(clickLogEntity, logline)

    println(clickLogEntity.getConnectionClientUser)
    println(clickLogEntity.getIp)
    println(clickLogEntity.getRequestTime)
    println(clickLogEntity.getMethod)
    println(clickLogEntity.getResolution)
    println(clickLogEntity.getRequestProtocol)
    println(clickLogEntity.getRequestStatus)
    println(clickLogEntity.getResponseBodyBytes)
    println(clickLogEntity.getReferer)
    println(clickLogEntity.getUserAgent)
    println(clickLogEntity.getReferDomain)
  }
}

/**
 * 定义拉宽后的点击流日志对象
 * 因为后续需要将拉宽后的点击流对象序列化成json字符串保存到kafka集群，所以需要实现属性的set和get方法
 */
case class ClickLogWideEntity(
                               @BeanProperty uid:String, //用户id
                               @BeanProperty ip:String, //ip地址
                               @BeanProperty requestTime:String, //访问时间
                               @BeanProperty requestMethod:String, //请求方式
                               @BeanProperty requestUrl:String, //请求地址
                               @BeanProperty requestProtocol:String, //请求协议
                               @BeanProperty requestStatus:String, //响应码
                               @BeanProperty requestBodyBytes:String, //返回的数据流量
                               @BeanProperty referer:String, //访客来源
                               @BeanProperty userAgent:String, //用户代理信息
                               @BeanProperty refererDomain:String, //跳转过来的域名
                               @BeanProperty var province:String, //拓宽后的字段：省份
                               @BeanProperty var city:String, //拓宽后的字段：城市
                               @BeanProperty var requestDateTime:String //拓宽后的字段：时间戳
                             )
object ClickLogWideEntity{
  def apply(clickLogEntity: ClickLogEntity): ClickLogWideEntity = {

      ClickLogWideEntity(
        clickLogEntity.getConnectionClientUser,
        clickLogEntity.getIp,
        clickLogEntity.getRequestTime,
        clickLogEntity.getMethod,
        clickLogEntity.getResolution,
        clickLogEntity.getRequestProtocol,
        clickLogEntity.getRequestStatus,
        clickLogEntity.getResponseBodyBytes,
        clickLogEntity.getReferer,
        clickLogEntity.getUserAgent,
        clickLogEntity.getReferDomain,
        "",
        "",
        ""
      )
    }
}
