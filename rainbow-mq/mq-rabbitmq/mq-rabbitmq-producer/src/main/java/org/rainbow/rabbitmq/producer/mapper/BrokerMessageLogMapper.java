package org.rainbow.rabbitmq.producer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.rainbow.rabbitmq.entity.BrokerMessageLog;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author K
 * @date 2021/1/23  10:48
 */
@Repository
public interface BrokerMessageLogMapper extends BaseMapper<BrokerMessageLog> {

    /**
     * 查询消息状态为0(发送中) 且已经超时的消息集合
     */
    List<BrokerMessageLog> query4StatusAndTimeoutMessage();

    /**
     * 重新发送统计count发送次数 +1
     */
    void update4ReSend(@Param("messageId")String messageId,
                       @Param("updateTime") Date updateTime);

    /**
     * 更新最终消息发送结果 成功 or 失败
     */
    void changeBrokerMessageLogStatus(@Param("messageId")String messageId,
                                      @Param("status")String status,
                                      @Param("updateTime")Date updateTime);


}
