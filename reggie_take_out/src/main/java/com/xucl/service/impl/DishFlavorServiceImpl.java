package com.xucl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xucl.entity.DishFlavor;
import com.xucl.mapper.DishFlavorMapper;
import com.xucl.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @author xucl
 * @apiNote
 * @date 2023/3/24 11:38
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
