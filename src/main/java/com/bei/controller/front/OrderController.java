package com.bei.controller.front;

import com.bei.common.BusinessException;
import com.bei.common.CommonResult;
import com.bei.dto.AdminUserDetail;
import com.bei.dto.param.PageParam;
import com.bei.model.AddressBook;
import com.bei.model.Orders;
import com.bei.model.ShoppingCart;
import com.bei.model.User;
import com.bei.service.AddressBookService;
import com.bei.service.OrderService;
import com.bei.service.ShoppingCartService;
import com.bei.service.UserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @Transactional
    public CommonResult submit(@RequestBody Orders orders) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(principal.getId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCart(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            log.debug("购物车为空，无法提交订单");
            throw new BusinessException("购物车不可为空");
        }
        if (orders.getAddressBookId() == null) {
            throw new BusinessException("用户地址不可为空");
        }
        AddressBook address = addressBookService.getAddress(orders.getAddressBookId());
        if (address == null) {
            throw new BusinessException("无法查询到地址信息");
        }
        User user = userService.getUser(principal.getId());
        if (user == null) {
            throw new BusinessException("用户信息有误");
        }
        String orderId = orderService.submitOrder(orders, shoppingCartList, address, user);
        shoppingCartService.deleteItemByUser(user.getId());
        return CommonResult.success(orderId);
    }

    @GetMapping("/userPage")
    public CommonResult getUserOrderPage(PageParam param) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageInfo page = orderService.getUserOrderDetails(principal.getId(), param.getPage(), param.getPageSize());
        return CommonResult.success(page);
    }
}
