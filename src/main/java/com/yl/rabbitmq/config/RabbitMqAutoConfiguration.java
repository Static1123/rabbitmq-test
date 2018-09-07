package com.yl.rabbitmq.config;

import com.yl.rabbitmq.service.RabbitMqService;
import com.yl.rabbitmq.service.RabbitMessageListener;
import com.yl.rabbitmq.service.impl.SimpleRabbitServiceImpl;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author admin
 * @date 2018/9/7 8:35
 */
@Configuration
public class RabbitMqAutoConfiguration implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;

    @Value("${rabbitmq.autoListen:true}")
    private boolean autoListen;

    @Value("${spring.rabbitmq.host:127.0.0.1}")
    private String host;

    @Value("${spring.rabbitmq.port:5672}")
    private int port;

    @Value("${spring.rabbitmq.username:guest}")
    private String userName;

    @Value("${spring.rabbitmq.password:guest}")
    private String password;

    @Value("${rabbitmq.metrics.report.interval:30}")
    private int reportInterval;

    @Value("${rabbitmq.serialize.type:1}")
    private int serializationType;

    @Value("${rabbitmq.addresses:}")
    private String addresses;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String vhost;

    @Value("${rabbitmq.maxChannels:25}")
    private int maxChannels;

    @Value("${rabbitmq.connectionTimeout:60000}")
    private int connectionTimeout;

    @Value("${rabbitmq.requestedHeartbeat:60}")
    private int requestedHeartbeat;

    @Value("${rabbitmq.automaticRecoveryEnabled:false}")
    private boolean automaticRecoveryEnabled;

    @Value("${rabbitmq.topologyRecoveryEnabled:true}")
    private boolean topologyRecoveryEnabled;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public RetryPolicy retryPolicy() {
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        return simpleRetryPolicy;
    }

    @Bean
    public RetryTemplate retryTemplate(RetryPolicy retryPolicy) {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory cf = new CachingConnectionFactory();
        cf.setHost(host);
        cf.setPassword(password);
        cf.setUsername(userName);
        cf.setPort(port);
        cf.setVirtualHost(vhost);
        cf.setAddresses(addresses);
        cf.setChannelCacheSize(maxChannels);
        cf.getRabbitConnectionFactory().setTopologyRecoveryEnabled(topologyRecoveryEnabled);
        cf.getRabbitConnectionFactory().setAutomaticRecoveryEnabled(automaticRecoveryEnabled);
        cf.setConnectionTimeout(connectionTimeout);
        cf.setRequestedHeartBeat(requestedHeartbeat);
        return cf;
    }

    @Bean
    @Autowired
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, RetryTemplate retryTemplate) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cf);
        rabbitTemplate.setRetryTemplate(retryTemplate);
        return rabbitTemplate;
    }

    @Bean
    @Autowired
    public RabbitAdmin rabbitAdmin(ConnectionFactory cf, RetryTemplate retryTemplate) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(cf);
        rabbitAdmin.setRetryTemplate(retryTemplate);
        return rabbitAdmin;
    }

    @Bean
    @Autowired
    public RabbitMqService rabbitMQService(ConnectionFactory rabbitConnectionFactory,
                                           RabbitTemplate rabbitTemplate,
                                           RabbitAdmin admin) {
        if (serializationType == 1) {
            return new SimpleRabbitServiceImpl(rabbitConnectionFactory, rabbitTemplate, admin);
        } else if (serializationType == 2) {
            return new SimpleRabbitServiceImpl(rabbitConnectionFactory, rabbitTemplate, admin, new SimpleMessageConverter());
        } else {
            return new SimpleRabbitServiceImpl(rabbitConnectionFactory, rabbitTemplate, admin);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (autoListen) {
            Collection<RabbitMessageListener> rabbitListeners = applicationContext.getBeansOfType(RabbitMessageListener.class).values();
            RabbitMqService rabbitMQService = applicationContext.getBean(RabbitMqService.class);
            Iterator<RabbitMessageListener> it = rabbitListeners.iterator();
            while (it.hasNext()) {
                rabbitMQService.listen(it.next());
            }
        }
    }
}