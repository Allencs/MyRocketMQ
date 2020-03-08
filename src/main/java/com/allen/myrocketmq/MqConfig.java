package com.allen.myrocketmq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rocketmq")
public class MqConfig {

    protected String NameServer;
    protected String Topic;

    public String getNameServer() {
        return NameServer;
    }

    public void setNameServer(String nameServer) {
        NameServer = nameServer;
    }

    public String getTopic() {
        return Topic;
    }

    public void setTopic(String topic) {
        Topic = topic;
    }
}
