package com.allen.myrocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Service
public class ProducerService {

    private DefaultMQProducer producer = null;

    @Autowired
    MqConfig mqConfig;

    @PostConstruct  // 在Bean初始化之后（构造方法和@Autowired之后）执行指定操作
    public void initMQProducer() {
        producer = new DefaultMQProducer("MyRocketMQProducer");
        producer.setNamesrvAddr(mqConfig.NameServer);
        producer.setRetryTimesWhenSendFailed(3);
        try {
            producer.start();
        }catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public void send(String topic, String tag, String keys, String msg) {
        Message message = new Message(topic, tag, keys, msg.getBytes());
        try {
            producer.send(message);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy  // 在关闭spring容器后释放资源, 类似DisposableBean接口的destroy方法
    public void shutDownProducer() {
        if (producer != null) {
            producer.shutdown();
        }
    }
}
