package com.yl.rabbitmq.listener;

import com.yl.rabbitmq.service.RabbitMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;

/**
 * @param <K>
 * @author admin
 */
public abstract class AbstractRabbitMessageListener<K> implements RabbitMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRabbitMessageListener.class);

    private String targetQueueName;

    private int acknowledgeMode;

    private boolean skipRequestTraceMQLog;

    public AbstractRabbitMessageListener(String queueName) {
        this(queueName, ACK_MODE_AUTO);
    }

    public AbstractRabbitMessageListener(String queueName, int ackMode) {
        this.targetQueueName = queueName;
        this.acknowledgeMode = ackMode;
    }

    @Override
    public int getAcknowledgeMode() {
        return acknowledgeMode;
    }

    public void setAcknowledgeMode(int acknowledgeMode) {
        this.acknowledgeMode = acknowledgeMode;
    }


    @Override
    public String getTargetQueueName() {
        return targetQueueName;
    }

    public void setTargetQueueName(String queueName) {
        this.targetQueueName = queueName;
    }


    public boolean isSkipRequestTraceMQLog() {
        return skipRequestTraceMQLog;
    }

    public void setSkipRequestTraceMQLog(boolean skipRequestTraceMQLog) {
        this.skipRequestTraceMQLog = skipRequestTraceMQLog;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleMessage(Object object) {
        doHandleMessage((K) object);
    }

    private Class getActualType() {
        return (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * 实际消息处理逻辑
     *
     * @param object
     */
    public abstract void doHandleMessage(K object);
}