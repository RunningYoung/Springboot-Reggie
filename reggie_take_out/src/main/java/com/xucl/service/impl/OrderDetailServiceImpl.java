package com.xucl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xucl.entity.OrderDetail;
import com.xucl.mapper.OrderDetailMapper;
import com.xucl.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author xucl
 * @apiNote
 * @date 2023/3/29 16:01
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
