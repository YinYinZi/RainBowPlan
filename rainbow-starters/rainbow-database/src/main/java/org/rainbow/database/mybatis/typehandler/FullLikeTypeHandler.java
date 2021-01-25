package org.rainbow.database.mybatis.typehandler;

import org.apache.ibatis.type.Alias;

/**
 * 模糊查询处理器
 *
 * @author K
 * @date 2021/1/25  17:27
 */
@Alias("fullLike")
public class FullLikeTypeHandler extends BaseLikeTypeHandler {

    public FullLikeTypeHandler() {
        super(true, true);
    }
}
