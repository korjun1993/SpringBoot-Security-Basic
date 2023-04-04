package com.example.security1.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.security1.config.auth.PrincipalDetails;
import com.example.security1.model.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가 있음.
// login 요청해서 username, password 전송하면 (post)
// UsernamePasswordAuthenticationFilter 가 동작함.
@RequiredArgsConstructor
public class JwtAuthFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authManager;

    // login 요청을 하면 인증 과정을 처리하는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            Member member = getLoginInfo(request);
            UsernamePasswordAuthenticationToken preAuthToken = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());

            // PrincipalDetailsService의 loadUserByUsername() 함수가 실행됨
            Authentication postAuthToken = authManager.authenticate(preAuthToken);
            return postAuthToken;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // attempAuthentication 실행 후 인증이 정상적으로 되었을 때 실행
    // JWT 토큰을 만들어서 request 요청한 사용자에게 응답
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principal = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", principal.getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        response.addHeader("Authorization", "Bearer " + jwtToken);
    }

    private Member getLoginInfo(HttpServletRequest request) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(request.getInputStream(), Member.class);
    }
}
