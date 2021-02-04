package org.rainbow.database.datasource;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.rainbow.core.base.entity.Entity;
import org.rainbow.core.base.entity.SuperEntity;
import org.rainbow.core.context.BaseContextHandler;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * MyBatis Plus 元数据处理类
 * 用于自动注入 id, createTime, createUser, updateTime, updateUser 等字段
 *
 * @author K
 * @date 2021/1/27  16:57
 */
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    private final static String ID_TYPE = "java.lang.String";

    private long workerId;
    private long dataCenterId;

    public MyMetaObjectHandler(long workerId, long dataCenterId) {
        super();
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 注意：不支持 复合主键 自动注入！！
     * <p>
     * 所有的继承了Entity、SuperEntity的实体，在insert时，
     * id： id为空时， 通过IdGenerate生成唯一ID， 不为空则使用传递进来的id
     * createUser, updateUser: 自动赋予 当前线程上的登录人id
     * createTime, updateTime: 自动赋予 服务器的当前时间
     * <p>
     * 未继承任何父类的实体，且主键标注了 @TableId(value = "xxx", type = IdType.INPUT) 自动注入 主键
     * 主键的字段名称任意
     *
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        boolean flag = true;
        // 设置创建时间和创建人
        if (metaObject.getOriginalObject() instanceof SuperEntity) {
            SuperEntity entity = (SuperEntity) metaObject.getOriginalObject();
            Object oldId = entity.getId();
            if (Objects.nonNull(oldId)) {
                flag = false;
            }

            if (Objects.isNull(entity.getCreateTime())) {
                this.setFieldValByName(Entity.CREATE_TIME, LocalDateTime.now(), metaObject);
            }

            if (Objects.isNull(entity.getCreateUser()) || entity.getCreateUser().equals(0)) {
                Object userIdVal = ID_TYPE.equals(metaObject.getGetterType(SuperEntity.CREATE_USER).getName()) ?
                        String.valueOf(BaseContextHandler.getUserId()) : BaseContextHandler.getUserId();
                this.setFieldValByName(Entity.CREATE_USER, userIdVal, metaObject);
            }
        }

        // 修改人 修改时间
        if (metaObject.getOriginalObject() instanceof Entity) {
            Entity entity = (Entity) metaObject.getOriginalObject();
            update(metaObject, entity);
        }

        // 若 ID 中有值，就不设置
        if (!flag) {
            return;
        }
        Snowflake snowflake = IdUtil.createSnowflake(workerId, dataCenterId);
        long id = snowflake.nextId();
        if (metaObject.hasGetter(SuperEntity.FIELD_ID)) {
            Object idVal = ID_TYPE.equals(metaObject.getGetterType(SuperEntity.FIELD_ID).getName()) ? String.valueOf(id) : id;
            this.setFieldValByName(SuperEntity.FIELD_ID, idVal, metaObject);
            return;
        }

        // 实体没有继承Entity和SuperEntity
        TableInfo tableInfo = metaObject.hasSetter(Constants.MP_OPTLOCK_ET_ORIGINAL) ?
                TableInfoHelper.getTableInfo(metaObject.getValue(Constants.MP_OPTLOCK_ET_ORIGINAL).getClass())
                : TableInfoHelper.getTableInfo(metaObject.getOriginalObject().getClass());
        if (Objects.isNull(tableInfo)) {
            return;
        }
        // 主键类型
        Class<?> keyType = tableInfo.getKeyType();
        if (Objects.isNull(keyType)) {
            return;
        }

        // id 字段名
        String keyProperty = tableInfo.getKeyProperty();

        // 反射得到主键的值
        Field idField = ReflectUtil.getField(metaObject.getOriginalObject().getClass(), keyProperty);
        Object fieldValue = ReflectUtil.getFieldValue(metaObject.getOriginalObject(), idField);
        if (Objects.nonNull(fieldValue)) {
            return;
        }
        Object idVal = keyType.getName().equalsIgnoreCase(ID_TYPE) ? String.valueOf(id) : id;
        this.setFieldValByName(keyProperty, idVal, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("start update file ...");
        if (metaObject.getOriginalObject() instanceof Entity) {
            Entity entity = (Entity) metaObject.getOriginalObject();
            update(metaObject, entity);
        } else {
            Object et = metaObject.getValue(Constants.ENTITY);
            if (et instanceof Entity) {
                Entity entity = (Entity) et;
                update(metaObject, entity, Constants.ENTITY + ".");
            }
        }
    }

    private void update(MetaObject metaObject, Entity entity, String et) {
        if (Objects.isNull(entity.getUpdateUser()) || entity.getUpdateUser().equals(0)) {
            Object userIdVal = ID_TYPE.equals(metaObject.getGetterType(et + Entity.UPDATE_USER).getName()) ? String.valueOf(BaseContextHandler.getUserId()) : BaseContextHandler.getUserId();
            this.setFieldValByName(Entity.UPDATE_USER, BaseContextHandler.getUserId(), metaObject);
        }
        if (Objects.isNull(entity.getUpdateTime())) {
            this.setFieldValByName(Entity.UPDATE_TIME, LocalDateTime.now(), metaObject);
        }
    }

    private void update(MetaObject metaObject, Entity entity) {
        update(metaObject, entity, "");
    }
}
