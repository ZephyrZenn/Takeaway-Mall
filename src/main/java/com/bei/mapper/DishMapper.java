package com.bei.mapper;

import com.bei.model.Dish;
import com.bei.model.DishExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DishMapper {
    long countByExample(DishExample example);

    int deleteByExample(DishExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Dish row);

    int insertSelective(Dish row);

    List<Dish> selectByExample(DishExample example);

    Dish selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") Dish row, @Param("example") DishExample example);

    int updateByExample(@Param("row") Dish row, @Param("example") DishExample example);

    int updateByPrimaryKeySelective(Dish row);

    int updateByPrimaryKey(Dish row);
}