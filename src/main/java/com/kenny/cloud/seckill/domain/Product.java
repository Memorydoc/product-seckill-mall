package com.kenny.cloud.seckill.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "product")
public class Product implements Serializable {
    /**
     * 商品id
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 商品名称
     */
    @Column(name = "product_name")
    private String productName;

    /**
     * 库存
     */
    @Column(name = "stock")
    private Long stock;

    private static final long serialVersionUID = 1L;
}