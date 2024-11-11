package com.lqz.injector;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//将上方的方法托管到spring管理
@Configuration
public class MybatisPlusConfig {

    @Bean
    public CustomSqlInjector customSqlInjector() {
        return new CustomSqlInjector();
    }

}