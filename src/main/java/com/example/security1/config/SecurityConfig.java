package com.example.security1.config;

import com.example.security1.config.jwt.JwtAuthFilter;
import com.example.security1.config.jwt.JwtAuthorizationFilter;
import com.example.security1.domain.MemberRepository;
import com.example.security1.filter.MyFilter1;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됩니다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
// secured 어노테이션 활성화, preAuthorize+postAuthorize 어노테이션 활성화
public class SecurityConfig {

    private final CorsFilter corsFilter;

    private final MemberRepository memberRepository;

    // BCryptPasswordEncoder를 IOC해준다.
    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////        http.csrf().disable();
////        http.authorizeHttpRequests()
////                .requestMatchers("/member/**").authenticated() // 인증이 필요한 경로
////                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN") // MANAGER, ADMIN 권한이 필요한 경로
////                .requestMatchers("/admin/**").hasRole("ADMIN") // MANAGER 권한이 필요한 경로
////                .anyRequest().permitAll() // 그외 모두 허용
////                .and()
////                .formLogin()// 인증방식은 formLogin으로 설정
////                .loginPage("/loginForm") // 로그인 페이지의 경로를 "loginForm"으로 설정
////                .usernameParameter("memberName") // UserDetailsService loadUserByUsername의 매개변수로 전달될 변수의 이름
////                .loginProcessingUrl("/loginProc") // "/login" 경로가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행
////                .defaultSuccessUrl("/"); // 로그인이 성공했을 경우 이동할 경로를 설정
////        return http.build();
        AuthenticationManager authManager = http.getSharedObject(AuthenticationManager.class);
        http.addFilterBefore(new MyFilter1(), BasicAuthenticationFilter.class); // BasicAuthenticationFilter 전에 내 필터를 등록하겠어.
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용X
                .and()
                .addFilter(corsFilter)
                .addFilter(new JwtAuthFilter(authManager)) // formLogin().disable() 때문에 동작안하게 됌. 필터로 추가하면 동작하게 됌
                .addFilter(new JwtAuthorizationFilter(authManager, memberRepository))
                .formLogin().disable() // 기본으로 동작하는 UsernamePasswordAuthenticationFilter 동작하지 않게하기 (https://velog.io/@seongwon97/Spring-Security-Form-Login 참조)
                .httpBasic().disable() // Authorization 필드에 ID,PW 담는 인증 방식을 허용하지 않겠다. 이곳에 JWT Token을 넣을 계획
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/user/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                .requestMatchers("/api/v1/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll();
        return http.build();
    }
}
