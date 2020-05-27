# JavaRocketMQTest
RocketMQTest(PushConsumer, PullConsumer, Producer)

### RocketMQ要点内容
#### 消费方式
* 推送式消费 (Push Consumer)
Broker收到数据后会主动推送给消费端

* 拉取式消费（Pull Consumer）
主动调用Consumer的拉消息方法从Broker服务器拉消息、主动权由应用控制。一旦获取了批量消息，应用就会启动消费过程。

#### 定时消息
定时消息（延迟队列）是指消息发送到broker后，不会立即被消费，等待特定时间投递给真正的topic。
delayLevel有18个level，对应时间分别为：“1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h”，
messageDelayLevel是broker的属性，不属于某个topic。
发消息时，设置delayLevel等级即可：msg.setDelayLevel(level)

* level == 0，消息为非延迟消息
* 1<=level<=maxLevel，消息延迟特定时间，例如level==1，延迟1s
* level > maxLevel，则level== maxLevel，例如level==20，延迟2h

#### 消息模式
* 集群消费（Cluster）
相同Consumer Group的每个Consumer实例平均分摊消息

* 广播消费 (Broadcasting)
相同Consumer Group的每个Consumer实例都接收全量的消息

#### 消息顺序
* 普通顺序消息 (Normal Ordered Message)
消费者通过同一个消费队列收到的消息是有顺序的，不同消息队列收到的消息则可能是无顺序的

* 严格顺序消息 (Strictly Ordered Message)
消费者收到的所有消息均是有顺序的

#### 消息重试
* 重试队列:RocketMQ会为每个消费组都设置一个Topic名称为“%RETRY%+consumerGroup”的重试队列。这个Topic的重试队列是针对消费组，而不是针对每个Topic设置的
因为异常恢复起来需要一些时间，会为重试队列设置多个重试级别，每个重试级别都有与之对应的重新投递延时，重试次数越多投递延时就越大。
RocketMQ对于重试消息的处理是先保存至Topic名称为“SCHEDULE_TOPIC_XXXX”的延迟队列中，后台定时任务按照对应的时间进行Delay后重新保存至“%RETRY%+consumerGroup”的重试队列中。

* 死信队列:用于处理无法被正常消费的消息。当一条消息初次消费失败，消息队列会自动进行消息重试；达到最大重试次数后，若消费依然失败，此时，消息队列 不会立刻将消息丢弃，而是将其发送到该消费者对应的特殊队列中。
RocketMQ将这种正常情况下无法被消费的消息称为死信消息（Dead-Letter Message），将存储死信消息的特殊队列称为死信队列（Dead-Letter Queue）。在RocketMQ中，可以通过使用console控制台对死信队列中的消息进行重发来使得消费者实例再次进行消费。
