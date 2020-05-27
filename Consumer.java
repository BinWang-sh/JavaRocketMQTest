package com.example.rocketmq.rocketmqdemo.app;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;
import java.util.Random;

public class Consumer {

    private final static long MAX_COUNT = 20000;
    public static void main(String[] args) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("BinConsumerGroup");
        Random r = new Random();
        String instanceName = "ConsumerInstance" + r.nextInt(100);
        consumer.setInstanceName(instanceName);
        consumer.setNamesrvAddr("106.14.186.226:7060");
        consumer.setConsumeTimeout(20000);

        consumer.registerMessageListener(new MessageListenerOrderly() {

            private long count = 0;
            private long startTime = 0L;
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {

                if(startTime==0L) {
                    startTime = System.currentTimeMillis();
                }

                context.setAutoCommit(true);
                for (MessageExt msg : msgs) {
                    // 可以看到每个queue有唯一的consume线程来消费, 订单对每个queue(分区)有序
                    System.out.print(".");
                    count++;

                    if (count>=MAX_COUNT) {
                        long endTime = System.currentTimeMillis();
                        count=0;
                        System.out.println("");
                        System.out.println("Consume " + MAX_COUNT + " messages cost :" + (endTime - startTime) + "ms");
                        startTime = System.currentTimeMillis();
                    }
                    //System.out.println("consumeThread=" + Thread.currentThread().getName() + "queueId=" + msg.getQueueId() + ", content:" + new String(msg.getBody()));
                }

                return ConsumeOrderlyStatus.SUCCESS;
            }
        });

        try {
            consumer.subscribe("BinTestTopic02", "*");
            consumer.setMessageModel(MessageModel.CLUSTERING);
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.setConsumeMessageBatchMaxSize(1);
            consumer.start();
            Thread.sleep(5000);
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
}