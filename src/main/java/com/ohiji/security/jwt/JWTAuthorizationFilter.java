package com.ohiji.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static com.ohiji.security.jwt.JWTConstants.HEADER_STRING;
import static com.ohiji.security.jwt.JWTConstants.SECRET_KEY;
import static com.ohiji.security.jwt.JWTConstants.TOKEN_PREFIX;

// 檢查每個 request 的 JWT，決定是否給予授權(Authentication)與權限(Authority)
@Component
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    /*
        BasicAuthenticationFilter 負責以 HTTP 標頭攜帶的憑證來做基本驗證，
        它可以用來處理 Spring 遠端協定，如 Hessian 及 Burlap，
        或普通瀏覽器使用者端，如 Chrome, Firefox 及 IE 所作的驗證請求。
    */
    private final UserDetailsService userDetailsService;

    public JWTAuthorizationFilter(AuthenticationManager authManager, UserDetailsService userDetailsService) {
        super(authManager);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        var header = request.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        var token = request.getHeader(HEADER_STRING);
        String username;

        if (token.equals("Bearer null")) {return null;}
        if (token.equals("Bearer unverified")) {
            System.out.println("接收到來自 未驗證帳號 的 authentication 請求 --- " + new Date());
            return null;
        }

        try {
            username = JWT.require(Algorithm.HMAC512(SECRET_KEY.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();
        } catch (SignatureVerificationException | JWTDecodeException e) {
            System.out.println("接收到來自 異常 JWToken 的 authentication 請求 --- " + new Date());
            return null;
        } catch (TokenExpiredException e) {
            System.out.println("接收到來自 過期 JWToken 的 authentication 請求 --- " + new Date());
            return null;
        }

        if (username == null) {return null;}

        UserDetails user = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
    }
}
