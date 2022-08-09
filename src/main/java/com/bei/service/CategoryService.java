package com.bei.service;

import com.bei.dto.param.CategoryParam;
import com.bei.model.Category;

import java.util.List;

public interface CategoryService {
    /**
     * 添加分类
     * @param name 分类名称 不可与数据库内已有记录重复
     * @param type 分类类型
     * @param sort 排序值
     * */
    int addCategory(String name, Integer type, Integer sort);

    /**
     * 分页查询分类，并将结果按sort字段值排序
     * @param page 页码
     * @param pageSize 每页数据量
     * */
    List<Category> getCategoryPage(int page, int pageSize);

    /**
     * 删除分类, 如果分类下还有菜品，则禁止删除
     * @param id 要删除的分类的id
     * */
    int deleteCategory(Long id);

    /**
     * 修改分类
     * @param categoryParam 分类信息，包含id，name，sort
     * */
    int updateCategory(CategoryParam categoryParam);

    /**
     * 查询指定类别的分类
     * @param type 类别
     * */
    List<Category> getCategoryByType(Integer type);

    /**
     * 根据id查找分类
     * @param cid 分类id
     * */
    Category getCategoryById(Long cid);
}
