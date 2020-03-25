package com.kenny.cloud.seckill.zk.watcher;

import com.kenny.cloud.seckill.constants.ProductRedisConstants;
import com.kenny.cloud.seckill.controller.OrderController;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName ZookeeperWatcher
 * @Description:
 * @Author Kenny
 * @Date 2020/3/24
 **/
@Component
@Slf4j
public class ZookeeperWatcher implements Watcher, ApplicationContextAware {

    @Autowired
    private ZooKeeper zooKeeper;

    private static ApplicationContext applicationContext;
    private static CountDownLatch countDownLatch = new CountDownLatch(1);


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        countDownLatch.countDown();
        this.applicationContext = applicationContext;
    }


    @Override
    public void process(WatchedEvent watchedEvent) {
        log.info("=====================zookeeper连接成功========================= ");
        if (watchedEvent.getType() == Event.EventType.None && watchedEvent.getPath() == null) {
            if (zooKeeper == null) {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                zooKeeper = (ZooKeeper) applicationContext.getBean(ZooKeeper.class);
                log.info("=====================zookeeper创建成功========================= ");

            }

            //下面是业务代码 创建zk的商品售完标记根节点
            try {
                if (zooKeeper.exists(ProductRedisConstants.PRODUCT_STOCK_JVM_PREFIX, false) == null) {
                    zooKeeper.create(ProductRedisConstants.PRODUCT_STOCK_JVM_PREFIX, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
            String path = watchedEvent.getPath();
            try {
                String slodOutFlag = new String(zooKeeper.getData(path, true, new Stat()));
                log.info("zookeeper  节点数据变动, path = {}， data = {}", path, slodOutFlag);
                if ("false".equals(slodOutFlag)) {
                    String productId = path.substring(path.lastIndexOf("/") + 1, path.length());
                    OrderController.stockMap.remove(Long.parseLong(productId));
                }

            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
