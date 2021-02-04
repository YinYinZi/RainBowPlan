package org.rainbow.database.injector;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import org.rainbow.database.injector.method.UpdateAllById;

import java.util.List;

/**
 * @author K
 * @date 2021/2/4  10:36
 */
public class Injector extends DefaultSqlInjector {

    public Injector() {}

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new InsertBatchSomeColumn((i) -> i.getFieldFill() != FieldFill.UPDATE));
        methodList.add(new UpdateAllById((field) -> !ArrayUtil.containsAny(new String[]{"create_time", "created_by"}, field.getColumn())));
        return methodList;
    }
}
