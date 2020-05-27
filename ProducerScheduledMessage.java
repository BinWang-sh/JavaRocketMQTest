package com.example.rocketmq.rocketmqdemo.app;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.Random;

public class ProducerScheduledMessage {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("BinProducerGroup");
        Random r = new Random();
        producer.setInstanceName("BinTestInstance_" + r.nextInt(1000));
        producer.setNamesrvAddr("106.14.186.226:7060");
        producer.setSendMsgTimeout(30000);
        producer.setMaxMessageSize(500000);
        producer.setRetryTimesWhenSendFailed(3);

        try {
            producer.start();
            Thread.sleep(8000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int totalMessagesToSend = 100;
        for (int i = 0; i < totalMessagesToSend; i++) {
            Message message = new Message("BinTestTopic02", ("Scheduled message_10s_" + i).getBytes());
            // 延时等级时长如下,从1到18
            //private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
            message.setDelayTimeLevel(3); // 10s
            // 发送消息
            SendResult result = producer.send(message);
            System.out.println("Send result:" + result);
        }
        // 关闭生产者
        producer.shutdown();
    }
}
