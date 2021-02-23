package org.rainbow.notice.properties;

import lombok.Data;

/**
 * 邮箱配置
 *
 * @author K
 * @date 2021/2/22  18:22
 */
@Data
public class MailProperties {

    /**
     * 发送人
     */
    private String from;

    /**
     * 接收人 多个以逗号隔开
     */
    private String[] to;

    /**
     * 抄送人 多个以逗号隔开
     */
    private String[] cc;
}
