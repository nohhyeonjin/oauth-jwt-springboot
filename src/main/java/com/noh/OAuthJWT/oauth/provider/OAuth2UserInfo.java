package com.noh.OAuthJWT.oauth.provider;

public interface OAuth2UserInfo {

    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();

}
