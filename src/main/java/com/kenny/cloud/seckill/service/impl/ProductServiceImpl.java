package com.kenny.cloud.seckill.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.kenny.cloud.seckill.mapper.ProductMapper;
import com.kenny.cloud.seckill.service.ProductService;
@Service
public class ProductServiceImpl implements ProductService{

    @Resource
    private ProductMapper productMapper;

}
