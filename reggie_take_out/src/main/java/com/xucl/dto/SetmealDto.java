package com.xucl.dto;

import com.xucl.entity.Setmeal;
import com.xucl.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
