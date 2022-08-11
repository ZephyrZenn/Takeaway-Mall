package com.bei.service;

import com.bei.dto.param.PageParam;
import com.bei.model.Setmeal;

import java.util.List;

public interface SetmealService {
    /**
     * 获取指定分类的套餐数量
     *
     * @param id 分类id
     */
    long getCountByCategory(Long id);

    /**
     * 添加套餐
     * @param setmeal 套餐
     * @return 新套餐的id
     * */
    Long addSetmeal(Setmeal setmeal);

    /**
     * 获取套餐分页
     * */
    List<Setmeal> getSetmealPage(PageParam pageParam);

    /**
     * 删除指定套餐
     * */
    int deleteSetmeal(Long id);

    /**
     * 更新套餐信息
     * @param setmeal 包含要更新的套餐信息，必须包含主键
     * */
    int updateSetmeal(Setmeal setmeal);

    /**
     * 根据id获取套餐
     * */
    Setmeal getSetmeal(Long id);

    /**
     * 根据传入对象的参数查找对象
     * @param setmeal 查找参数
     * */
    List<Setmeal> getSetmeal(Setmeal setmeal);
}
