package com.bei.service.impl;

import com.bei.dto.AdminUserDetail;
import com.bei.mapper.DishMapper;
import com.bei.model.Dish;
import com.bei.model.DishExample;
import com.bei.service.DishService;
import com.bei.utils.SnowflakeIdUtils;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Override
    public List<Dish> getDishByCategory(Long id) {
        DishExample dishExample = new DishExample();
        dishExample.createCriteria().andCategoryIdEqualTo(id);
        dishExample.setOrderByClause("sort");
        return dishMapper.selectByExample(dishExample);
    }

    @Override
    public long getCountByCategory(Long id) {
        DishExample dishExample = new DishExample();
        dishExample.createCriteria().andCategoryIdEqualTo(id);
        return dishMapper.countByExample(dishExample);
    }

    @Override
    public Long addDish(Dish dish) {
        SnowflakeIdUtils snowflakeIdUtils = new SnowflakeIdUtils(4, 1);
        dish.setId(snowflakeIdUtils.nextId());
        Long dishId = dish.getId();
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dish.setCreateUser(principal.getId());
        dish.setUpdateUser(principal.getId());
        dish.setCreateTime(new Date());
        dish.setUpdateTime(new Date());
        dish.setIsDeleted(1);
        dishMapper.insert(dish);
        return dishId;
    }

    @Override
    public List<Dish> getDishPage(int page, int pageSize, String name) {
        PageHelper.startPage(page, pageSize);
        DishExample example = new DishExample();
        if (StringUtils.isNotBlank(name)) {
            example.createCriteria().andNameEqualTo(name);
        }
        return dishMapper.selectByExample(example);
    }

    @Override
    public Dish getDishById(Long id) {
        return dishMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateDish(Dish dish) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dish.setUpdateTime(new Date());
        dish.setUpdateUser(principal.getId());
        return dishMapper.updateByPrimaryKeySelective(dish);
    }

    @Override
    public int deleteDishBatches(List<Long> idList) {

        DishExample example = new DishExample();
        example.createCriteria().andIdIn(idList);
        return dishMapper.deleteByExample(example);
    }
}
