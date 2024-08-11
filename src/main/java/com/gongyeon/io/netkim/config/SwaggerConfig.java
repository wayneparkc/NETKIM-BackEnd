package com.gongyeon.io.netkim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("NETKIM Gongyeon.IO REST API 명세서")
                        .description("비인기 장르, 중소형 기획사의 홍보 파트너, 공연이요의 API 명세서")
                        .version("v1.0.0")
                        .license(new License().name("Notion").url("https://github.com/gongyeon/netkim.gongyeon")));
    }
}
