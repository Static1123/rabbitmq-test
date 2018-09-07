package com.yl.rabbitmq.controller;

import com.yl.rabbitmq.service.RabbitMqService;
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
    private RabbitMqService rabbitMqService;

    @RequestMapping("/directSend")
    public boolean directSend(@RequestParam("msg") String msg) {
        rabbitMqService.send("rabbitmq-test", msg);
        return true;
    }
}