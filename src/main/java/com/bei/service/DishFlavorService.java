package com.bei.service;

import com.bei.model.DishFlavor;

import java.util.List;

public interface DishFlavorService {

    /**
     * 插入一个餐品口味
     * @param dishFlavor 要插入的对象
     * */
    int addDishFlavor(DishFlavor dishFlavor);

    /**
     * 批量插入餐品口味
     * @param dishFlavorList 口味列表
     * @param dishId 餐品id
     * */
    void addDishFlavorBatch(List<DishFlavor> dishFlavorList, Long dishId);

    /**
     * 根据菜品id查询口味信息
     * */
    List<DishFlavor> getFlavorByDish(Long id);

    /**
     * 根据dishId删除对应的口味信息
     * */
    void removeByDish(Long id);

    /**
     * 批量删除菜品口味记录
     * @param idList 菜品id列表
     * */
    int deleteDishBatches(List<Long> idList);
}
