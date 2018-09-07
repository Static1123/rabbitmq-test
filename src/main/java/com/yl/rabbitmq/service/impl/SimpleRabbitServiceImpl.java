package com.yl.rabbitmq.service.impl;

import com.google.common.collect.Sets;
import com.yl.rabbitmq.entity.RabbitMqTarget;
import com.yl.rabbitmq.service.RabbitMessageListener;
import com.yl.rabbitmq.service.RabbitMqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
public class SimpleRabbitServiceImpl implements RabbitMqService {

    private static final Logger logger = LoggerFactory.getLogger(SimpleRabbitServiceImpl.class);

    protected static final long DEFAULT_SLEEP_MILLIS = 30;

    private ConnectionFactory rabbitMQConnectionFactory;

    private RabbitTemplate rabbitTemplate;

    private RabbitAdmin rabbitAdmin;

    private Set<String> declaredQueues = Sets.newHashSet();

    private Set<String> declaredExchangeAndQueues = Sets.newHashSet();

    private MessageConverter messageConverter;

    public SimpleRabbitServiceImpl() {
    }

    public SimpleRabbitServiceImpl(ConnectionFactory cf, RabbitTemplate rt, RabbitAdmin admin) {
        this(cf, rt, admin, null);
    }

    public SimpleRabbitServiceImpl(ConnectionFactory cf, RabbitTemplate rt, RabbitAdmin admin, MessageConverter mc) {
        this.rabbitMQConnectionFactory = cf;
        this.rabbitTemplate = rt;
        this.rabbitAdmin = admin;
        this.messageConverter = mc;
        if (this.messageConverter != null) {
            rabbitTemplate.setMessageConverter(messageConverter);
        }
    }

    /**
     * 信息发送(exchangeType:direct)
     *
     * @param queueName queue名称
     * @param data      发送数据对象
     */
    @Override
    public void send(String queueName, Object data) {
        send(queueName, queueName, ExchangeType.DIRECT, data, queueName);
    }

    /**
     * 信息发送
     *
     * @param target 消息类型对象，详见{@RabbitMqTarget}
     * @param data   数据对象
     */
    @Override
    public void send(RabbitMqTarget target, Object data) {
        this.send(target.getExchangeName(), target.getRoutingKey(), target.getExchangeType(), data, target.getQueueNames());
    }

    protected void send(String exchangeName, String routingKey, ExchangeType exchangeType, Object data, String... queueNames) {
        if (StringUtils.isEmpty(exchangeName)) {
            throw new IllegalArgumentException("exchange or routingKey must not be null");
        }
        this.declareExchangeAndQueue(exchangeName, exchangeType, routingKey, queueNames);
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, data);
        } catch (AmqpException e) {
            logger.error("RabbitMQ send exception" + e.getMessage(), e);
            throw e;
        }
    }

    private void declareExchangeAndQueue(String exchangeName, ExchangeType ExchangeType, String routingKey,
                                         String... queueNames) {
        if (queueNames != null && queueNames.length > 0) {
            for (String queueName : queueNames) {
                if (!declaredExchangeAndQueues.contains(exchangeName + "|" + queueName)) {
                    Queue queue = new Queue(queueName);
                    queue.setAdminsThatShouldDeclare(rabbitAdmin);
                    rabbitAdmin.declareQueue(queue);

                    switch (ExchangeType) {
                        case TOPIC:
                            TopicExchange topicExchange = new TopicExchange(exchangeName);
                            rabbitAdmin.declareExchange(topicExchange);
                            rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(topicExchange).with(routingKey));
                            break;
                        case DIRECT:
                            DirectExchange directExchange = new DirectExchange(exchangeName);
                            rabbitAdmin.declareExchange(directExchange);
                            rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(directExchange).with(routingKey));
                            break;
                        case FANOUT:
                            FanoutExchange fanoutExchange = new FanoutExchange(exchangeName);
                            rabbitAdmin.declareExchange(fanoutExchange);
                            rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(fanoutExchange));
                            break;
                        default:
                            FanoutExchange exchange = new FanoutExchange(exchangeName);
                            rabbitAdmin.declareExchange(exchange);
                            rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange));
                            break;
                    }
                    declaredExchangeAndQueues.add(exchangeName + "|" + queueName);
                }
            }
        }
    }

    public enum ExchangeType {
        /**
         * topic
         */
        TOPIC,
        /**
         * direct
         */
        DIRECT,
        /**
         * fanout
         */
        FANOUT
    }

    /**
     * 主动接收消息
     *
     * @param listener    消费者
     * @param threadCount 所需要开启的消费者线程数，默认1
     */
    public void receive(final RabbitMessageListener listener, int threadCount) {
        ExecutorService es;
        if (threadCount <= 0) {
            es = Executors.newSingleThreadExecutor();
        } else {
            es = Executors.newFixedThreadPool(threadCount);
        }
        for (int i = 0; i < threadCount; i++) {
            es.execute(() -> {
                while (true) {
                    Object o = rabbitTemplate.receiveAndConvert(listener.getTargetQueueName());
                    if (o != null) {
                        listener.handleMessage(o);
                        continue;
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(DEFAULT_SLEEP_MILLIS);
                    } catch (InterruptedException e) {
                        logger.error("Sleep in receive method interrupted.");
                    }
                }
            });
        }
    }

    /**
     * 注册listener
     *
     * @param listener 要注册的消费者
     */
    @Override
    public void listen(final RabbitMessageListener listener) {
        int concurrent = 1;
        listen(listener, concurrent);
    }

    @Override
    public void listen(final RabbitMessageListener listener, Integer concurrentConsumers) {
        String targetQueue = listener.getTargetQueueName();
        this.ensureQueueDeclared(targetQueue);
        // 注册监听接口
        MessageListenerAdapter adapter = new MessageListenerAdapter(new Object() {
            @SuppressWarnings("unused")
            public void handleMessage(Object message) {
                try {
                    listener.handleMessage(message);
                } catch (Exception e) {
                    logger.error("MQ listener handle method exception " + e.getMessage(), e);
                }
            }
        });

        if (messageConverter != null) {
            adapter.setMessageConverter(messageConverter);
        }

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(rabbitMQConnectionFactory);
        container.setMessageListener(adapter);
        container.setQueueNames(listener.getTargetQueueName());
        if (concurrentConsumers != null && concurrentConsumers.intValue() > 1) {
            container.setConcurrentConsumers(concurrentConsumers);
        }
        switch (listener.getAcknowledgeMode()) {
            case RabbitMessageListener.ACK_MODE_AUTO: {
                container.setAcknowledgeMode(AcknowledgeMode.AUTO);
                break;
            }
            case RabbitMessageListener.ACK_MODE_MANUAL: {
                container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
                break;
            }
            case RabbitMessageListener.ACK_MODE_NONE: {
                container.setAcknowledgeMode(AcknowledgeMode.NONE);
                break;
            }
            default: {
                container.setAcknowledgeMode(AcknowledgeMode.AUTO);
                break;
            }
        }

        container.start();
    }

    /**
     * 声明exchange与queue
     *
     * @param queueName
     */
    private void ensureQueueDeclared(String queueName) {
        if (!declaredQueues.contains(queueName)) {
            Queue queue = new Queue(queueName);
            queue.setAdminsThatShouldDeclare(rabbitAdmin);
            rabbitAdmin.declareQueue(queue);

            declaredQueues.add(queueName);
        }
    }
}