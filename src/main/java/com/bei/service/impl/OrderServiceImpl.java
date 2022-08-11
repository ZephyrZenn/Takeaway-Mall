package com.bei.service.impl;

import com.bei.mapper.OrderDetailMapper;
import com.bei.mapper.OrdersMapper;
import com.bei.model.*;
import com.bei.service.OrderService;
import com.bei.utils.SnowflakeIdUtils;
import com.bei.vo.OrderVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public String submitOrder(Orders orders, List<ShoppingCart> shoppingCartList, AddressBook address, User user) {
        SnowflakeIdUtils snowflakeIdUtils = new SnowflakeIdUtils(5, 1);
        String orderId = String.valueOf(snowflakeIdUtils.nextId());
        orders.setNumber(orderId);
        orders.setOrderTime(new Date());
        orders.setStatus(2);
        orders.setEmail(user.getEmail());
        orders.setAddressBookId(address.getId());
        orders.setConsignee(address.getConsignee());
        StringBuilder ad = new StringBuilder();
        ad.append(address.getProvinceName() != null ? address.getProvinceName() : "")
                .append(address.getCityName() != null ? address.getCityName() : "")
                .append(address.getDistrictName() != null ? address.getDistrictName() : "")
                .append(address.getDetail() != null ? address.getDetail() : "");
        orders.setAddress(ad.toString());
        orders.setUserId(user.getId());
        orders.setUserName(user.getName());
        BigDecimal amount = BigDecimal.valueOf(0);
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setId(null);
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(Long.valueOf(orderId));
            orderDetailMapper.insert(orderDetail);
            amount = amount.add(cart.getAmount().multiply(BigDecimal.valueOf(cart.getNumber())));
        }
        orders.setAmount(amount);
        ordersMapper.insert(orders);
        return orderId;
    }

    @Override
    public PageInfo getUserOrderDetails(Long uid, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        OrdersExample ordersExample = new OrdersExample();
        ordersExample.createCriteria().andUserIdEqualTo(uid);
        ordersExample.setOrderByClause("order_time");
        List<Orders> orders = ordersMapper.selectByExample(ordersExample);
        PageInfo pageInfo = new PageInfo(orders);
        List<OrderVo> orderVoList = new ArrayList<>();
        for (Orders order : orders) {
            OrderDetailExample detailExample = new OrderDetailExample();
            detailExample.createCriteria().andOrderIdEqualTo(Long.valueOf(order.getNumber()));
            List<OrderDetail> orderDetails = orderDetailMapper.selectByExample(detailExample);
            OrderVo orderVo = new OrderVo();
            BeanUtils.copyProperties(order, orderVo);
            orderVo.setOrderDetails(orderDetails);
            orderVo.setSumNum(orderDetails.size());
            orderVoList.add(orderVo);
        }
        pageInfo.setList(orderVoList);
        return pageInfo;
    }
}
