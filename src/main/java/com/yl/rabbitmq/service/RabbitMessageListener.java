package com.yl.rabbitmq.service;

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
     * 业务处理方法
     *
     * @param messageData
     */
    void handleMessage(Object messageData);

    /**
     * ack model
     *
     * @return AcknowledgeMode
     */
    int getAcknowledgeMode();
}