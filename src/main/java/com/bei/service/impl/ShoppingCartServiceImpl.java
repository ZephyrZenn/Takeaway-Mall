package com.bei.service.impl;

import com.bei.mapper.ShoppingCartMapper;
import com.bei.model.ShoppingCart;
import com.bei.model.ShoppingCartExample;
import com.bei.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    public int addShoppingCart(ShoppingCart shoppingCart) {
        return shoppingCartMapper.insertSelective(shoppingCart);
    }

    @Override
    public List<ShoppingCart> getShoppingCart(ShoppingCart cart) {
        ShoppingCartExample example = new ShoppingCartExample();
        ShoppingCartExample.Criteria criteria = example.createCriteria();
        if (cart.getId() != null) {
            criteria.andIdEqualTo(cart.getId());
        }
        if (cart.getDishId() != null) {
            criteria.andDishIdEqualTo(cart.getDishId());
        }
        if (cart.getSetmealId() != null) {
            criteria.andSetmealIdEqualTo(cart.getSetmealId());
        }
        if (cart.getUserId() != null) {
            criteria.andUserIdEqualTo(cart.getUserId());
        }
        return shoppingCartMapper.selectByExample(example);
    }

    @Override
    public int updateShoppingCart(ShoppingCart shoppingCart) {
        if (shoppingCart.getId() != null)
            return shoppingCartMapper.updateByPrimaryKeySelective(shoppingCart);
        ShoppingCartExample example = new ShoppingCartExample();
        ShoppingCartExample.Criteria criteria = example.createCriteria();
        if (shoppingCart.getSetmealId() != null) {
            criteria.andSetmealIdEqualTo(shoppingCart.getSetmealId());
        }
        if (shoppingCart.getDishId() != null) {
            criteria.andDishIdEqualTo(shoppingCart.getDishId());
        }
        if (shoppingCart.getUserId() != null) {
            criteria.andUserIdEqualTo(shoppingCart.getUserId());
        }
        return shoppingCartMapper.updateByExampleSelective(shoppingCart, example);
    }



    @Override
    public int deleteItemByUser(Long id) {
        ShoppingCartExample example = new ShoppingCartExample();
        example.createCriteria().andUserIdEqualTo(id);
        return shoppingCartMapper.deleteByExample(example);
    }
}
