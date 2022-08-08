package com.bei.service;

public interface SetmealService {
    /**
     * 获取指定分类的套餐数量
     *
     * @param id 分类id
     */
    long getCountByCategory(Long id);
}
