package org.rainbow.database.mybatis.typehandler;

import org.apache.ibatis.type.Alias;

/**
 * 左模糊处理器
 *
 * @author K
 * @date 2021/1/25  17:30
 */
@Alias("leftLike")
public class LeftLikeTypeHandler extends BaseLikeTypeHandler {

    public LeftLikeTypeHandler() {
        super(true, false);
    }
}
