package com.xucl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xucl.entity.Category;

public interface CategoryService extends IService <Category>{

    /**
     * TODO
     * 根据id删除分类，删除之前需要判断是否关联菜品或者套餐
     * @param id
     * @date
     */
    public void remove(Long id);
}
