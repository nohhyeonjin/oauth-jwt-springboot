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

쿠키의 경우, 값을 그대로 보내기 때문에 보안에 취약하다는 단점이 있고 이러한 보안 위험을 session을 이용하여 해결한다

#### Session
쿠키와 다르게 서버에 인증정보를 저장한다

서버는 클라이언트가 로그인 요청 시 인증 관련 정보는 서버에 저장하고, 세션ID를 발급하여 쿠키에 담아 응답 ->
이후 클라이언트는 요청 시 세션ID를 전달 ->
서버는 세션ID의 유효성을 판단하여 클라이언트를 식별

세션의 경우, 세션ID를 탈취하게되면 클라이언트인척 위장이 가능하며, 서버에 세션 저장소를 사용하므로 요청이 많아지면 서버 부하가 심해진다는 단점이 있다.
또한 scale out으로 서버가 여러대일 경우 서버 간 세션 정보 공유가 필요하다. 서버마다 세션 복제를 한다던지, 한 DB에 세션 정보를 넣어두고 모든 서버가 DB에 접근한다던지.. 번거롭다

## JWT
인증 관련 정보를 암호화시킨 토큰으로 서버에서는 토큰을 검증만 하면 되기 때문에, 서버가 여러대인 경우에서도 secret 키 값만 공유하면 된다

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

