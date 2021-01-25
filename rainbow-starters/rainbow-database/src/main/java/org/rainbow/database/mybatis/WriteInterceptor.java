package org.rainbow.database.mybatis;

import cn.hutool.aop.ProxyUtil;
import cn.hutool.aop.aspects.TimeIntervalAspect;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.rainbow.core.exception.BizException;
import org.rainbow.core.utils.BizAssert;

import java.sql.Connection;
import java.util.Properties;

/**
 * 演示环境写权限控制 拦截器
 *
 * @author K
 * @date 2021/1/25  17:36
 */
@Slf4j
@NoArgsConstructor
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class WriteInterceptor extends AbstractSqlParserHandler implements Interceptor {


    @Override
    @SneakyThrows
    public Object intercept(Invocation invocation) {
        if (!SpringUtil.getApplicationContext().getEnvironment().getProperty("rainbow.database.isNotWrite", Boolean.class, false)) {
            return invocation.proceed();
        }

        StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        sqlParser(metaObject);
        MappedStatement statement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        // 读操作放行 修改和删除操作不允许
        if (!SqlCommandType.SELECT.equals(statement.getSqlCommandType())) {
            BizAssert.fail("测试环境不允许修改和删除");
        }
        return invocation.proceed();
    }

    /**
     * 生成拦截对象的代理
     *
     * @param target 目标对象
     * @return 代理对象
     */
    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    /**
     * mybatis配置的属性
     *
     * @param properties mybatis配置的属性
     */
    @Override
    public void setProperties(Properties properties) {

    }
}
