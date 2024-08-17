package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//使用lombok依赖实现构造函数等  可以在target中查看编译结果

@Data // get Set toSpring等自动填充好
@NoArgsConstructor // 无参数构造函数自动填充
@AllArgsConstructor // 有参自动填充
// pojo用来将sql表翻译成class
public class User {
    // mybatis-plus中注解 实现自增
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String photo;
    private Integer rating;
}
