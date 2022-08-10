package com.bei.service.impl;

import com.bei.dto.AdminUserDetail;
import com.bei.dto.param.PageParam;
import com.bei.mapper.SetmealMapper;
import com.bei.model.Setmeal;
import com.bei.model.SetmealExample;
import com.bei.service.SetmealService;
import com.bei.utils.SnowflakeIdUtils;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    @Override
    public Long addSetmeal(Setmeal setmeal) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SnowflakeIdUtils snowflakeIdUtils = new SnowflakeIdUtils(3, 1);
        setmeal.setCreateTime(new Date());
        setmeal.setUpdateTime(new Date());
        setmeal.setCreateUser(principal.getId());
        setmeal.setUpdateUser(principal.getId());
        Long id = snowflakeIdUtils.nextId();
        setmeal.setId(id);
        setmealMapper.insertSelective(setmeal);
        return id;
    }

    @Override
    public List<Setmeal> getSetmealPage(PageParam pageParam) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getPageSize());
        SetmealExample example = new SetmealExample();
        if (StringUtils.isNotBlank(pageParam.getName())) {
            example.createCriteria().andNameEqualTo(pageParam.getName());
        }
        return setmealMapper.selectByExample(example);
    }

    @Override
    public int deleteSetmeal(Long id) {
        return setmealMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int updateSetmeal(Setmeal setmeal) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        setmeal.setUpdateUser(principal.getId());
        setmeal.setUpdateTime(new Date());
        return setmealMapper.updateByPrimaryKeySelective(setmeal);
    }

    @Override
    public Setmeal getSetmeal(Long id) {
        return setmealMapper.selectByPrimaryKey(id);
    }
}
