package com.gjgs.gjgs.infra.config.jwt;

import com.gjgs.gjgs.modules.member.repository.interfaces.LogoutAccessTokenRedisRepository;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberRepository;
import com.gjgs.gjgs.modules.utils.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 직접 만든 JwtTokenUtil 와 JwtFilter 를 SecurityConfig 에 적용할 때 사용
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final JwtTokenUtil jwtTokenUtil;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    private final MemberRepository memberRepository;

    // JwtFilter를 Security에 등록
    @Override
    public void configure(HttpSecurity http) {
        JwtFilter jwtFilter = new JwtFilter(jwtTokenUtil, logoutAccessTokenRedisRepository, memberRepository);
        ExceptionHandlerFilter exceptionHandlerFilter = new ExceptionHandlerFilter();

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionHandlerFilter, JwtFilter.class);
    }
}
