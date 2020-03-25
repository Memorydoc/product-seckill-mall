package com.kenny.cloud.seckill.controller;

import com.kenny.cloud.seckill.constants.ProductRedisConstants;
import com.kenny.cloud.seckill.domain.Product;
import com.kenny.cloud.seckill.mapper.ProductMapper;
import com.kenny.cloud.seckill.service.impl.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName OrderController
 * @Description:
 * @Author Kenny
 * @Date 2020/3/24
 **/

@RestController
@RequestMapping("order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 缓存商品是否已经被售完，
     */
    public static ConcurrentHashMap<Long, Boolean> stockMap = new ConcurrentHashMap();


    @Autowired
    private ZkClient zkClient;

    /**
     * 在Spring初始化就将订单数据加载到redis中
     */
    @PostConstruct
    private void initProduct() {

        List<Product> products = productMapper.selectByExample(new Example(Product.class));
        products.stream().forEach(product -> {
            redisTemplate.opsForValue().set(ProductRedisConstants.PRODUCT_STOCK_PREFIX + product.getId(), product.getStock() + "");

            /*
            初始化zk,设置监听
            String zkSoldOutPath = ProductRedisConstants.getProductZkKey(product.getId());
            try {
                if (zooKeeper.exists(zkSoldOutPath, true) == null) {
                    if (zooKeeper.exists(ProductRedisConstants.PRODUCT_STOCK_JVM_PREFIX, true) == null) {
                        zooKeeper.create(ProductRedisConstants.PRODUCT_STOCK_JVM_PREFIX
                                , "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }
                    zooKeeper.create(zkSoldOutPath
                            , "false".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    zooKeeper.exists(zkSoldOutPath, true);
                } else {
                    zooKeeper.exists(zkSoldOutPath, true);
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        });


    }

    /**
     * 普通业务代码秒杀，直接操作数据库， 使用Jemeter查看吞吐量
     *
     * @param productId
     * @return
     */
    @GetMapping("seckill/{productId}")
    public String seckill(@PathVariable("productId") Long productId) {
        return orderService.seckill(productId);
    }

    /**
     * 使用redis和jvm缓存优化后的秒杀接口
     *
     * @param productId
     * @return
     */
    @GetMapping("seckillByRedis/{productId}")
    public String seckillByRedis(@PathVariable("productId") Long productId) {
        if (stockMap.get(productId) != null && stockMap.get(productId) == true) {
            log.info("库存不足");
            return "库存不足";
        }

        // 先去redis中查询商品是否被抢空
        Long stock = redisTemplate.opsForValue().decrement(ProductRedisConstants.PRODUCT_STOCK_PREFIX + productId);
        if (stock < 0) {
            stockMap.put(productId, true);
            redisTemplate.opsForValue().increment(redisTemplate.opsForValue().get(ProductRedisConstants.PRODUCT_STOCK_PREFIX + productId));

            return "库存不足";
        }
        String seckill = null;

        try {
            seckill = orderService.seckill(productId);

        } catch (Exception ex) {
            if (stockMap.get(productId) != null) {
                stockMap.remove(productId);
            }

            redisTemplate.opsForValue().increment(redisTemplate.opsForValue().get(ProductRedisConstants.PRODUCT_STOCK_PREFIX + productId));
            ex.printStackTrace();
            return "抢购失败";
        }

        return seckill;
    }

    @Autowired
    private ZooKeeper zooKeeper;



    /**
     * 使用redis 和 jvm两层缓存
     */
    @GetMapping("seckillByRedisJvmCache/{productId}")
    public String seckillByRedisJvmCache(@PathVariable("productId") Long productId) {
        if (stockMap.get(productId) != null) {
            log.info("库存不足");
            return "库存不足";
        }

        // 先去redis中查询商品是否被抢空
        Long stock = redisTemplate.opsForValue().decrement(ProductRedisConstants.PRODUCT_STOCK_PREFIX + productId);
        if (stock < 0) {
            stockMap.put(productId, false);
            redisTemplate.opsForValue().increment(redisTemplate.opsForValue().get(ProductRedisConstants.PRODUCT_STOCK_PREFIX + productId));
            return "库存不足";
        }
        String seckill = null;

        try {
            seckill = orderService.seckill(productId);

        } catch (Exception ex) {
            if (stockMap.get(productId) != null) {
                stockMap.remove(productId);
            }
            redisTemplate.opsForValue().increment(redisTemplate.opsForValue().get(ProductRedisConstants.PRODUCT_STOCK_PREFIX + productId));
            ex.printStackTrace();
            return "抢购失败";
        }
        log.info("抢购成功");
        return seckill;
    }


    /**
     * 使用zookeeper 解决上面jvm 的缓存在分布式环境下的缺陷问题
     */
    @GetMapping("seckillByRedisCheckzk/{productId}")
    public String seckillByRedisCheckzk(@PathVariable("productId") Long productId) {
        String zkSoldOutPath = null;
        if (stockMap.get(productId) != null) {
            return "库存不足";
        }
        // 先去redis中查询商品是否被抢空
        Long stock = redisTemplate.opsForValue().decrement(ProductRedisConstants.PRODUCT_STOCK_PREFIX + productId);
        if (stock < 0) {
            stockMap.put(productId, true);
            log.info("设置jvm内存变量");
            redisTemplate.opsForValue().increment(redisTemplate.opsForValue().get(ProductRedisConstants.PRODUCT_STOCK_PREFIX + productId));
            try {
                zkSoldOutPath = ProductRedisConstants.getProductZkKey(productId);
                if (zooKeeper.exists(zkSoldOutPath, true) == null) {
                    log.info("创建soldOutZkNode");
                    zooKeeper.create(zkSoldOutPath, "true".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);//如果不存在 则创建
                }
                zooKeeper.exists(zkSoldOutPath, true);// 设置监听

            } catch (Exception e) {
                e.printStackTrace();
                return "下单失败";
            }

            return "库存不足";
        }
        String seckill = null;

        try {
            seckill = orderService.seckill(productId);

        } catch (Exception ex) {
            log.error(ex.toString());

            if (stockMap.get(productId) != null) {
                stockMap.remove(productId);
            }
            try { // 秒杀失败，则通知zk 更新各jvm中的缓存信息
                zooKeeper.setData(zkSoldOutPath, "false".getBytes(), -1);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            redisTemplate.opsForValue().increment(redisTemplate.opsForValue().get(ProductRedisConstants.PRODUCT_STOCK_PREFIX + productId));
            ex.printStackTrace();
            return "抢购失败";
        }
        log.info("抢购成功");
        return seckill;
    }


}
