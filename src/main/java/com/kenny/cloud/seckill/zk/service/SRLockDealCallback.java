package com.kenny.cloud.seckill.zk.service;

/**
 * @ClassName SRLockDealCallback
 * @Description:
 * @Author Kenny
 * @Date 2020/3/24
 **/
public interface SRLockDealCallback<T> {

    /**
     * 获取可重入共享锁后的处理方法
     * @return
     */
    public T deal();

}