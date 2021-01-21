package cn.itheima.data_simulator.util;

import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

public class KafkaUtil {
    public static KafkaProducer producer;
    static {
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", ConfigReader.kafka_bootstrap_servers);
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("acks", "all");
        kafkaProps.put("retries", 0);
        kafkaProps.put("batch.size", 16384);
        kafkaProps.put("linger.ms", 1);
        kafkaProps.put("buffer.memory", 33554432);

        producer = new KafkaProducer<String, String>(kafkaProps);
    }
}
