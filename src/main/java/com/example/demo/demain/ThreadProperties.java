package com.example.demo.demain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wangyongqiang
 * 使用@ConfigurationProperties注解指定我们扫描的配置文件中的前缀，同时要使用这个注解需要引入一个对应pom依赖
 */
@Component
@ConfigurationProperties(prefix = "ownconfig.thread")
@Data
public class ThreadProperties {
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;
    private Integer keepAliveSeconds;
    private String  threadNamePrefix;
}