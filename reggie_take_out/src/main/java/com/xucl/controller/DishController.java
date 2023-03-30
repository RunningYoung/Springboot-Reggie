package com.xucl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xucl.common.R;
import com.xucl.dto.DishDto;
import com.xucl.entity.Category;
import com.xucl.entity.Dish;
import com.xucl.entity.DishFlavor;
import com.xucl.service.CategoryService;
import com.xucl.service.DishFlavorService;
import com.xucl.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author xucl
 * @apiNote
 * @date 2023/3/24 11:41
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * TODO
     * 新增菜品
     * @param dishDto
     * @return com.xucl.common.R<java.lang.String>
     * @date
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("新增菜品:=== {}",dishDto);
        dishService.saveWithFlavor(dishDto);
        //清理所有菜品的缓存数据
        // Set keys = redisTemplate.keys("dish_*");
        // redisTemplate.delete(keys);

        //清理某个分类下面的缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }

    /**
     * TODO
     *  菜品分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return com.xucl.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @date
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page,int pageSize,String name){
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dtoList = records.stream().map((item)->{
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(item, dto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dto.setCategoryName(categoryName);
            }
            return dto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dtoList);
        return R.success(dishDtoPage);
    }

    /**
     * TODO
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return com.xucl.common.R<com.xucl.dto.DishDto>
     * @date
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * TODO
     * 修改菜品
     * @param dishDto
     * @return com.xucl.common.R<java.lang.String>
     * @date
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("更新菜品:=== {}",dishDto);
        dishService.updateWithFlavor(dishDto);
        //清理所有菜品的缓存数据
        // Set keys = redisTemplate.keys("dish_*");
        // redisTemplate.delete(keys);

        //清理某个分类下面的缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("更新菜品成功");
    }

    /**
     * TODO
     * 根据id删除菜品
     * @param ids
     * @return com.xucl.common.R<java.lang.String>
     * @date
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteWithid(ids);
        return R.success("删除菜品成功");
    }


    @PostMapping("/status/{status}")
    public R<String> stop(@PathVariable int status, @RequestParam List<Long> ids){
        List<Dish> dishes = dishService.listByIds(ids);
        dishes = dishes.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishes);
        return R.success("菜品状态修改成功");
    }

    /**
     * TODO
     * 根据条件查询菜品数据
     * @param dish
     * @return com.xucl.common.R<java.util.List<com.xucl.entity.Dish>>
     * @date
     */
    // @GetMapping("/list")
    // public R<List<Dish>> list(Dish dish){
    //     LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    //     queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
    //     queryWrapper.eq(Dish::getStatus,1);
    //     queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    //     List<Dish> list = dishService.list(queryWrapper);
    //     return R.success(list);
    // }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        String key = "dish" + dish.getCategoryId() + "_" + dish.getStatus();
        //先从redis中获取缓存
        List<DishDto> dtos = (List<DishDto>)redisTemplate.opsForValue().get(key);
        //如果存在直接返回，无需查询数据库
        if (dtos != null) {
            return R.success(dtos);
        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto> dtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if (byId != null) {
                String name = byId.getName();
                dishDto.setCategoryName(name);
            }
            Long itemId = item.getId();
            LambdaQueryWrapper<DishFlavor> wrap = new LambdaQueryWrapper<>();
            wrap.eq(DishFlavor::getDishId,itemId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(wrap);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());
        //如果不存在，需要查询数据库 将菜品数据缓存到redis中
        redisTemplate.opsForValue().set(key,dtoList,60, TimeUnit.MINUTES);
        return R.success(dtoList);
    }
}
