package com.kenny.cloud.seckill.mapper;

import com.kenny.cloud.seckill.domain.Product;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ProductMapper extends Mapper<Product> {
    int updateProductStockByProductId(@Param("productId") Long productId);
}