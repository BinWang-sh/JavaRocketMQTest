package com.example.rocketmq.rocketmqdemo.app;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Random;

@SpringBootApplication
public class PushConsumerTest {
    private final static long TOTAL_MSG_COUNT = 50000;
    private final static int CPU_SIZE = Runtime.getRuntime().availableProcessors();
    private static long msgCnt = 0;
    private static long startTime;
    private static long endTime;

    public static void main(String[] args) {
        System.out.println("------------Start push consume ----------------");

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("BinConsumerGroup");
        Random r = new Random();
        String instanceName = "ConsumerInstance" + r.nextInt(100);
        consumer.setInstanceName(instanceName);
        consumer.setNamesrvAddr("106.14.186.226:7060");
        consumer.setConsumeThreadMin(CPU_SIZE);
        consumer.setConsumeThreadMax(CPU_SIZE);
        consumer.setConsumeTimeout(20000);
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {

                System.out.println(Thread.currentThread().getName() + " Receive New Messages" );

                msgs.forEach(msg-> {
                    System.out.println(Thread.currentThread().getName() + " Receive New Messages: " + new String(msg.getBody()));
                    msgCnt++;
                    //System.out.println(msgCnt+"");
                    if (msgCnt>=TOTAL_MSG_COUNT) {
                        msgCnt=0;
                        endTime = System.currentTimeMillis();
                        //consumer.shutdown();
                        System.out.println("-------------Consume " + TOTAL_MSG_COUNT + " messages spent " + (endTime-startTime)/1000);
                        startTime = System.currentTimeMillis();
                        System.out.println("************Start time: " + startTime);
                    }
                });

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        try {
            consumer.subscribe("BinTestTopic02", "*");
            consumer.setMessageModel(MessageModel.CLUSTERING);
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.setConsumeMessageBatchMaxSize(10);
            startTime = System.currentTimeMillis();
            System.out.println("************Start time: " + startTime);
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

}
