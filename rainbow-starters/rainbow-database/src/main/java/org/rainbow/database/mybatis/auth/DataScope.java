package org.rainbow.database.mybatis.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;

/**
 * 数据权限查询参数
 *
 * @author K
 * @date 2021/1/25  18:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataScope extends HashMap {

    /**
     * 限制范围的字段名称 (除个人外)
     */
    private String scopeName = "org_id";
    /**
     * 限制范围为个人的字段名称
     */
    private String selfScopeName = "create_user";
    /**
     * 当前用户id
     */
    private Long userId;
    /**
     * 数据范围
     */
    private List<Long> orgIds;
}
