package com.noh.OAuthJWT.controller;

import com.noh.OAuthJWT.model.Member;
import com.noh.OAuthJWT.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/join")
    public Long join(@RequestBody JoinDTO joinDto){
        String rawPassword = joinDto.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);

        Member member = memberService.join(joinDto.getUsername(), encPassword, joinDto.getEmail());

        return member.getId();
    }

    @Getter @Setter
    @AllArgsConstructor
    static class JoinDTO {
        private String username;
        private String password;
        private String email;
    }

    @GetMapping("/")
    public String home() {
        return "home!!!!";
    }

}
