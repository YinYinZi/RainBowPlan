package org.rainbow.rabbitmq.producer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.rainbow.rabbitmq.entity.Order;
import org.springframework.stereotype.Repository;

/**
 * @author K
 * @date 2021/1/23  9:43
 */
@Repository
public interface OrderMapper extends BaseMapper<Order> {


}
