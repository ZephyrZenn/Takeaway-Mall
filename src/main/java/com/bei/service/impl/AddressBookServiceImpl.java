package com.bei.service.impl;

import com.bei.dto.AdminUserDetail;
import com.bei.mapper.AddressBookMapper;
import com.bei.model.AddressBook;
import com.bei.model.AddressBookExample;
import com.bei.service.AddressBookService;
import com.bei.utils.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public List<AddressBook> getAddressList(Long id) {
        AddressBookExample example = new AddressBookExample();
        example.createCriteria().andUserIdEqualTo(id);
        return addressBookMapper.selectByExample(example);
    }

    @Override
    public AddressBook getAddress(Long id) {
        return addressBookMapper.selectByPrimaryKey(id);
    }

    @Override
    public int addAddress(AddressBook addressBook) {
        SnowflakeIdUtils snowflakeIdUtils = new SnowflakeIdUtils(2, 1);
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        addressBook.setId(snowflakeIdUtils.nextId());
        addressBook.setUserId(principal.getId());
        addressBook.setCreateUser(principal.getId());
        addressBook.setUpdateUser(principal.getId());
        addressBook.setCreateTime(new Date());
        addressBook.setUpdateTime(new Date());
        addressBook.setIsDefault(false);
        addressBook.setIsDeleted(0);
        return addressBookMapper.insert(addressBook);
    }

    @Override
    public int updateAddress(AddressBook addressBook) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        addressBook.setUpdateTime(new Date());
        addressBook.setUpdateUser(principal.getId());
        return addressBookMapper.updateByPrimaryKeySelective(addressBook);
    }

    @Override
    public AddressBook getDefaultAddress(Long id) {
        AddressBookExample example = new AddressBookExample();
        example.createCriteria().andIsDefaultEqualTo(true).andUserIdEqualTo(id);
        List<AddressBook> addressBooks = addressBookMapper.selectByExample(example);
        if (addressBooks.size() != 0) {
            return addressBooks.get(0);
        }
        return null;
    }
}
