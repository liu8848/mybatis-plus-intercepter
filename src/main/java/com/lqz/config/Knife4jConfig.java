package com.lqz.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(
                        new Info()
                                .title("mybatis-plus 自定义sql注入器")
                                .description("mybatis-plus 自定义sql注入器")
                                .contact(new Contact().name("lqz").email("sudalu929@outlook.com"))
                                .summary("mybatis-plus 自定义sql注入器")
                                .version("v1.0")
                );
    }

}
