package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data // get Set toSpring等自动填充好
@NoArgsConstructor // 无参数构造函数自动填充
@AllArgsConstructor // 有参自动填充
public class Bot {
//    自增
    @TableId(type = IdType.AUTO)
    private Integer id;
//    QueryWrapper 要使用原本名字 user_id
    private Integer userId;
    private String title;
    private String description;
    private String content;
//    日期在java中使用Date类型  日期格式定义使用@JsonFormat 并修改时区
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createtime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date modifytime;
}
