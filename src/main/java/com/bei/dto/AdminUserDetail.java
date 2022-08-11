package com.bei.dto;

import com.bei.model.Employee;
import com.bei.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * spring security需要的用户类
 * */
public class AdminUserDetail implements UserDetails {

    private Employee employee;

    private User user;

    public AdminUserDetail(Employee employee) {
        this.employee = employee;
    }

    public AdminUserDetail(User user) {
        this.user = user;
    }

    public Long getId() {
        if (employee != null) {
            return employee.getId();
        }
        if (user != null) {
            return user.getId();
        }
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        if (employee != null && employee.getUsername().equals("admin")) {
            authorityList.add(new SimpleGrantedAuthority("admin"));
        }
        return authorityList;
    }

    @Override
    public String getPassword() {
        if (employee != null) {
            return employee.getPassword();
        }
        return "";
    }

    @Override
    public String getUsername() {
        if (employee != null) {
            return employee.getUsername();
        }
        if (user != null) {
            return user.getEmail();
        }
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (employee != null)
            return employee.getStatus() == 1;
        else
            return user.getStatus() == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (employee != null) {
            return employee.getStatus() == 1;
        }
        return user.getStatus() == 1;
    }
}
