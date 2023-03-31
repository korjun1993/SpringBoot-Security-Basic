package com.example.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됩니다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화, preAuthorize+postAuthorize 어노테이션 활성화
public class SecurityConfig {

    // BCryptPasswordEncoder를 IOC해준다.
    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeHttpRequests()
                .requestMatchers("/member/**").authenticated() // 인증이 필요한 경로
                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN") // MANAGER, ADMIN 권한이 필요한 경로
                .requestMatchers("/admin/**").hasRole("ADMIN") // MANAGER 권한이 필요한 경로
                .anyRequest().permitAll() // 그외 모두 허용
                .and()
                .formLogin()// 인증방식은 formLogin으로 설정
                .loginPage("/loginForm") // 로그인 페이지의 경로를 "loginForm"으로 설정
                .usernameParameter("memberName") // UserDetailsService loadUserByUsername의 매개변수로 전달될 변수의 이름
                .loginProcessingUrl("/loginProc") // "/login" 경로가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행
                .defaultSuccessUrl("/"); // 로그인이 성공했을 경우 이동할 경로를 설정
        return http.build();
    }
}
