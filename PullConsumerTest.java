package com.example.rocketmq.rocketmqdemo.app;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PullConsumerTest {
    private static final Map<MessageQueue, Long> offsetTable = new HashMap<MessageQueue, Long>();
    private final static long TOTAL_MSG_COUNT = 50000;
    private final static int CPU_SIZE = Runtime.getRuntime().availableProcessors();
    private static long msgCnt = 0;
    private static long startTime;
    private static long endTime;

    public static void main(String[] args) throws MQClientException {
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("BinConsumerGroup");
        consumer.setNamesrvAddr("106.14.186.226:7060");
        consumer.start();
        Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("BinTestTopic02");
        for (MessageQueue mq : mqs) {
            System.err.println("Consume from the queue: " + mq);
            startTime = System.currentTimeMillis();
            SINGLE_MQ:
            while (true) {
                try {
                    PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);
                    System.out.println(pullResult);
                    putMessageQueueOffset(mq, pullResult.getNextBeginOffset());

                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            List<MessageExt> messageExtList = pullResult.getMsgFoundList();
                            for (MessageExt m : messageExtList) {
                                //System.out.println(new String(m.getBody()));
                                msgCnt++;
                                if (msgCnt>=TOTAL_MSG_COUNT) {
                                    msgCnt=0;
                                    endTime = System.currentTimeMillis();
                                    //consumer.shutdown();
                                    System.out.println("-------------Consume " + TOTAL_MSG_COUNT + " messages spent " + (endTime-startTime)/1000);
                                    startTime = System.currentTimeMillis();
                                    System.out.println("************Start time: " + startTime);
                                }
                            }
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                            break SINGLE_MQ;
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        consumer.shutdown();
    }

    private static void putMessageQueueOffset(MessageQueue mq, long offset) {
        offsetTable.put(mq, offset);
    }

    private static long getMessageQueueOffset(MessageQueue mq) {
        Long offset = offsetTable.get(mq);
        if (offset != null) {
            return offset;
        }
        return 0;
    }

}
