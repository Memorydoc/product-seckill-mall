package com.kenny.cloud.seckill.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "`order`")
public class Order implements Serializable {
    /**
     * 订单id
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 商品id
     */
    @Column(name = "product_id")
    private Long productId;

    /**
     * 商品名称
     */
    @Column(name = "product_name")
    private String productName;

    private static final long serialVersionUID = 1L;
}