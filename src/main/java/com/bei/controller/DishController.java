package com.bei.controller;

import com.bei.common.BusinessException;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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

    @GetMapping("/list")
    public CommonResult getDishList(Dish dish) {
        if (dish.getCategoryId() == null) {
            return CommonResult.error("分类参数为空");
        }
        List<Dish> dishList = dishService.getDishByCategory(dish.getCategoryId());
        dishList = dishList.stream()
                .filter(dish1 -> {
                    return dish1.getStatus() == 1;
                })
                .collect(Collectors.toList());
        return CommonResult.success(dishList);
    }

    @DeleteMapping
    @Transactional
    public CommonResult deleteDish(String ids) {
        List<Long> list = convertIdsToList(ids);
        int count = dishService.deleteDishBatches(list);
        if (count == 0) {
            log.debug("删除 " + ids + " 失败，数据库中缺少该记录");
            throw new BusinessException("删除失败,该菜品未被记录");
        }
        count = dishFlavorService.deleteDishBatches(list);
        if (count == 0) {
            log.debug("删除 " + ids + " 失败，没有删除菜品口味关系表中的记录");
            throw new BusinessException("删除菜品失败");
        }
        return CommonResult.success("删除菜品成功");
    }

    @PostMapping("/status/0")
    public CommonResult disableDish(String ids) {
        List<Long> idList = convertIdsToList(ids);
        for (Long id : idList) {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(0);
            int count = dishService.updateDish(dish);
            if (count != 1) {
                log.debug("停售 " + id + " 失败，数据库没有找到操作对象");
                throw new BusinessException("停售失败，请检查参数是否正确");
            }
        }
        return CommonResult.success("停售成功");
    }

    @PostMapping("/status/1")
    public CommonResult enableDish(String ids) {
        List<Long> idList = convertIdsToList(ids);
        for (Long id : idList) {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(1);
            int count = dishService.updateDish(dish);
            if (count != 1) {
                log.debug("启售 " + ids + " 失败，数据库没有找到操作对象");
                throw new BusinessException("启售失败，请检查参数是否正确");
            }
        }
        return CommonResult.success("启售成功");
    }

    private List<Long> convertIdsToList(String ids) {
        String[] idList = ids.split(",");
        List<Long> list = Arrays.stream(idList)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        return list;
    }
}
