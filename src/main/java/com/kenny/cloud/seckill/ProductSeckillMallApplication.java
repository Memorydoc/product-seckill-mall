package com.kenny.cloud.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.kenny.cloud.seckill.mapper")
public class ProductSeckillMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductSeckillMallApplication.class, args);
    }

}
