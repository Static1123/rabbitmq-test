package com.yl.rabbitmq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * @author Liang.Yang5
 * @date 2018/9/10 14:27
 */
@Component
public class TestListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        System.out.println("TestListener onMessage,message=[" + message + "],channel=[" + channel + "]");
    }
}