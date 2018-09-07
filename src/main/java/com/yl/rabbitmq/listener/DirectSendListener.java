package com.yl.rabbitmq.listener;

import com.yl.rabbitmq.service.impl.AbstractRabbitMessageListener;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @date 2018/9/7 15:35
 */
@Component
public class DirectSendListener extends AbstractRabbitMessageListener<String> {
    public DirectSendListener() {
        super("rabbitmq-test");
    }

    @Override
    public void doHandleMessage(String object) {
        System.out.println("DirectSendListener:doHandleMessage:msg=[" + object + "]");
    }
}