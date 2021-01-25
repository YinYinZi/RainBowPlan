package org.rainbow.core.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * 树结构实体
 *
 * @author K
 * @date 2021/1/25  11:35
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class TreeEntity<E, T extends Serializable> extends Entity<T> {

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @NotEmpty(message = "名称不能为空")
    @Length(max = 255, message = "名称长度不能超过255")
    @TableField(value = "label", condition = LIKE)
    protected String label;

    /**
     * 父ID
     */
    @ApiModelProperty(value = "父ID")
    @TableField(value = "parent_id")
    protected T parentId;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序号")
    @TableField(value = "sort_value")
    protected Integer sortValue;

    /**
     * 子节点
     */
    @ApiModelProperty(value = "子节点", hidden = true)
    @TableField(exist = false)
    protected List<E> children;

    /**
     * 初始化子类
     */
    public void initChildren() {
        if (getChildren() == null) {
            this.setChildren(new ArrayList<>());
        }
    }
}
