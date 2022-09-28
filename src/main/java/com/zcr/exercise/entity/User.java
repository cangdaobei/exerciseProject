package com.zcr.exercise.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("user")
public class User {
    @TableField("id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField("user_name")
    private String username;

    @TableField("age")
    private Integer age;
}