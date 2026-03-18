package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/03/20 14:41
 **/
public class producer {

    public static final Logger logger = LoggerFactory.getLogger(producer.class);

    public static void main(String[] args) {

//        第 1 步：构造生产者对象所需的参数对象。
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        第 2 步：利用第 1 步的参数对象，创建 KafkaProducer 对象实例。
        try (Producer producer = new KafkaProducer<>(properties)) {
//            第 3 步：使用 KafkaProducer 的 send 方法发送消息。
//            第 4 步：调用 KafkaProducer 的 close 方法关闭生产者并释放各种系统资源。
            producer.send(new ProducerRecord("test", "aaaaaaaaaa"), (metadata, exception) -> {
                if (metadata.hasOffset()) {
                    logger.info("offset = {}", metadata.offset());
                }
            });

        }
    }
}