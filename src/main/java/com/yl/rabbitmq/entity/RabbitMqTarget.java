package com.yl.rabbitmq.entity;

import com.yl.rabbitmq.service.impl.SimpleRabbitServiceImpl.ExchangeType;
import lombok.Data;

/**
 * @author admin
 * 消息发送对象类型(direct|topic|fanout)
 */
@Data
public class RabbitMqTarget {

    /**
     * <p>返回一个向指定queue发送消息的信息对象，其中exchange默认与queue名称一致，
     * exchangeType为direct类型。</p>
     *
     * @param queueName queue名称
     * @return 向指定queue发送数据的信息类型对象
     */
    public static RabbitMqTarget createDirectTarget(String queueName) {
        return new RabbitMqTarget(queueName, queueName, ExchangeType.DIRECT, queueName);
    }

    /**
     * <p>返回一个向指定queue发送消息的信息对象，exchangeType为fanout类型，
     * 所有指定的queue都将接收到发送的消息。</p>
     *
     * @param exchangeName exchange名称
     * @param queueNames   接收exchange消息的队列名称
     * @return
     */
    public static RabbitMqTarget createFanoutTarget(String exchangeName, String... queueNames) {
        return new RabbitMqTarget(exchangeName, null, ExchangeType.FANOUT, queueNames);
    }

    /**
     * <p>返回一个向指定queue发送消息的信息对象，exchangeType为topic类型，
     * 所有指定的queue都将接收到发送的消息。</p>
     *
     * @param exchangeName
     * @param routingKey
     * @param queueNames
     * @return
     */
    public static RabbitMqTarget createTopicTarget(String exchangeName, String routingKey, String... queueNames) {
        return new RabbitMqTarget(exchangeName, routingKey, ExchangeType.TOPIC, queueNames);
    }

    private String[] queueNames;

    private String exchangeName;

    private String routingKey;

    private ExchangeType exchangeType;

    protected RabbitMqTarget() {

    }

    protected RabbitMqTarget(String exchangeName, String routingKey, ExchangeType exchangeType,
                             String... queueNames) {
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.exchangeType = exchangeType;
        this.queueNames = queueNames;
    }
}