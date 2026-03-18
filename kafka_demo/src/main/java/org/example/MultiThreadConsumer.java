package org.example;

import javafx.concurrent.Worker;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/03/21 14:16
 **/
public class MultiThreadConsumer {
    public static void main(String[] args) {

    }
}

class KafkaConsumerRunner implements Runnable {
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final KafkaConsumer consumer;

    public KafkaConsumerRunner(KafkaConsumer consumer) {
        this.consumer = consumer;
    }

    public void run() {
        try {
            consumer.subscribe(Arrays.asList("topic"));
            while (!closed.get()) {
                ConsumerRecords records =
                        consumer.poll(Duration.ofMillis(10000));
                //  执行消息处理逻辑
            }
        } catch (WakeupException e) {
            // Ignore exception if closing
            if (!closed.get()) throw e;
        } finally {
            consumer.close();
        }
    }


    // Shutdown hook which can be called from a separate thread
    public void shutdown() {
        closed.set(true);
        consumer.wakeup();
    }

}

class KafkaMultiConsumer {
    private final KafkaConsumer<String, String> consumer;
    private ExecutorService executors;
    private int workerNum = 2;

    public KafkaMultiConsumer(KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;
        executors = new ThreadPoolExecutor(
                workerNum, workerNum, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void run() {
        while (true) {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofSeconds(1));
            for (final ConsumerRecord record : records) {
//                executors.submit(new Worker(record));
            }
        }
    }
}