package com.example.security1.controller;

import com.example.security1.domain.MemberRepository;
import com.example.security1.model.Role;
import com.example.security1.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HelloController {

    private final MemberRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping
    public String indexPage() {
        return "index";
    }

    @ResponseBody
    @GetMapping("/member")
    public String member() {
        return "member";
    }

    @ResponseBody
    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }

    @ResponseBody
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("loginForm")
    public String login() {
        return "loginForm";
    }

    @GetMapping("joinForm")
    public String join() {
        return "joinForm";
    }

    // Security가 "loginProc"을 낚아채서 로그인 로직을 수행한다. (SecurityConfig.loginProcessingUrl 설정참고)
    // 사용자의 요청 메시지의 파라미터 중 memberName 값이 PrincipalDetailsServiced의 매개변수로 전달된다 (SecurityConfig.usernameParameter 설정 참고)
//    @PostMapping("loginProc")
//    public String loginProc() {
//        ...
//    }

    @PostMapping("join")
    public String joinProc(Member user) {
        log.info(user.toString());
        user.setRole(Role.USER);
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user); //회원가입 잘됨. 비밀번호 1234 => 시큐리티로 로그인할 수 없음. 이유는 패스워드가 암호화되지 않아서
        return "redirect:/loginForm";
    }

    @Secured("ADMIN")
    @ResponseBody
    @GetMapping("/info")
    public String info() {
        return "개인정보";
    }

//    @PostAuthorize()
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @ResponseBody
    @GetMapping("/data")
    public String data() {
        return "개인정보";
    }
}
