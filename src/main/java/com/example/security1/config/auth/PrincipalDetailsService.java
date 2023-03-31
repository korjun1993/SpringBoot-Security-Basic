package com.example.security1.config.auth;

import com.example.security1.domain.MemberRepository;
import com.example.security1.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 시큐리티 설정에서 loginProcessingUrl("/login");
 * "/login" 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어 있는 loadUserByUsername 함수가 실행
 */
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository userRepository;

    // 시큐리티 session(내부 Authentication(내부 UserDetails))
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = userRepository.findByName(username).orElseThrow();
        return new PrincipalDetails(member);
    }
}
