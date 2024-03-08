package com.mj.mysns.config;

import com.mj.mysns.domain.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

public class CustomOAuth2AuthenticationSuccessHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws ServletException, IOException {

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("http://localhost:3000");
        Cookie token = new Cookie("TOKEN", "1234");
//        token.setHttpOnly(true);
        token.setSecure(false);
        token.setPath("/");
//        response.addCookie(token);

        response.addHeader("Authentication", "Bearer " + ((DefaultOidcUser) authentication.getPrincipal()).getIdToken().getTokenValue());

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
