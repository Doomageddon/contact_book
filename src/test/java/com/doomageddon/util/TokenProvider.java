package com.doomageddon.util;

import com.doomageddon.model.entity.Role;
import com.doomageddon.model.entity.User;
import com.doomageddon.model.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static java.util.Arrays.stream;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final AuthorizationServerTokenServices tokenService;

    public RequestPostProcessor addBearerToken(String... authorities) {
        return mockRequest -> {

            UserDetailsImpl username = new UserDetailsImpl(User.builder()
                    .id(1L)
                    .name(randomUUID().toString())
                    .isEnabled(true)
                    .password(randomUUID().toString())
                    .roles(stream(authorities).map(Role::valueOf).collect(toList()))
                    .build());

            OAuth2Request oauth2Request = new OAuth2Request(
                    null, "mhp-client", null, true,
                    null, null, null, null, null);
            Authentication userOauth = new TestingAuthenticationToken(username, null, authorities);
            OAuth2Authentication oauth2auth = new OAuth2Authentication(oauth2Request, userOauth);
            OAuth2AccessToken token = tokenService.createAccessToken(oauth2auth);
            mockRequest.addHeader("Authorization", "Bearer " + token.getValue());

            return mockRequest;
        };
    }
}
