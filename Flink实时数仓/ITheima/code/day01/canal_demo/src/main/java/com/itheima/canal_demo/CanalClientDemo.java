package com.itheima.canal_demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.itheima.cana_demo.CanalModel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * canal客户端程序，canal是cs架构的，sever端不需要编写代码，直接部署到linux服务器即可
 * 客户端程序需要开发，连接服务端，与canal服务端进行交互
 */
public class CanalClientDemo {
    public static void main(String[] args) {
        /**
         * 实现步骤：
         * 1：创建连接
         * 2：建立连接
         * 3：订阅主题
         * 4：获取数据
         * 5：递交确认
         * 6：关闭连接
         */
        //1：创建连接
//        CanalConnector canalConnector = CanalConnectors.newSingleConnector(new InetSocketAddress("node1", 11111),
//                "example", "", "");
        CanalConnector canalConnector = CanalConnectors.newClusterConnector("node1:2181", "example",
                "", "");

        //指定每次拉取数据的数量
        int batchSize = 1000;
        //定义一个标记不停的拉取数据
        boolean isRunning = true;
        try {
            //2：建立连接
            canalConnector.connect();
            //设置回滚上一次的get请求，重新获取数据
            canalConnector.rollback();

            //3：订阅主题
            canalConnector.subscribe("itcast_shop.*");

            //不停的拉取数据
            while (isRunning){
                //4：获取数据
                Message message = canalConnector.getWithoutAck(batchSize);
                //获取batch的id
                long batchId = message.getId();

                //获取binlog日志的数据总数
                //getEntries封装了一千条数据的对象集合
                int size = message.getEntries().size();
                if(size == 0 || size == -1){
                    //没有获取到数据，不需要处理数据了
                }else{
                    //拉取到了数据，对数据进行处理
                    //printSummary(message);
                    String json = binlogToJson(message);
                    System.out.println(json);

                    //将消费到的binlog日志转换成字符串，将字符串写入到kafka中
                    /**
                     * kafka可以写入的数据格式：
                     * 1：字符串类型
                     * 2：二进制字节码数据
                     */
                    //因为存储到kafka的数据是json格式的话，需要占用太多的存储资源和网络资源，所以需要将存储的数据格式化成占用资源比较小的数据存储格式
                    //protobuf存储格式
                    //1：将message对象转换成protobuf的字节码数据
                    byte[] bytes = binlogToProtoBuf(message);
                    //2：将字节码数据写入到kafka集群中

                }

                //5：递交确认
                canalConnector.ack(batchId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            canalConnector.disconnect();
        }
    }

    // binlog解析为ProtoBuf
    private static byte[] binlogToProtoBuf(Message message) throws InvalidProtocolBufferException {
        // 1. 构建CanalModel.RowData实体
        CanalModel.RowData.Builder rowDataBuilder = CanalModel.RowData.newBuilder();

        // 1. 遍历message中的所有binlog实体
        for (CanalEntry.Entry entry : message.getEntries()) {
            // 只处理事务型binlog
            if(entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN ||
                    entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            // 获取binlog文件名
            String logfileName = entry.getHeader().getLogfileName();
            // 获取logfile的偏移量
            long logfileOffset = entry.getHeader().getLogfileOffset();
            // 获取sql语句执行时间戳
            long executeTime = entry.getHeader().getExecuteTime();
            // 获取数据库名
            String schemaName = entry.getHeader().getSchemaName();
            // 获取表名
            String tableName = entry.getHeader().getTableName();
            // 获取事件类型 insert/update/delete
            String eventType = entry.getHeader().getEventType().toString().toLowerCase();

            rowDataBuilder.setLogfileName(logfileName);
            rowDataBuilder.setLogfileOffset(logfileOffset);
            rowDataBuilder.setExecuteTime(executeTime);
            rowDataBuilder.setSchemaName(schemaName);
            rowDataBuilder.setTableName(tableName);
            rowDataBuilder.setEventType(eventType);

            // 获取所有行上的变更
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            List<CanalEntry.RowData> columnDataList = rowChange.getRowDatasList();
            for (CanalEntry.RowData rowData : columnDataList) {
                if(eventType.equals("insert") || eventType.equals("update")) {
                    for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                        rowDataBuilder.putColumns(column.getName(), column.getValue().toString());
                    }
                }
                else if(eventType.equals("delete")) {
                    for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                        rowDataBuilder.putColumns(column.getName(), column.getValue().toString());
                    }
                }
            }
        }

        return rowDataBuilder.build().toByteArray();
    }

    // binlog解析为json字符串
    private static String binlogToJson(Message message) throws InvalidProtocolBufferException {
        // 1. 创建Map结构保存最终解析的数据
        Map rowDataMap = new HashMap<String, Object>();

        // 2. 遍历message中的所有binlog实体
        for (CanalEntry.Entry entry : message.getEntries()) {
            // 只处理事务型binlog
            if(entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN ||
                    entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            // 获取binlog文件名
            String logfileName = entry.getHeader().getLogfileName();
            // 获取logfile的偏移量
            long logfileOffset = entry.getHeader().getLogfileOffset();
            // 获取sql语句执行时间戳
            long executeTime = entry.getHeader().getExecuteTime();
            // 获取数据库名
            String schemaName = entry.getHeader().getSchemaName();
            // 获取表名
            String tableName = entry.getHeader().getTableName();
            // 获取事件类型 insert/update/delete
            String eventType = entry.getHeader().getEventType().toString().toLowerCase();

            rowDataMap.put("logfileName", logfileName);
            rowDataMap.put("logfileOffset", logfileOffset);
            rowDataMap.put("executeTime", executeTime);
            rowDataMap.put("schemaName", schemaName);
            rowDataMap.put("tableName", tableName);
            rowDataMap.put("eventType", eventType);

            // 封装列数据
            Map columnDataMap = new HashMap<String, Object>();
            // 获取所有行上的变更
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            List<CanalEntry.RowData> columnDataList = rowChange.getRowDatasList();
            for (CanalEntry.RowData rowData : columnDataList) {
                if(eventType.equals("insert") || eventType.equals("update")) {
                    for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                        columnDataMap.put(column.getName(), column.getValue());
                    }
                }
                else if(eventType.equals("delete")) {
                    for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                        columnDataMap.put(column.getName(), column.getValue());
                    }
                }
            }

            rowDataMap.put("columns", columnDataMap);
        }

        return JSON.toJSONString(rowDataMap);
    }

    //解析获取到的message对象
    private static void printSummary(Message message) {
        // 遍历整个batch中的每个binlog实体
        //entry就是mysql中的一条记录
        for (CanalEntry.Entry entry : message.getEntries()) {
            // 事务开始，对于事务的开始和结束，不需要进行处理，因为处理的是数据本身
            if(entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
                    || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            //获取到的数据RowData
            // 获取binlog文件名
            String logfileName = entry.getHeader().getLogfileName();
            // 获取logfile的偏移量
            long logfileOffset = entry.getHeader().getLogfileOffset();
            // 获取sql语句执行时间戳
            long executeTime = entry.getHeader().getExecuteTime();
            // 获取数据库名
            String schemaName = entry.getHeader().getSchemaName();
            // 获取表名
            String tableName = entry.getHeader().getTableName();
            // 获取事件类型 insert/update/delete
            String eventTypeName = entry.getHeader().getEventType().toString().toLowerCase();

            System.out.println("logfileName" + ":" + logfileName);
            System.out.println("logfileOffset" + ":" + logfileOffset);
            System.out.println("executeTime" + ":" + executeTime);
            System.out.println("schemaName" + ":" + schemaName);
            System.out.println("tableName" + ":" + tableName);
            System.out.println("eventTypeName" + ":" + eventTypeName);

            CanalEntry.RowChange rowChange = null;

            try {
                // 获取存储数据，并将二进制字节数据解析为RowChange实体
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (InvalidProtocolBufferException e) {
                //解析数据的时候会抛出一个ProtocolBufferException异常，很明显这个数据是protobuf的数据格式
                //所以可以确定，canal中传递的数据就是potobuf格式数据
                e.printStackTrace();
            }

            // 迭代每一条变更数据
            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                // 判断是否为删除事件
                if(entry.getHeader().getEventType() == CanalEntry.EventType.DELETE) {
                    System.out.println("---delete---");
                    printColumnList(rowData.getBeforeColumnsList());
                    System.out.println("---");
                }
                // 判断是否为更新事件
                else if(entry.getHeader().getEventType() == CanalEntry.EventType.UPDATE) {
                    System.out.println("---update---");
                    printColumnList(rowData.getBeforeColumnsList());
                    System.out.println("---");
                    printColumnList(rowData.getAfterColumnsList());
                }
                // 判断是否为插入事件
                else if(entry.getHeader().getEventType() == CanalEntry.EventType.INSERT) {
                    System.out.println("---insert---");
                    printColumnList(rowData.getAfterColumnsList());
                    System.out.println("---");
                }
            }
        }
    }

    // 打印所有列名和列值
    private static void printColumnList(List<CanalEntry.Column> columnList) {
        //遍历的是一条数据的所有的列
        for (CanalEntry.Column column : columnList) {
            System.out.println(column.getName() + "\t" + column.getValue());
        }
    }
}
