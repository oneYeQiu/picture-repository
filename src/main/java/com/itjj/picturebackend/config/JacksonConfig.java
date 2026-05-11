package com.itjj.picturebackend.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * SpringMVC Json 配置
 * 全局配置：解决 Long 类型返回前端精度丢失问题
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置 Jackson2ObjectMapperBuilderCustomizer
     * 解决 Long 类型返回前端精度丢失问题
     *
     * @return {@link Jackson2ObjectMapperBuilderCustomizer}
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // 把 Long 类型全部转为 String
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            // 把 long 基本类型也转为 String（防止漏掉）
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
        };
    }
}