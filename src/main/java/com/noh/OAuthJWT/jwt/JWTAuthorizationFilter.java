package com.noh.OAuthJWT.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.noh.OAuthJWT.auth.PrincipalDetails;
import com.noh.OAuthJWT.model.Member;
import com.noh.OAuthJWT.service.MemberService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private MemberService memberService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, MemberService memberService) {
        super(authenticationManager);
        this.memberService = memberService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwtHeader = request.getHeader(JwtProperties.HEADER);
        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request,response);
            return;
        }

        String jwtToken = request.getHeader(JwtProperties.HEADER).replace(JwtProperties.TOKEN_PREFIX, "");
        System.out.println("jwtToken = " + jwtToken);

        String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                .build()
                .verify(jwtToken)
                .getClaim("username")
                .asString();
        System.out.println("username = " + username);

        if (username != null) {
            Member member = memberService.findByUserName(username);
            PrincipalDetails principalDetails = new PrincipalDetails(member);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            // 권한을 위한 작업
            // 시큐리티 세션에 접근하여 Authentication 객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request,response);
        }
    }
}
