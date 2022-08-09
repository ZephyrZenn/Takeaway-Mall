package com.bei.service;

import com.bei.model.Dish;
import com.bei.vo.DishVo;

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

    /**
     * 插入菜品
     * @param dish
     * */
    Long addDish(Dish dish);

    /**
     * 分页查询Dish
     * @param page 页码
     * @param pageSize 每页数据量
     * @param name 需要匹配的名称
     * */
    List<Dish> getDishPage(int page, int pageSize, String name);

    /**
     * 根据id获取dish
     * @param id 菜品id
     * */
    Dish getDishById(Long id);

    /**
     * 更新dish信息
     * @param dish dish的新信息，其中必须包含主键
     * */
    int updateDish(Dish dish);
}
