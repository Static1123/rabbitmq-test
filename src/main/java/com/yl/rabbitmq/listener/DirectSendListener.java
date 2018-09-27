package com.yl.rabbitmq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

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
    public void handleMessage(Message messageData, Channel channel) {
        System.out.println(messageData.getMessageProperties().getDeliveryTag());
        System.out.println((new Date()) + ":DirectSendListener:doHandleMessage:msg=[" + messageData + "]");
        String msg = new String(messageData.getBody());
        try {
            if (StringUtils.isEmpty(msg)) {
                channel.basicReject(messageData.getMessageProperties().getDeliveryTag(), false);
            } else {
                if (msg.equalsIgnoreCase("geely111")) {
                    channel.basicAck(messageData.getMessageProperties().getDeliveryTag(), false);
                } else {
//                    channel.basicRecover(true);
                    //退回消息到队列
                    channel.basicReject(messageData.getMessageProperties().getDeliveryTag(), true);
//                    Thread.sleep(10000);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}