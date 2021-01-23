package org.rainbow.rabbitmq.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 订单实体
 *
 * @author K
 * @date 2021/1/21  15:17
 */
@Data
@Accessors(chain = true)
@TableName(value = "order_")
public class Order implements Serializable {

    private Integer id;

    private String name;

    private String messageId;
}
