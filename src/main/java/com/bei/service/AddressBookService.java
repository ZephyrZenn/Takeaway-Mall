package com.bei.service;

import com.bei.model.AddressBook;

import java.util.List;

public interface AddressBookService {

    /**
     * 获取用户地址薄信息
     * @param id 当前登录用户id
     * */
    List<AddressBook> getAddressList(Long id);

    /**
     * 获取指定地址信息
     * @param id 地址id
     * */
    AddressBook getAddress(Long id);

    /**
     * 添加地址信息
     * */
    int addAddress(AddressBook addressBook);

    /**
     * 更改地址信息
     * @param addressBook 新的地址信息，必须包含主键
     * */
    int updateAddress(AddressBook addressBook);

    /**
     * 获取默认地址
     * @param id 用户id
     * */
    AddressBook getDefaultAddress(Long id);
}
