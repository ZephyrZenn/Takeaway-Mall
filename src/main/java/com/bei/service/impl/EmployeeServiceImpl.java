package com.bei.service.impl;

import com.bei.common.param.EmployeeParam;
import com.bei.dto.AdminUserDetail;
import com.bei.mapper.EmployeeMapper;
import com.bei.model.Employee;
import com.bei.model.EmployeeExample;
import com.bei.service.EmployeeService;
import com.bei.utils.JwtTokenUtil;
import com.bei.utils.SnowflakeIdUtils;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Import({BCryptPasswordEncoder.class})
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 根据用户名获取员工
     * @param username 用户名
     * */
    @Override
    public Employee getEmployeeByUsername(String username) {
        EmployeeExample example = new EmployeeExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<Employee> employees = employeeMapper.selectByExample(example);
        if (employees != null && employees.size() > 0) {
            return employees.get(0);
        }
        return null;
    }

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return token
     * */
    public String login(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // 校验密码
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new BadCredentialsException("帐号已被封禁");
        }
        // 将登录成功的用户交给spring security
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 返回生成的token
        return jwtTokenUtil.generateToken(userDetails);
    }

    @Override
    public int addEmployee(EmployeeParam employeeParam, Long uid) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeParam, employee);
        SnowflakeIdUtils idUtils = new SnowflakeIdUtils(uid, 1);
        employee.setId(idUtils.nextId());
        employee.setPassword(passwordEncoder.encode("123456"));
        employee.setCreateTime(new Date());
        employee.setUpdateTime(new Date());
        employee.setCreateUser(uid);
        employee.setUpdateUser(uid);
        return employeeMapper.insertSelective(employee);
    }

    @Override
    public List<Employee> getEmployeePage(int page, int pageSize, String name) {
        PageHelper.startPage(page, pageSize);
        EmployeeExample example = new EmployeeExample();
        if (StringUtils.isNotBlank(name)) {
            example.createCriteria().andUsernameEqualTo(name);
        }
        return employeeMapper.selectByExample(example);
    }

    @Override
    public int updateEmployeeStatus(Long id, Integer status) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Employee employee = new Employee();
        employee.setId(id);
        employee.setStatus(status);
        employee.setUpdateUser(principal.getId());
        employee.setUpdateTime(new Date());
        return employeeMapper.updateByPrimaryKeySelective(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateEmployee(Long id, EmployeeParam employeeParam) {
        Employee employee = new Employee();
        employee.setId(id);
        BeanUtils.copyProperties(employeeParam, employee);
        employee.setUpdateTime(new Date());
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        employee.setUpdateUser(principal.getId());
        return employeeMapper.updateByPrimaryKeySelective(employee);
    }

}
