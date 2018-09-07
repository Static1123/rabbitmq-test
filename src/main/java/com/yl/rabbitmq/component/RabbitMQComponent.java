package com.yl.rabbitmq.component;

import com.yl.rabbitmq.entity.RabbitMqTarget;
import com.yl.rabbitmq.service.RabbitMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @date 2018/9/5 16:42
 */
@Component("rabbitMqComponent")
@Lazy
@Slf4j
public class RabbitMQComponent {
    @Autowired
    private RabbitMqService rabbitMQService;

    public boolean directSend(String queue, Object msg) {
        RabbitMqTarget rabbitMQMessageTarget = RabbitMqTarget.createDirectTarget(queue);
        try {
            rabbitMQService.send(rabbitMQMessageTarget, msg);
            return true;
        } catch (Exception ex) {
            log.error("RabbitMQComponent:directSend,error=[{}]", ex.getMessage());
            return false;
        }
    }

    public boolean exchangeSend(String exchangeName, Object msg, String... queueNames) {
        RabbitMqTarget rabbitMQMessageTarget = RabbitMqTarget.createFanoutTarget(exchangeName, queueNames);
        try {
            rabbitMQService.send(rabbitMQMessageTarget, msg);
            return true;
        } catch (Exception ex) {
            log.error("RabbitMQComponent:exchangeSend,error=[{}]", ex.getMessage());
            return false;
        }
    }

    public boolean topicSend(String exchangeName, String routeKey, Object msg, String... queueNames) {
        RabbitMqTarget rabbitMQMessageTarget = RabbitMqTarget.createTopicTarget(exchangeName, routeKey, queueNames);
        try {
            rabbitMQService.send(rabbitMQMessageTarget, msg);
            return true;
        } catch (Exception ex) {
            log.error("RabbitMQComponent:topicSend,error=[{}]", ex.getMessage());
            return false;
        }
    }
}