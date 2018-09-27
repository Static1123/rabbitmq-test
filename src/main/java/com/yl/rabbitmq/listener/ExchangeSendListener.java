package com.yl.rabbitmq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @date 2018/9/7 15:43
 */
@Component
public class ExchangeSendListener extends AbstractRabbitMessageListener<String> {
    public ExchangeSendListener() {
        super("exchange1");
        this.setAcknowledgeMode(ACK_MODE_MANUAL);
    }

    @Override
    public void handleMessage(Message messageData, Channel channel) {

    }
}