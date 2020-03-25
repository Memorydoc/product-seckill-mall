package com.kenny.cloud.seckill.constants;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ProductRedisConstants
 * @Description:
 * @Author Kenny
 * @Date 2020/3/24
 **/

@Data
public class ProductRedisConstants implements Serializable {
    private static final long serialVersionUID = -7838093200465902695L;

    public static final String PRODUCT_STOCK_PREFIX = "product_stock_";


    public static final String PRODUCT_STOCK_JVM_PREFIX = "/product_stock_jvm_cache";


    public static String getProductZkKey(Long productId) {
        return PRODUCT_STOCK_JVM_PREFIX + "/" + productId;
    }
}
