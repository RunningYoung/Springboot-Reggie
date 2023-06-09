package com.xucl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xucl.dto.DishDto;
import com.xucl.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    public void deleteWithid(List<Long> ids);

}
