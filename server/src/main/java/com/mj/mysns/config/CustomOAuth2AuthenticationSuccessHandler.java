package com.mj.mysns.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class CustomOAuth2AuthenticationSuccessHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws ServletException, IOException {

        String jwt = ((DefaultOidcUser) authentication.getPrincipal()).getIdToken()
            .getTokenValue();

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("http://localhost:3000");
        Cookie token = new Cookie("tkn", jwt);
        token.setHttpOnly(true);
        token.setSecure(false);
        token.setPath("/");
        response.addCookie(token);

        response.addHeader("Authentication", "Bearer " + jwt);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
