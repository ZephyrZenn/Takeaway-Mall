package com.bei.service.impl;

import com.bei.dto.AdminUserDetail;
import com.bei.mapper.DishFlavorMapper;
import com.bei.model.DishFlavor;
import com.bei.model.DishFlavorExample;
import com.bei.service.DishFlavorService;
import com.bei.utils.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishFlavorServiceImpl implements DishFlavorService {

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    public int addDishFlavor(DishFlavor dishFlavor) {
        SnowflakeIdUtils snowflakeIdUtils = new SnowflakeIdUtils(4, 1);
        dishFlavor.setId(snowflakeIdUtils.nextId());
        dishFlavor.setCreateTime(new Date());
        dishFlavor.setUpdateTime(new Date());
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dishFlavor.setCreateUser(principal.getId());
        dishFlavor.setUpdateUser(principal.getId());
        return dishFlavorMapper.insert(dishFlavor);
    }

    @Override
    public void addDishFlavorBatch(List<DishFlavor> dishFlavorList, Long dishId) {
        SnowflakeIdUtils snowflakeIdUtils = new SnowflakeIdUtils(4, 1);
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long uid = principal.getId();
        dishFlavorList = dishFlavorList.stream()
                .peek(dishFlavor -> {
                    dishFlavor.setId(snowflakeIdUtils.nextId());
                    dishFlavor.setDishId(dishId);
                    dishFlavor.setCreateTime(new Date());
                    dishFlavor.setUpdateTime(new Date());
                    dishFlavor.setUpdateUser(uid);
                    dishFlavor.setCreateUser(uid);
                    dishFlavor.setIsDeleted(1);
                }).collect(Collectors.toList());
        dishFlavorMapper.insertBatch(dishFlavorList);
    }

    @Override
    public List<DishFlavor> getFlavorByDish(Long id) {
        DishFlavorExample example = new DishFlavorExample();
        example.createCriteria().andDishIdEqualTo(id);
        return dishFlavorMapper.selectByExample(example);
    }

    @Override
    public void removeByDish(Long id) {
        DishFlavorExample example = new DishFlavorExample();
        example.createCriteria().andDishIdEqualTo(id);
        dishFlavorMapper.deleteByExample(example);
    }

    @Override
    public int deleteDishBatches(List<Long> idList) {
        DishFlavorExample example = new DishFlavorExample();
        example.createCriteria().andDishIdIn(idList);
        return dishFlavorMapper.deleteByExample(example);
    }
}
