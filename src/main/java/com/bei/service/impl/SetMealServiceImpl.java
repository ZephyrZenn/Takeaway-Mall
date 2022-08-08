package com.bei.service.impl;

import com.bei.mapper.SetmealMapper;
import com.bei.model.SetmealExample;
import com.bei.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetMealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public long getCountByCategory(Long id) {
        SetmealExample example = new SetmealExample();
        example.createCriteria().andCategoryIdEqualTo(id);
        return setmealMapper.countByExample(example);
    }
}
