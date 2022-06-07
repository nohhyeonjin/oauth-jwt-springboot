package com.noh.OAuthJWT.config;

import com.noh.OAuthJWT.jwt.JWTAuthenticationFilter;
import com.noh.OAuthJWT.jwt.JWTAuthorizationFilter;
import com.noh.OAuthJWT.oauth.PrincipalOauth2UserService;
import com.noh.OAuthJWT.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;
    private final PrincipalOauth2UserService principalOauth2UserService;
    private final MemberService memberService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.formLogin().disable()
                .httpBasic().disable();
        http.addFilter(corsFilter)
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(),memberService));
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/admin/**").access("hasRole('ADMIN')")
                .anyRequest().permitAll();
    }

}
