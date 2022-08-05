package com.bei.mapper;

import com.bei.model.Setmeal;
import com.bei.model.SetmealExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SetmealMapper {
    long countByExample(SetmealExample example);

    int deleteByExample(SetmealExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Setmeal row);

    int insertSelective(Setmeal row);

    List<Setmeal> selectByExample(SetmealExample example);

    Setmeal selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") Setmeal row, @Param("example") SetmealExample example);

    int updateByExample(@Param("row") Setmeal row, @Param("example") SetmealExample example);

    int updateByPrimaryKeySelective(Setmeal row);

    int updateByPrimaryKey(Setmeal row);
}