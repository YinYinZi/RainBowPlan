package org.rainbow.rabbitmq.producer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.rainbow.rabbitmq.entity.Order;

/**
 * @author K
 * @date 2021/1/23  10:53
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     *
     * @param order 订单
     * @throws Exception 异常
     */
    void createOrder(Order order) throws Exception;
}
