package org.rainbow.rabbitmq.producer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.rainbow.rabbitmq.entity.BrokerMessageLog;
import org.rainbow.rabbitmq.entity.Order;
import org.rainbow.rabbitmq.producer.constant.Constants;
import org.rainbow.rabbitmq.producer.mapper.BrokerMessageLogMapper;
import org.rainbow.rabbitmq.producer.mapper.OrderMapper;
import org.rainbow.rabbitmq.producer.sender.RabbitOrderSender;
import org.rainbow.rabbitmq.producer.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author K
 * @date 2021/1/23  10:55
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private BrokerMessageLogMapper brokerMessageLogMapper;

    @Autowired
    private RabbitOrderSender rabbitOrderSender;

    /**
     * 创建订单
     *
     * @param order 订单
     * @throws Exception 异常
     */
    @Override
    public void createOrder(Order order) throws Exception {
        // 使用当前时间作为订单时间
        Date orderTime = new Date();
        // 插入业务数据
        orderMapper.insert(order);
        // 插入消息记录表数据
        BrokerMessageLog brokerMessageLog = new BrokerMessageLog();
        // 消息唯一ID
        brokerMessageLog.setMessageId(order.getMessageId());
        // 保存消息整体 转为JSON 格式存储入库
        brokerMessageLog.setMessage(JSONUtil.toJsonStr(order));
        // 设置消息状态为0 表示发送中
        brokerMessageLog.setStatus("0");
        // 设置消息未确认超时时间窗口为 一分钟
        brokerMessageLog.setNextRetry(DateUtil.offsetMinute(orderTime, Constants.ORDER_TIMEOUT));
        brokerMessageLog.setCreateTime(new Date());
        brokerMessageLog.setUpdateTime(new Date());
        brokerMessageLogMapper.insert(brokerMessageLog);
        // 发送消息
        rabbitOrderSender.sendOrder(order);
    }
}
