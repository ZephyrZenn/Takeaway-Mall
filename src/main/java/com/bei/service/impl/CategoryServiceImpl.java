package com.bei.service.impl;

import com.bei.common.param.CategoryParam;
import com.bei.dto.AdminUserDetail;
import com.bei.mapper.CategoryMapper;
import com.bei.model.Category;
import com.bei.model.CategoryExample;
import com.bei.service.CategoryService;
import com.bei.utils.SnowflakeIdUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public int addCategory(String name, Integer type, Integer sort) {
        Category category = new Category();
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SnowflakeIdUtils snowflakeIdUtils = new SnowflakeIdUtils(principal.getId(), 1);
        category.setId(snowflakeIdUtils.nextId());
        category.setName(name);
        category.setSort(sort);
        category.setType(type);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        category.setCreateUser(principal.getId());
        category.setUpdateUser(principal.getId());
        return categoryMapper.insert(category);
    }

    @Override
    public List<Category> getCategoryPage(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        CategoryExample example = new CategoryExample();
        example.setOrderByClause("sort");
        return categoryMapper.selectByExample(example);
    }

    @Override
    public int deleteCategory(Long id) {
        return categoryMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int updateCategory(CategoryParam categoryParam) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Category category = new Category();
        category.setId(categoryParam.getId());
        category.setName(categoryParam.getName());
        category.setSort(categoryParam.getSort());
        category.setUpdateUser(principal.getId());
        category.setUpdateTime(new Date());
        return categoryMapper.updateByPrimaryKeySelective(category);
    }
}
