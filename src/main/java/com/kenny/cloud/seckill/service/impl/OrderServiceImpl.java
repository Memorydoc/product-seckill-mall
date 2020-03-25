package com.kenny.cloud.seckill.service.impl;

import com.kenny.cloud.seckill.domain.Order;
import com.kenny.cloud.seckill.domain.Product;
import com.kenny.cloud.seckill.mapper.OrderMapper;
import com.kenny.cloud.seckill.mapper.ProductMapper;
import com.kenny.cloud.seckill.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName OrderServiceImpl
 * @Description:
 * @Author Kenny
 * @Date 2020/3/24
 **/

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;



    /**
     * 秒杀
     *
     * @param productId 商品id
     * @return
     */
    @Override
    public String seckill(Long productId) {


        int count = productMapper.updateProductStockByProductId(productId);
        if (count <= 0) {
            log.info("库存不足");
            throw new RuntimeException("库存不足");
        }

        Product product = productMapper.selectByPrimaryKey(productId);

        Order order = new Order();
        order.setProductId(productId);
        order.setProductName(product.getProductName());
        orderMapper.insertSelective(order);
        return "success";
    }
}

