package com.xucl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xucl.dto.SetmealDto;
import com.xucl.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
    public void  removeWithDish(List<Long> ids);
    public SetmealDto getByIdWithDish(Long id);
    public void updateWithDish(SetmealDto setmealDto);

}
