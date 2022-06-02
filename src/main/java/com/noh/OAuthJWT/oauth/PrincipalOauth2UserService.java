package com.noh.OAuthJWT.oauth;

import com.noh.OAuthJWT.auth.PrincipalDetails;
import com.noh.OAuthJWT.model.Member;
import com.noh.OAuthJWT.oauth.provider.GoogleUserInfo;
import com.noh.OAuthJWT.oauth.provider.OAuth2UserInfo;
import com.noh.OAuthJWT.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberService memberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String password = bCryptPasswordEncoder.encode(username);
        String email = oAuth2UserInfo.getEmail();

        Member member = null;
        if (!memberService.existMember(username)) {
            member = memberService.join(username, password, email, provider, providerId);
        } else {
            member = memberService.findByUserName(username);
        }

        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }
}
