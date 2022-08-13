package com.bei.controller.backend;

import com.bei.annotation.Cache;
import com.bei.annotation.CleanCache;
import com.bei.common.CommonResult;
import com.bei.dto.param.CategoryParam;
import com.bei.dto.param.PageParam;
import com.bei.model.Category;
import com.bei.service.CategoryService;
import com.bei.service.DishService;
import com.bei.service.SetmealService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @CleanCache(name = "category")
    public CommonResult addCategory(@RequestBody CategoryParam categoryParam) {
        int count = categoryService.addCategory(categoryParam.getName(), categoryParam.getType(), categoryParam.getSort());
        if (count == 1) {
            log.debug("添加分类 " + categoryParam + " 成功");
            return CommonResult.success("添加分类成功");
        }
        log.debug("添加分类 " + categoryParam + " 失败");
        return CommonResult.error("添加分类失败");
    }

    @GetMapping("/page")
    @Cache(name = "categoryPage")
    public CommonResult getCategoryPage(PageParam pageParam) {
        List<Category> categoryList = categoryService.getCategoryPage(pageParam.getPage(), pageParam.getPageSize());
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        return CommonResult.success(pageInfo);
    }

    @DeleteMapping()
    @CleanCache(name = "category")
    public CommonResult deleteCategory(Long id) {
        long dishCount = dishService.getCountByCategory(id);
        long setmealCount = setmealService.getCountByCategory(id);
        if (dishCount > 0 || setmealCount > 0) {
            return CommonResult.error("当前分类下仍有菜品，不可删除");
        }

        int count = categoryService.deleteCategory(id);
        if (count == 1) {
            log.debug("删除分类 [" + id + "] 成功");
            return CommonResult.success("删除成功");
        } else {
            log.debug("删除分类 [" + id + "] 失败");
            return CommonResult.error("删除失败");
        }
    }

    @PutMapping("")
    @CleanCache(name = "category")
    public CommonResult updateCategory(@RequestBody CategoryParam categoryParam) {
        int count = categoryService.updateCategory(categoryParam);
        if (count == 1) {
            log.debug("更新分类 " + categoryParam + " 成功");
            return CommonResult.success("更新成功");
        } else {
            log.debug("更新分类 " + categoryParam + " 失败");
            return CommonResult.success("更新失败");
        }
    }

    @GetMapping("/list")
    @Cache(name = "categoryList")
    public CommonResult listCategory(CategoryParam categoryParam) {
        if (categoryParam.getType() == null) {
            List<Category> list1 = categoryService.getCategoryByType(Category.DISH_CATEGORY);
            List<Category> list2 = categoryService.getCategoryByType(Category.COMBO_CATEGORY);
            list1.addAll(list2);
            return CommonResult.success(list1);
        }
        List<Category> categoryList = categoryService.getCategoryByType(categoryParam.getType());
        return CommonResult.success(categoryList);
    }
}
