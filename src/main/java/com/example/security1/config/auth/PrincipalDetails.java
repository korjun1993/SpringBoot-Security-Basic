package com.example.security1.config.auth;

import com.example.security1.model.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Security가 "/login" 주소 요청이 오면 낚아채서 로그인을 진행한다.
 * 로그인 진행이 완료되면 session(SecurityContext)을 만들어줍니다.
 * SecurityContext은 SecurityContextHolder에서 관리합니다.
 * SecurityContext은 Authentication객체를 저장합니다.
 * Authentication은 Pricipal (인증된 사용자정보)를 저장합니다.
 */

public class PrincipalDetails implements UserDetails {

    private Member member;

    public PrincipalDetails(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return member.getRole().name();
            }
        });
        return null;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
