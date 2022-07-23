# OAuth-JWT-SpringBoot
Spring Security, OAuth, Cookie/Session/JWT 학습을 위해 진행

branch 나눠 OAuth와 JWT 인증방식 구현

## Cookie & Session
HTTP 프로토콜의 비연결성, 무상태성 특성으로 인하여 서버가 클라이언트를 매번 식별해야하는 단점을 보완하기 위해 사용

#### Cookie
클라이언트의 브라우저에 저장되는 파일로,

서버는 클라이언트가 로그인 요청 시 인증 관련 정보를 쿠키에 담아 응답 ->
이후 클라이언트는 요청 시 쿠키를 헤더에 담아 요청 ->
서버는 쿠키로 클라이언트를 식별

쿠키로만 인증을 진행할 경우, 사용자 인증에 대한 정보를 클라이언트 쪽에서 가지고 있기 때문에 쿠키 탈취 시 사용자 인증에 대한 정보를 빼앗길 수 있다. 그렇기 때문에 쿠키는 주로 보안과 상관없는 장바구니 등에 사용된다.

#### Session
쿠키와 다르게 서버에 인증정보를 저장한다

서버는 클라이언트가 로그인 요청 시 세션ID를 발급하여 세션 저장소에 저장 후 쿠키에 담아 응답 ->
이후 클라이언트는 요청 시 세션ID를 전달 ->
서버는 세션ID의 유효성을 판단하여 클라이언트를 식별

세션의 경우, 세션ID를 탈취하게되면 클라이언트인척 위장이 가능하며, 서버에 세션 저장소를 사용하므로 요청이 많아지면 서버 부하가 심해진다는 단점이 있다.
또한 scale out으로 서버가 여러대일 경우 서버 간 세션 정보 공유가 필요하다.

<p align="center">
  <img width="750" alt="image" src="https://user-images.githubusercontent.com/47866105/180596541-d02c3c8a-de8d-4a6a-80d0-9892eccef89f.png">
</p>


## JWT
JWT는 인증 관련 정보를 암호화시킨 토큰으로 인증 정보를 위한 별도의 저장소가 필요하지 않으며, 서버에서는 토큰을 검증만 하면 되기 때문에 서버가 여러대인 경우에서도 secret 키 값만 공유하면 된다.

서버는 클라이언트가 로그인 요청 시 payload에 유효기간, 인증정보 등을 담고 secretkey를 이용하여 토큰 발급 ->
이후 클라이언트는 요청 시 토큰을 헤더에 담아 요청 ->
서버는 토큰을 secretkey로 검증


``` Java
String jwtToken = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))
                .withClaim("id",principalDetails.getMember().getId())
                .withClaim("username",principalDetails.getMember().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

// 해당 코드는 JWT branch의 src/main/java/com/noh/OAuthJWT/jwt/JWTAuthenticationFilter.java 에서 확인 가능
```

JWT의 경우에도 단점이 존재하는데, 세션의 경우 탈취 시 세션 삭제하여 대처가능하지만 JWT토큰은 한번 발급되면 유효기간 만료될 때 까지 계속해서 사용가능하니 대처하기가 어렵다.

#### JWT 보안 대처 방법
- 짧은 만료 시간 설정
 
  탈취되더라도 만료 시간이 짧기 때문에 피해를 최소화할 수 있다. 그러나 사용자의 잦은 로그인이 요구된다
- sliding session
  
  유저가 서비스를 사용중이라면 만료되기 전 만료기한을 연장시켜주는 방법으로, 글 작성을 시작한다던지 결제 시작 시 만료기간을 연장해줄 수 있다
- refresh token 사용
  
  서버는 access token과 refresh token을 발급하여 access token이 만료된 상황에서 refresh token이 유효할 경우 새로운 access token을 발급, 유효하지 않을 경우 로그인을 요청한다.
  
  이 방법은 refrech token 유효 여부 판단을 위해 추가적인 I/O 작업이 필요하다는 단점이 있으나, 탈취 시 서버에서 관리하는 refresh token을 강제 만료시키며 피해를 막을 수 있다
- sliding session + refresh token
  
  sliding session 방법이 access token의 유효기간을 늘려주었다면, sliding session + refresh token 방법의 경우 refresh token의 유효기간을 늘려주어 일반 sliding session 방식처럼 빈번하게 기간 연장을 해줄 필요가 없다

<br>

### 정리
| |장점|단점|
|-|------|---|
|cookie & session|서버쪽에서 session 통제 가능, 네트워크 부하 낮음|세션 저장소 사용으로 인한 서버 부하|
|jwt|서버에 인증 정보를 저장하지 않음|특정 토큰을 강제로 만료시키기 어려움, 긴 토큰 길이로 인한 네트워크 부하|
