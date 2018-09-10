package com.yl.rabbitmq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @date 2018/9/7 15:35
 */
@Component
public class DirectSendListener extends AbstractRabbitMessageListener<String> {
    public DirectSendListener() {
        super("rabbitmq-test");
        this.setAcknowledgeMode(ACK_MODE_MANUAL);
    }

    @Override
    public void doHandleMessage(String object) {
        System.out.println("DirectSendListener:doHandleMessage:msg=[" + object + "]");
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

    }
}