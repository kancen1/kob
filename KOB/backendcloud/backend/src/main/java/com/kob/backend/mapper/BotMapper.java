package com.kob.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kob.backend.pojo.Bot;
import org.apache.ibatis.annotations.Mapper;

// mybatis-plus依赖可以帮我们实现很多sql语句 使用需要继承
@Mapper
public interface BotMapper extends BaseMapper<Bot> {

}
