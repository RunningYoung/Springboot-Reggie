package com.xucl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xucl.common.R;
import com.xucl.entity.Category;
import com.xucl.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xucl
 * @apiNote
 * @date 2023/3/23 16:52
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * TODO
     * 新增分类
     * @param category
     * @return com.xucl.common.R<java.lang.String>
     * @date
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("Category = {}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * TODO
     * 分页查询
     * @param page
     * @param pageSize
     * @return com.xucl.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @date
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * TODO
     * 删除分类
     * @param id
     * @return com.xucl.common.R<java.lang.String>
     * @date
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类 id = " + ids);
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    /**
     * TODO
     * 根据id修改分类信息
     * @param category
     * @return com.xucl.common.R<java.lang.String>
     * @date
     */
    @PutMapping
    public R<String> update (@RequestBody Category category){
        log.info("修改分类信息:{}",category);
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }

    /**
     * TODO
     * 获取菜品分类
     * @param category
     * @return com.xucl.common.R<java.util.List<com.xucl.entity.Category>>
     * @date
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        log.info("获取分类数据");
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
