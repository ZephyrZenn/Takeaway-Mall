package com.bei.service.impl;

import com.bei.mapper.DishMapper;
import com.bei.model.Dish;
import com.bei.model.DishExample;
import com.bei.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Override
    public List<Dish> getDishByCategory(Long id) {
        DishExample dishExample = new DishExample();
        dishExample.createCriteria().andCategoryIdEqualTo(id);
        return dishMapper.selectByExample(dishExample);
    }

    @Override
    public long getCountByCategory(Long id) {
        DishExample dishExample = new DishExample();
        dishExample.createCriteria().andCategoryIdEqualTo(id);
        return dishMapper.countByExample(dishExample);
    }
}
