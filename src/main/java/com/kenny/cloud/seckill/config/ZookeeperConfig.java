package com.kenny.cloud.seckill.config;

import com.kenny.cloud.seckill.zk.watcher.ZookeeperWatcher;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @ClassName ZookeeperConfig
 * @Description:
 * @Author Kenny
 * @Date 2020/3/24
 **/
@Configuration
public class ZookeeperConfig {

    @Value("${zookeeper.server}")
    private String server;

    @Value("${zookeeper.sessionTimeoutMs}")
    private int sessionTimeout;

    @Value("${zookeeper.connectionTimeoutMs}")
    private int connectionTimeout;


    @Bean
    public ZooKeeper initZookeeper() throws IOException {
        return new ZooKeeper(server, 600000, new ZookeeperWatcher());
    }


    @Bean
    public ZkClient zkClient() {
        return new ZkClient(server, sessionTimeout, connectionTimeout);
    }

}
