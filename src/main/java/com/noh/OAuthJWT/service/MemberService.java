package com.noh.OAuthJWT.service;

import com.noh.OAuthJWT.model.Member;
import com.noh.OAuthJWT.model.Role;
import com.noh.OAuthJWT.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public Member join(String username, String password, String email) {
        Member member = new Member();
        member.setUsername(username);
        member.setPassword(password);
        member.setEmail(email);
        member.setRole(Role.DEFAULT);

        memberRepository.save(member);

        return member;
    }
}
