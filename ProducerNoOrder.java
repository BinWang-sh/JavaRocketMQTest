package com.example.rocketmq.rocketmqdemo.app;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.Random;

public class ProducerNoOrder {
    final static long MAX_COUNT = 100100;
    public static void main(String[] args) {

        DefaultMQProducer producer = new DefaultMQProducer("BinProducerGroup");
        Random r = new Random();
        producer.setInstanceName("BinTestInstance_" + r.nextInt(1000));
        producer.setNamesrvAddr("106.14.186.226:7060");
        producer.setSendMsgTimeout(30000);
        producer.setMaxMessageSize(500000);
        producer.setRetryTimesWhenSendFailed(3);

        try {
            producer.start();
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }


        for(int i=0;i<MAX_COUNT;i++) {
            try {
                String msg = "message"+i;
                Message sendMsg = new Message("BinTestTopic02",
                        "rocketTag01",
                        "key"+i,
                        msg.getBytes());
                //mqProducer.sendOneway(sendMsg); //Only send message, don't care about send result (such as send log).
                //sendResult = mqProducer.send(sendMsg);// Sync send

                producer.send(sendMsg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        System.out.println("Sent ok:"+sendResult);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        System.out.println("Sent failed:" + throwable.getMessage());
                    }
                });
                Thread.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
