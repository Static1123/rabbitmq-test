package com.yl.rabbitmq.listener;

import org.springframework.stereotype.Component;

/**
 * @author admin
 * @date 2018/9/7 15:43
 */
@Component
public class ExchangeSendListener extends AbstractRabbitMessageListener<String> {
    public ExchangeSendListener() {
        super("exchange1");
    }

    @Override
    public void doHandleMessage(String object) {
        System.out.println("ExchangeSendListener:doHandleMessage:msg=[" + object + "]");
    }
}