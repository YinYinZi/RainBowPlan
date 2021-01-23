package org.rainbow.rabbitmq.producer.controller;

import cn.hutool.core.util.IdUtil;
import org.rainbow.rabbitmq.entity.Order;
import org.rainbow.rabbitmq.producer.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author K
 * @date 2021/1/23  16:31
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @GetMapping("/create")
    public String create() throws Exception {
        Order order = new Order();
        order.setId(2018092102);
        order.setName("测试订单1");
        order.setMessageId(System.currentTimeMillis() + "$" + IdUtil.randomUUID());
        orderService.createOrder(order);
        return "创建成功";
    }
}
