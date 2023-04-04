# 세션
![img.png](img/session0.png)
출처: 스프링 MVC2 2편 - 인프런, 김영한

- 로그인을 세션으로 구현할 경우, 인증 과정에 세션 저장소를 조회하는 로직이 포함된다.
- 일반적으로 세션 저장소는 **메모리(RAM)**의 형태이다.
- 동시접속자가 많다면 세션 저장소에 트래픽이 집중될 수 있다.
- 트래픽을 분산시키기 위해 세션 저장소를 확장(scale-out)한다.

## 세션 불일치 해결방법 
- Sticky Session
- Session Clustering
- Session Storage 분리

<br/>

**Sticky Session**
![img.png](img/session1.png)
- 사용자가 처음 접속된 서버로 계속 접근하도록 트래픽을 처리하는 방식
- 특정 서버에 트래픽이 집중될 위험이 있다.

<br/>

**Session Clustering**
![img.png](img/session2.png)
- 세션 저장 정보가 변경될때마다 여러 서버의 세션 저장소를 동기화하는 방식
- 모든 서버의 세션 저장 정보를 동기화 해야하므로 서버 수에 비례하여 네트워크 트래픽이 증가한다.

<br/>

**Session Storage 분리**
![img.png](img/session3.png)
- Sticky Session의 문제점인 특정 서버에 트래픽이 집중되는 문제를 해결
- Session Clustering의 문제점인 동기화 작업으로 인한 트래픽 증가 현상이 발생하지 않음
- 외부 세션 저장소 서버를 일반적인 Disk-Based DB로 선택할 경우, 세션 정보를 조회하는 로직이 I/O 기반으로 동작하므로 성능이 떨어진다.
- 세션 정보는 영속성을 저장할 필요가 없기 때문에 Disk-Based DB를 선택할 필요가 없다.
- 따라서 외부 세션 저장소는 In-Memory DB를 선택한다.
- 일반적으로 Key-Value DB인 Redis, Memcached를 사용한다.

---

# JWT
- JWT (JSON Web Token)는 당사자간에 정보를 JSON 객체로 안전하게 전송하기 위한 표준 ([RFC 7519](https://www.rfc-editor.org/rfc/rfc7519))
- 정보는 공개 / 개인키 쌍을 사용하여 디지털 서명을 하기 때문에 신뢰할 수 있습니다. 

## 구조
![img.png](img/jwt1.png)
- Header
- Payload(데이터)
- Signature


<br/>

### Header
![img.png](img/jwt0.png)
- `alg`: 서명 알고리즘
- `typ`: 토큰 타입
- 헤더 정보는 **Base64Url** 방식으로 인코딩되어 JWT의 첫 번째 구성값이 된다.
- Base64란 8비트 이진 데이터를 ASCII 문자로 바꾸는 인코딩 방식이다.

### Payload
![img.png](img/jwt2.png)
- 클레임(전달하고싶은 정보)를 포함하는 데이터
- 세 가지 타입의 클레임이 있다.
  - Registered claims:
    - 사용하길 권장하는 미리 정의된 클레임 집합.
    - iss(발행자)
    - exp(만료시간)
    - sub(주제)
    - aud(청중)
    - [기타](https://www.iana.org/assignments/jwt/jwt.xhtml)
  - Public claims
    - JWT를 사용하는 사람들이 원하는대로 정의
    - 충돌을 피하기 위해서 [링크](https://www.iana.org/assignments/jwt/jwt.xhtml)에 정의된 것을 사용
    - 또는 충볼 방지 네임스페이스를 포함하여 정의
  - private claims
    - 당사자간에 정보를 공유하기 위한 클레임

### Signature
![img.png](img/jwt3.png)
- 서명을 통해 무결성을 검증

## JWT 토큰을 이용한 인증 구현
![img.png](img/app0.png)

1. **로그인 요청**
- 사용자가 서버에게 아이디, 비밀번호를 전달하며 로그인 요청을 한다.
2. **검증 및 JWT 토큰 생성**
- 서버는 사용자로부터 전달받은 아이디, 비밀번호를 바탕으로 인증 절차를 수행한다.
- 유효한 사용자라면 서버는 JWT 토큰을 생성한다.
3. **JWT 토큰 전달**
- 사용자에게 JWT 토큰을 전달한다.
- JWT 토큰은 사용자 웹 브라우저의 웹 스토리지 영역에 저장된다.
4. **API 호출**
- 사용자는 쿠키에 JWT 토큰을 담아 서버의 API를 호출한다.
- 서버는 사용자로부터 전달받은 JWT 토큰이 유효한지 검증한다.
- 만일 유효한 토큰이라면 API를 호출하고 사용자에게 응답한다.


## 세션-인증방식의 문제를 해결
- JWT 토큰을 이용한 인증은 세션의 문제를 해결할 수 있다.
- 세션을 이용한 인증 방식의 문제는 서버 확장에 한계가 있다는 점이다. 서버간 세션 정보의 불일치가 발생할 수 있기 때문이다.
- JWT 토큰을 이용한 인증 방식은 세션 저장소와 같은 개념이 필요없기 때문에 서버를 무한히 확장할 수 있다.

---

# Spring Security  

스프링 시큐리티 공식문서는 스프링 시큐리티를 아래와 같이 정의한다.
> 스프링 시큐리티는 인증, 인가, 공격 방어책을 제공하는 프레임워크이다.

## 용어 정리
### 인증
**인증(Authentication)**은 **주체(Principal)**의 신원(Identity)를 증명하는 과정입니다.
주체는 보통 유저(사용자)를 가르키며 주체는 자신을 인증해달라고 신원 증명 정보, 즉 Credential을 제시합니다.

**인가(Authorization)는 인증을 마친 유저에게 권한(Authority)를 부여하여 애플리케이션의 특정 리소스에 접근할 수 있게 허가하는 과정입니다.
인가는 반드시 인증 과정 이후에 수행되야합니다.

**접근통제(Access Control)는 애플리케이션 리소스에 접근하는 행위를 제어하는 일입니다.
따라서 어떤 유저가 어떤 리소스에 접근하도록 허락할지를 결정하는 행위, 즉 접근 통제 결정이 뒤따릅니다.
리소스의 접근 속성과 유저에게 부여된 권한 또는 다른 속성들을 바탕으로 결정합니다.
출처: https://velog.io/@shinmj1207/Spring-Spring-Security-JWT-로그인

## Architectures

![img.png](img/spring-architecture.png)
출처: https://gowoonsori.com/spring/architecture/

- 위 그림은 Tomcat(Servlet-Container), Spring-Container 의 구조를 나타낸 것입니다.
- Servlet-Container의 입장에서 Spring-Container의 존재를 모릅니다.
- 따라서 내부에 어떤 bean이 있는지도 알 수 없습니다.


### DelegatingFilterProxy
![img.png](img/delegatingfilterproxy.png)
- Spring은 `DelegatingFilterProxy`라는 이름의 필터를 제공합니다.
- Servlet-Container에 존재하며, Spring-Container에 존재하는 FilterChainProxy를 호출합니다.


### FilterChainProxy
![img.png](img/securityfilterchain.png)
- 스프링 시큐리티가 제공하는 특별한 필터입니다.
- Spring-Container에 존재하며, DelegatingFilterProxy로부터 요청을 위임받습니다.
- `SecurityFilterChain`에 등록되어있는 인스턴스를 사용하여 보안 업무를 수행합니다.
- `FilterChainProxy`는 스프링 시큐리티의 출발점이므로 문제가 발생했을 경우, 이곳을 디버그 포인트로 추가하면 좋습니다.


![img.png](img/multiple_securityFilterChain.jpeg)
- `FilterChainProxy`는 사용해야할 SecurityFilterChain을 결정할 수 있습니다.


### SecurityFilterChain
- FilterProxy가 현재 요청에 대해 어떤 `Security Filter`를 호출할지 결정할 때, `SecurityFilterChain`을 사용합니다.
- 개인적으로 `Security Filter`의 집합이라고 생각합니다.


### SecurityFilter
- Security Filter는 SecurityFilterChain API를 이용하여 FilterChainProxy 내에 삽입됩니다.
- 필터의 순서를 고려하는 것이 중요합니다.
- 다음 그림은 일반적으로 SecurityFilterChain에 등록된 SecurityFilter들의 모습입니다.
![img.png](img/securityfilterchain1.png)
출처: http://atin.tistory.com/590


### Security Exception 처리
![img.png](img/exceptionTranslationFilter.jpeg)
- ① 애플리케이션의 나머지 로직을 수행하기 위해 `FilterChain.doFilter(request, response)`를 호출합니다.
- ② 만약, 인증된 사용자가 아니거나 `AuthenticationException`이 발생했다면 인증과정을 수행합니다.
  - `SecurityContextHolder.clear`가 수행됩니다.
  - HttpServletRequest는 RequestCache에 저장되어, 인증을 성공할 경우 원래 요청을 재수행합니다.
- ③ 이 외에, AccessDeniedException이 발생할 경우, 접근 거부됩니다. `AccessDeniedHandler`가 호출되어 접근 거부 처리합니다.

> Note
> 
> 만약 애플리케이션이 `AccessDeniedException` 또는 `AuthenticationException`을 throw하지 않는다면, `ExceptionTranslationFilter`는 아무것도 하지 않습니다.


# Reference
- [시큐리티 특강, Youtube 메타코딩 채널](https://www.youtube.com/@metacoding)
- [JWT Document](https://jwt.io/introduction)
- [Spring Security Document](https://docs.spring.io/spring-security)
- https://velog.io/@shinmj1207/Spring-Spring-Security-JWT-로그인
- https://gowoonsori.com/spring/architecture/
- http://atin.tistory.com/590
