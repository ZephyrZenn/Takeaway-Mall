package com.bei.service;

import com.bei.model.AddressBook;
import com.bei.model.Orders;
import com.bei.model.ShoppingCart;
import com.bei.model.User;
import com.bei.vo.OrderVo;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface OrderService {
    /**
     * 提交订单
     *
     * @param orders           订单信息
     * @param shoppingCartList 购买物品信息
     * @param address          地址信息
     * @param user             用户信息
     * @return
     */
    String submitOrder(Orders orders, List<ShoppingCart> shoppingCartList, AddressBook address, User user);

    /**
     * 分页获取订单详细信息
     * */
    PageInfo getUserOrderDetails(Long uid, int page, int pageSize);
}
