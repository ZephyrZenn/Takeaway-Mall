package com.bei.service;

import com.bei.model.SetmealDish;

import java.util.List;

public interface SetmealDishService {
    /**
     * 批量插入套餐菜品关系
     * */
    void addBatches(List<SetmealDish> setmealDishes, Long setmealId);

    /**
     * 删除与指定套餐有关的菜品关系
     * */
    int deleteSetmeal(Long id);

    /**
     * 根据套餐id查询口味信息
     * */
    List<SetmealDish> getSetmeal(Long id);

    /**
     * 更新菜品套餐中指定菜品对应的条目
     * @param setmealDish 新的菜品套餐关系信息
     * */
    void updateSetmeal(SetmealDish setmealDish);

}
