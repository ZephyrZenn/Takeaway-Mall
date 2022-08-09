package com.bei.controller;

import com.bei.common.CommonResult;
import com.bei.dto.DishDto;
import com.bei.model.Category;
import com.bei.model.Dish;
import com.bei.model.DishFlavor;
import com.bei.service.CategoryService;
import com.bei.service.DishFlavorService;
import com.bei.service.DishService;
import com.bei.vo.DishVo;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping("")
    @Transactional
    public CommonResult addDish(@RequestBody DishDto dishParam) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishParam, dish);
        Long dishId = dishService.addDish(dish);
        dishFlavorService.addDishFlavorBatch(dishParam.getFlavors(), dishId);
        return CommonResult.success(dishId);
    }

    @GetMapping("/page")
    public CommonResult getDishPage(int page, int pageSize, String name) {
        List<Dish> dishList = dishService.getDishPage(page, pageSize, name);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd");
        PageInfo pageInfo = new PageInfo(dishList);
        List list = (List) pageInfo.getList().stream()
                .map(o -> {
                    Dish dish = (Dish) o;
                    DishVo dishVo = new DishVo();
                    BeanUtils.copyProperties(dish, dishVo);
                    Category category = categoryService.getCategoryById(dish.getCategoryId());
                    dishVo.setUpdateTime(dateFormat.format(dish.getUpdateTime()));
                    dishVo.setCategoryName(category.getName());
                    return dishVo;
                })
                .collect(Collectors.toList());
        pageInfo.setList(list);
        return CommonResult.success(pageInfo);
    }

    @GetMapping("/{id}")
    public CommonResult getDishDetail(@PathVariable Long id) {
        Dish dish = dishService.getDishById(id);
        if (dish == null) {
            return CommonResult.error("没有该菜品的信息");
        }
        List<DishFlavor> flavors = dishFlavorService.getFlavorByDish(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(flavors);
        return CommonResult.success(dishDto);
    }

    @PutMapping
    @Transactional
    public CommonResult updateDish(@RequestBody DishDto dishDto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);
        int count = dishService.updateDish(dish);
        if (count != 1) {
            log.debug("更新菜品 [" + dishDto + "] 失败");
            return CommonResult.error( "更新菜品失败");
        }
        dishFlavorService.removeByDish(dish.getId());
        dishFlavorService.addDishFlavorBatch(dishDto.getFlavors(), dish.getId());
        return CommonResult.success(dish.getId());
    }
}
