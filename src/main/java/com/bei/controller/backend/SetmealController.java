package com.bei.controller.backend;

import com.bei.common.BusinessException;
import com.bei.common.CommonResult;
import com.bei.dto.SetmealDto;
import com.bei.dto.param.PageParam;
import com.bei.model.Category;
import com.bei.model.Setmeal;
import com.bei.model.SetmealDish;
import com.bei.service.CategoryService;
import com.bei.service.SetmealDishService;
import com.bei.service.SetmealService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @Transactional
    public CommonResult addSetmeal(@RequestBody SetmealDto setmealDto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);
        Long setmealId = setmealService.addSetmeal(setmeal);
        if (setmealId == null) {
            return CommonResult.error("添加套餐失败");
        }
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        if (setmealDishes.size() == 0) {
            return CommonResult.error("套餐中必须有至少一种菜品");
        }
        setmealDishService.addBatches(setmealDishes, setmealId);
        return CommonResult.success(setmealId);
    }

    @GetMapping("/page")
    public CommonResult getSetmealPage(PageParam pageParam) {
        List<Setmeal> setmealList = setmealService.getSetmealPage(pageParam);
        PageInfo pageInfo = new PageInfo(setmealList);
        List list = pageInfo.getList();
        list = (List) list.stream()
                .map(o -> {
                    Setmeal setmeal = (Setmeal) o;
                    SetmealDto setmealDto = new SetmealDto();
                    Category category = categoryService.getCategoryById(setmeal.getCategoryId());
                    setmealDto.setCategoryName(category.getName());
                    BeanUtils.copyProperties(setmeal, setmealDto);
                    return setmealDto;
                })
                .collect(Collectors.toList());
        pageInfo.setList(list);
        return CommonResult.success(pageInfo);
    }

    @DeleteMapping
    @Transactional
    public CommonResult deleteSetmeal(@RequestParam(name = "ids") String ids) {
        List<Long> idList = convertIdsToList(ids);
        for (Long id : idList) {
            int count = setmealService.deleteSetmeal(id);
            if (count != 1) {
                log.debug("删除套餐 " + id + " 失败");
                throw new BusinessException("删除套餐失败，没有查到该套餐");
            }
            count = setmealDishService.deleteSetmeal(id);
            if (count == 0) {
                log.debug("删除套餐 " + id + " 失败, 无法更新菜品套餐关系表");
                throw new BusinessException("删除套餐失败");
            }
            log.debug("删除套餐 " + id + " 成功");
        }
        return CommonResult.success("删除套餐成功");
    }

    @PostMapping("/status/0")
    public CommonResult disableSetmeal(String ids) {
        List<Long> idList = convertIdsToList(ids);
        for (Long id : idList) {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(0);
            int count = setmealService.updateSetmeal(setmeal);
            if (count != 1) {
                log.debug("停售 " + id + " 失败，数据库没有找到操作对象");
                throw new BusinessException("停售失败，请检查参数是否正确");
            }
        }
        return CommonResult.success("停售成功");
    }

    @PostMapping("/status/1")
    public CommonResult enableSetmeal(String ids) {
        List<Long> idList = convertIdsToList(ids);
        for (Long id : idList) {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(1);
            int count = setmealService.updateSetmeal(setmeal);
            if (count != 1) {
                log.debug("启售 " + id + " 失败，数据库没有找到操作对象");
                throw new BusinessException("启售失败，请检查参数是否正确");
            }
        }
        return CommonResult.success("启售成功");
    }

    @GetMapping("/{id}")
    public CommonResult getSetmealDetail(@PathVariable Long id) {
        Setmeal setmeal = setmealService.getSetmeal(id);
        List<SetmealDish> setmealDishList = setmealDishService.getSetmeal(id);
        if (setmeal == null || setmealDishList == null || setmealDishList.size() == 0) {
            log.debug("查询 " + id + " 信息失败");
            throw new BusinessException("查询套餐信息失败");
        }
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        setmealDto.setSetmealDishes(setmealDishList);
        return CommonResult.success(setmealDto);
    }

    @GetMapping("/list")
    public CommonResult getSetmeal(Setmeal setmeal) {
        List<Setmeal> setmealList = setmealService.getSetmeal(setmeal);
        return CommonResult.success(setmealList);
    }

    @PutMapping
    @Transactional
    public CommonResult updateSetmeal(@RequestBody SetmealDto setmealDto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);
        int count = setmealService.updateSetmeal(setmeal);
        if (count == 0) {
            log.debug("更新 " + setmealDto.getId() + " 失败，没有查询到该对象");
            throw new BusinessException("更新失败，没有查询到该套餐");
        }
        count = setmealDishService.deleteSetmeal(setmeal.getId());
        if (count == 0) {
            log.debug("更新 " + setmealDto.getId() + " 失败，无法删除原先的口味关系");
            throw new BusinessException("更新失败，数据库错误");
        }
        setmealDishService.addBatches(setmealDto.getSetmealDishes(), setmeal.getId());
        return CommonResult.success(setmeal.getId());
    }

    private List<Long> convertIdsToList(String ids) {
        String[] idList = ids.split(",");
        List<Long> list = Arrays.stream(idList)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        return list;
    }
}
