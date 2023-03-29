package com.xucl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xucl.entity.Orders;


public interface OrdersService extends IService<Orders> {
    public void submit(Orders orders);
}
