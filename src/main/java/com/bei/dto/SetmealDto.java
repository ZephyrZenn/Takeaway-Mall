package com.bei.dto;

import com.bei.model.Setmeal;
import com.bei.model.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
