package com.kob.botrunningsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    // @Component 中使用service要加入Bean注解

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
