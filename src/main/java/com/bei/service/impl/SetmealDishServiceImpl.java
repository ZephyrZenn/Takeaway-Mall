package com.bei.service.impl;

import com.bei.dto.AdminUserDetail;
import com.bei.mapper.SetmealDishMapper;
import com.bei.model.SetmealDish;
import com.bei.model.SetmealDishExample;
import com.bei.service.SetmealDishService;
import com.bei.utils.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealDishServiceImpl implements SetmealDishService {

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public void addBatches(List<SetmealDish> setmealDishes, Long setmealId) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SnowflakeIdUtils snowflakeIdUtils = new SnowflakeIdUtils(principal.getId(), 1);
        setmealDishes = setmealDishes.stream()
                .peek(setmealDish -> {
                    setmealDish.setId(snowflakeIdUtils.nextId());
                    setmealDish.setSort(0);
                    setmealDish.setIsDeleted(0);
                    setmealDish.setSetmealId(String.valueOf(setmealId));
                    setmealDish.setCreateTime(new Date());
                    setmealDish.setUpdateTime(new Date());
                    setmealDish.setUpdateUser(principal.getId());
                    setmealDish.setCreateUser(principal.getId());
                })
                .collect(Collectors.toList());
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public int deleteSetmeal(Long id) {
        SetmealDishExample example = new SetmealDishExample();
        example.createCriteria().andSetmealIdEqualTo(String.valueOf(id));
        return setmealDishMapper.deleteByExample(example);
    }

    @Override
    public List<SetmealDish> getSetmeal(Long id) {
        SetmealDishExample example = new SetmealDishExample();
        example.createCriteria().andSetmealIdEqualTo(String.valueOf(id));
        return setmealDishMapper.selectByExample(example);
    }
}
