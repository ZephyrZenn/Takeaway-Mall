package com.bei.service;

import com.bei.model.Dish;

import java.util.List;

public interface DishService {

    /** 
     * 获取指定分类的菜品列表
     * @param id 分类id
     * */
    List<Dish> getDishByCategory(Long id);

    /**
     * 查询指定分类的菜品数量
     * */
    long getCountByCategory(Long id);
}
