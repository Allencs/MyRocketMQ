package com.allen.myrocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public class ConsumerService {

    private static Logger logger = LoggerFactory
            .getLogger(ConsumerService.class);

    protected DefaultMQPushConsumer consumer = null;

    @Autowired
    MqConfig mqConfig;

    @PostConstruct
    public void initMQConsumer() {
        consumer = new DefaultMQPushConsumer("MyRocketMQConsumer");
        consumer.setNamesrvAddr(mqConfig.NameServer);
        //消费模式:一个新的订阅组第一次启动从队列的最后位置开始消费 后续再启动接着上次消费的进度开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        //订阅主题和 标签（ * 代表所有标签)下信息
        try {
            consumer.subscribe(mqConfig.Topic, "*");
        }catch (MQClientException e) {
            e.printStackTrace();
        }

        //注册消费的监听 并在此监听中消费信息，并返回消费的状态信息
//        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
//            // msgs中只收集同一个topic，同一个tag，并且key相同的message
//            // 会把不同的消息分别放置到不同的队列中
//            try {
//                for (Message msg : msgs) {
//                    //消费者获取消息 这里只输出 不做后面逻辑处理
//                    String body = new String(msg.getBody(), "utf-8");
//                    logger.info("Consumer-获取消息-主题topic为={}, 消费消息为={}", msg.getTopic(), body);
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
//            }
//            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//        });

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
                                                            ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                try {
                    //MessageExt
                    MessageExt messageExt = list.get(0);
                    String topic = messageExt.getTopic();
                    String message = new String(messageExt.getBody(),"UTF-8");
                    int queueId = messageExt.getQueueId();
                    System.out.println("收到来自topic:" + topic + ", queueId:" + queueId + "的消息：" + message);

                } catch (Exception e) {
                    //失败，请求稍后重发
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                //成功
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

    }

    @PreDestroy
    public void shutDownConsumer(){
        if (consumer != null) {
            consumer.shutdown();
        }
    }
}
