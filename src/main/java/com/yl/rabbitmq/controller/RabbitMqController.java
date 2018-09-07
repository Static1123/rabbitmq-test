package com.yl.rabbitmq.controller;

import com.yl.rabbitmq.component.RabbitMQComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author admin
 * @date 2018/9/7 11:11
 */
@RestController
@RequestMapping("/mq")
public class RabbitMqController {
    @Autowired
    private RabbitMQComponent rabbitMQComponent;

    @RequestMapping("/directSend")
    public boolean directSend(@RequestParam("msg") String msg) {
        rabbitMQComponent.directSend("rabbitmq-test", msg);
        return true;
    }

    @RequestMapping("/exchangeSend")
    public boolean exchangeSend(@RequestParam("msg") String msg) {
        rabbitMQComponent.exchangeSend("q.exchange", msg, "exchange1");
        return true;
    }
}