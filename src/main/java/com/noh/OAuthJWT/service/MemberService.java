package com.noh.OAuthJWT.service;

import com.noh.OAuthJWT.model.Member;
import com.noh.OAuthJWT.model.Role;
import com.noh.OAuthJWT.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public Member join(String username, String password, String email) {
        Member member = Member.builder()
                .username(username)
                .password(password)
                .email(email)
                .role(Role.DEFAULT)
                .build();

        memberRepository.save(member);

        return member;
    }

    public Member join(String username, String password, String email, String provider, String providerId) {
        Member member = Member.builder()
                .username(username)
                .password(password)
                .email(email)
                .role(Role.DEFAULT)
                .provider(provider)
                .providerId(providerId)
                .build();

        memberRepository.save(member);

        return member;
    }

    public boolean existMember(String username) {
        List<Member> findUser = memberRepository.findByUsername(username);

        if (findUser.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public Member findByUserName(String username) {
        List<Member> findUser = memberRepository.findByUsername(username);

        if (!findUser.isEmpty()) {
            return findUser.get(0);
        } else {
            return null;
        }
    }
}
