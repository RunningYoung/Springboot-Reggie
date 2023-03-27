package com.xucl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xucl.common.R;
import com.xucl.dto.SetmealDto;
import com.xucl.entity.Category;
import com.xucl.entity.Setmeal;
import com.xucl.service.CategoryService;
import com.xucl.service.SetmealDishService;
import com.xucl.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xucl
 * @apiNote 套餐管理
 * @date 2023/3/27 10:14
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;
    /**
     * TODO
     * 新增套餐
     * @param setmealDto
     * @return com.xucl.common.R<java.lang.String>
     * @date
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息:{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> pageDto = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> dtoList = records.stream().map((item) -> {
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item, dto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String name1 = category.getName();
                dto.setCategoryName(name1);
            }
            return dto;
        }).collect(Collectors.toList());

        pageDto.setRecords(dtoList);

        return R.success(pageDto);
    }


    /**
     * TODO
     * 单独或者批量删除
     * @param ids
     * @return com.xucl.common.R<java.lang.String>
     * @date
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("dis:{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable int status,@RequestParam List<Long> ids){
        log.info("套餐信息：{}",ids);
        List<Setmeal> setmeals = setmealService.listByIds(ids);
        setmeals.stream().map((item) -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(setmeals);
        return R.success("套餐状态修改成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto byIdWithDish = setmealService.getByIdWithDish(id);
        return R.success(byIdWithDish);
    }


    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){

        setmealService.updateWithDish(setmealDto);
        return R.success("套餐更新成功");
    }

}
