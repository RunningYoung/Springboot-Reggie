package com.xucl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xucl.common.CustomException;
import com.xucl.entity.Category;
import com.xucl.entity.Dish;
import com.xucl.entity.Setmeal;
import com.xucl.mapper.CategoryMapper;
import com.xucl.service.CategoryService;
import com.xucl.service.DishService;
import com.xucl.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xucl
 * @apiNote
 * @date 2023/3/23 16:52
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        long count = dishService.count(dishLambdaQueryWrapper);
        //查询当前分类是否关联了菜品，如果关联，抛出异常
        if (count > 0) {
            //已经关联菜品，抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        // 查询当前分类是否关联套餐，如果已关联，抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        long count1 = setmealService.count(setmealLambdaQueryWrapper);
        if (count1 > 0) {
            //已关联套餐，抛出异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        super.removeById(id);
        //正常删除分类
    }
}

