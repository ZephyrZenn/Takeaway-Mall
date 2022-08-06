package com.bei.config;

import com.bei.component.JwtAuthenticationTokenFilter;
import com.bei.dto.AdminUserDetail;
import com.bei.model.Employee;
import com.bei.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** spring security配置类 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用csrf和session
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 允许网页内嵌显示
        http.headers().frameOptions().sameOrigin();
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, // 允许静态页面请求
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/front/**",
                        "/backend/**",
                        "/swagger-resources/**",
                        "/v2/api-docs/**",
                        "/webjars/springfox-swagger-ui/**"
                )
                .permitAll()
                .antMatchers("/employee/login", "/employee/logout")
                .permitAll()
                .anyRequest()
                .authenticated();
//                .permitAll();
        http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint);
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Employee employee = employeeService.getEmployeeByUsername(username);
            if (username == null) {
                throw new UsernameNotFoundException("该用户不存在");
            }
            return new AdminUserDetail(employee);
        };
    }
}
