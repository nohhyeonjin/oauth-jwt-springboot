package com.noh.OAuthJWT.auth;

import com.noh.OAuthJWT.model.Member;
import com.noh.OAuthJWT.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Member> findMember = memberRepository.findByUsername(username);
        if (!findMember.isEmpty()) {
            return new PrincipalDetails(findMember.get(0));
        }
        return null;
    }

}
