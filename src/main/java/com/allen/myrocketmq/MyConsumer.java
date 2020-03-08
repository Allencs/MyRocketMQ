package com.allen.myrocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;


@RestController
public class MyConsumer {

    private static Logger logger = LoggerFactory
            .getLogger(MyConsumer.class);

    @Resource
    private ConsumerService consumerService;

    @RequestMapping(value = "/consume", method = RequestMethod.GET)
    private String consume() throws MQClientException {
        consumerService.consumer.start();
//        logger.info("Consumer start successfully...");
        return "消费者 启动成功=======";
    }
}
