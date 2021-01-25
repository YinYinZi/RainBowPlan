package org.rainbow.database.mybatis.typehandler;

import org.apache.ibatis.type.Alias;

/**
 * 右模糊查询
 *
 * @author K
 * @date 2021/1/25  17:31
 */
@Alias("rightLike")
public class RightLikeTypeHandler extends BaseLikeTypeHandler {

    public RightLikeTypeHandler() {
        super(false, true);
    }
}
