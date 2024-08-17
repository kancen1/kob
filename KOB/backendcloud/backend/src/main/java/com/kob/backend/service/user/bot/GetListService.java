package com.kob.backend.service.user.bot;

import com.kob.backend.pojo.Bot;

import java.util.List;

public interface GetListService {
    // userId存放在token中所以并不需要传递参数
    List<Bot> getList();
}
