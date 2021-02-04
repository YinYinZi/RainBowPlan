package org.rainbow.database.injector.method;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;

import java.util.function.Predicate;

/**
 * @author K
 * @date 2021/2/4  10:32
 */
public class UpdateAllById extends AlwaysUpdateSomeColumnById {

    public UpdateAllById(Predicate<TableFieldInfo> predicate) {
        super(predicate);
    }

    public UpdateAllById() {}

    @Override
    public String getMethod(SqlMethod sqlMethod) {
        return "UpdateAllById";
    }
}
