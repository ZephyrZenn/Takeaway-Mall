package com.bei.mapper;

import com.bei.model.SetmealDish;
import com.bei.model.SetmealDishExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SetmealDishMapper {
    long countByExample(SetmealDishExample example);

    int deleteByExample(SetmealDishExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SetmealDish row);

    int insertSelective(SetmealDish row);

    List<SetmealDish> selectByExample(SetmealDishExample example);

    SetmealDish selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") SetmealDish row, @Param("example") SetmealDishExample example);

    int updateByExample(@Param("row") SetmealDish row, @Param("example") SetmealDishExample example);

    int updateByPrimaryKeySelective(SetmealDish row);

    int updateByPrimaryKey(SetmealDish row);

    void insertBatch(List<SetmealDish> setmealDishList);
}