package com.bei.controller.front;

import com.bei.common.BusinessException;
import com.bei.common.CommonResult;
import com.bei.dto.AdminUserDetail;
import com.bei.model.AddressBook;
import com.bei.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressController {

    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    public CommonResult getAddressList() {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<AddressBook> addressBooks = addressBookService.getAddressList(principal.getId());
        return CommonResult.success(addressBooks);
    }

    @PostMapping
    public CommonResult addAddress(@RequestBody AddressBook addressBook) {
        int count = addressBookService.addAddress(addressBook);
        if (count == 1) {
            log.debug("添加地址 [" + addressBook + "] 成功");
            return CommonResult.success("添加地址成功");
        } else {
            log.debug("添加地址 [" + addressBook + "] 失败");
            return CommonResult.error("添加地址失败");
        }
    }

    @PutMapping("/default")
    public CommonResult setDefaultAddress(@RequestBody AddressBook addressBook) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = principal.getId();
        AddressBook defaultAddress = addressBookService.getDefaultAddress(id);
        if (defaultAddress != null) {
            defaultAddress.setIsDefault(false);
            int count = addressBookService.updateAddress(defaultAddress);
            if (count != 1) {
                log.debug("无法取消原先的默认地址 address: {}", defaultAddress);
                throw new BusinessException("修改默认地址失败");
            }
        }

        addressBook.setIsDefault(true);
        int count = addressBookService.updateAddress(addressBook);
        if (count == 1) {
            log.debug("设置地址 [" + addressBook + "] 为默认地址成功");
            return CommonResult.success("设置默认地址成功");
        } else {
            log.debug("设置地址 [" + addressBook + "] 为默认地址失败");
            throw new BusinessException("设置默认地址失败");
        }
    }

    @GetMapping("/default")
    public CommonResult getDefaultAddress() {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = principal.getId();
        AddressBook addressBook = addressBookService.getDefaultAddress(id);
        if (addressBook == null) {
            return CommonResult.error("目前没有默认地址");
        } else {
            return CommonResult.success(addressBook);
        }
    }

    @GetMapping("/{id}")
    public CommonResult getAddressBook(@PathVariable Long id) {
        AddressBook address = addressBookService.getAddress(id);
        return CommonResult.success(address);
    }

    @PutMapping
    public CommonResult updateAddress(@RequestBody AddressBook addressBook) {
        int count = addressBookService.updateAddress(addressBook);
        if (count != 1) {
            throw new BusinessException("更新地址失败：" + addressBook);
        }
        return CommonResult.success("更新成功");
    }
}
