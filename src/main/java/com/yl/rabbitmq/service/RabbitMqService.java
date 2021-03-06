package com.yl.rabbitmq.service;

import com.yl.rabbitmq.entity.RabbitMqTarget;

import java.util.Collection;

/**
 * 消息队列接口
 *
 * @author admin
 */
public interface RabbitMqService {

    /**
     * 信息发送(exchangeType:direct)
     *
     * @param queueName queueName名称
     * @param data      发送数据对象
     */
    void send(String queueName, Object data);

    /**
     * 信息发送
     *
     * @param target 信息类型对象
     * @param data   数据对象
     */
    void send(RabbitMqTarget target, Object data);


    /**
     * 注册消费者
     *
     * @param l 消费者实例
     */
    void listen(RabbitMessageListener l);

    /**
     * 批量注册消费者
     *
     * @param messageListenerCollection
     */
    void listen(Collection<RabbitMessageListener> messageListenerCollection);

    /**
     * 注册消费者
     *
     * @param l
     * @param concurrentConsumers
     */
    void listen(RabbitMessageListener l, Integer concurrentConsumers);
}