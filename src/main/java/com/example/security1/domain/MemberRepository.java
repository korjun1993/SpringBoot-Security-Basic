package com.example.security1.domain;

import com.example.security1.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// CRUD 함수를 JpaRepository가 들고 있음.
// @Repository라는 어노테이션이 없어도 IOC된다. 이유는 JpaRepository를 상속했기 때문에
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByName(String name);
}
