package com.bei.service;

import com.bei.model.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加一个购物车项目
     * */
    int addShoppingCart(ShoppingCart shoppingCart);

    /**
     * 根据传入对象包含的信息，查询购物车项目
     * */
    List<ShoppingCart> getShoppingCart(ShoppingCart cart);

    /**
     * 更新购物车项目
     * @param shoppingCart 新的购物车信息，必须包含主键
     * */
    int updateShoppingCart(ShoppingCart shoppingCart);

    /**
     * 清空指定用户的购物车
     * */
    int deleteItemByUser(Long id);
}
