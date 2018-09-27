package com.yl.rabbitmq.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

/**
 * 消息队列监听接口
 *
 * @author admin
 */
public interface RabbitMessageListener {

    int ACK_MODE_AUTO = 0;

    int ACK_MODE_MANUAL = 1;

    int ACK_MODE_NONE = 2;

    /**
     * 监听queue名称
     *
     * @return
     */
    String getTargetQueueName();

    /**
     * ack model
     *
     * @return AcknowledgeMode
     */
    int getAcknowledgeMode();

    /**
     * 处理发送消息
     *
     * @param messageData
     * @param channel
     */
    void handleMessage(Message messageData, Channel channel);
}