package com.kenny.cloud.seckill.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ZookeeperProperties
 * @Description:
 * @Author Kenny
 * @Date 2020/3/24
 **/
@ConfigurationProperties(prefix = "zookeeper")
@Configuration
@Data
public class ZookeeperProperties {

    private String server;

    private String namespace;
    private boolean enabled;

    private String  digest;

    private int sessionTimeoutMs;

    private int connectionTimeoutMs;
    private int maxRetries;
    private int baseSleepTimeMs;


}
