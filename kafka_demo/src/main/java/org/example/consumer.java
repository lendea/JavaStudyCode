package org.example;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/03/20 17:09
 **/
public class consumer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "2000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("test", "bar"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
        }

//        while (true) {
//            ConsumerRecords<String, String> records =
//                    consumer.poll(Duration.ofSeconds(1));
//            process(records); // 处理消息
//            try {
//                consumer.commitSync();
//            } catch (CommitFailedException e) {
//                handle(e); // 处理提交失败异常
//            }
//        }


//        while (true) {
//            ConsumerRecords<String, String> records =
//                    consumer.poll(Duration.ofSeconds(1));
//            process(records); // 处理消息
//            consumer.commitAsync((offsets, exception) -> {
//                if (exception != null)
//                    handle(exception);
//            });
//        }


//        try {
//            while(true) {
//                ConsumerRecords<String, String> records =
//                        consumer.poll(Duration.ofSeconds(1));
//                process(records); // 处理消息
//                commitAysnc(); // 使用异步提交规避阻塞
//            }
//        } catch(Exception e) {
//            handle(e); // 处理异常
//        } finally {
//            try {
//                consumer.commitSync(); // 最后一次提交使用同步阻塞式提交
//            } finally {
//                consumer.close();
//            }
//        }


//        private Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
//        int count = 0;
//……
//        while (true) {
//            ConsumerRecords<String, String> records =
//                    consumer.poll(Duration.ofSeconds(1));
//            for (ConsumerRecord<String, String> record: records) {
//                process(record);  // 处理消息
//                offsets.put(new TopicPartition(record.topic(), record.partition()),
//                        new OffsetAndMetadata(record.offset() + 1)；
//                if（count % 100 == 0）
//                consumer.commitAsync(offsets, null); // 回调处理逻辑是null
//                count++;
//            }
//        }



    }
}
