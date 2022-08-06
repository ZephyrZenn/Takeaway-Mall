package com.bei.component;

import com.bei.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt过滤器
 * */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Value("${jwt.header}")
    private String header;
    @Value("${jwt.head}")
    private String head;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("URI:{}", request.getRequestURI());
        String tokenWithHead = request.getHeader(header);
        if (StringUtils.isNotBlank(tokenWithHead) && tokenWithHead.startsWith(head)) {
            String token = tokenWithHead.substring(head.length());
            String username = jwtTokenUtil.getUserNameFromToken(token);
            log.info("start authentication");
            log.info("authorize {}", username);
            log.info("token: {}", token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(token, userDetails) && userDetails.isAccountNonLocked()) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.info("{} has been authenticated", username);
                }
            }
            log.info("authentication finish");
        }
        filterChain.doFilter(request, response);
    }
}
