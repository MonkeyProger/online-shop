package org.scenter.onlineshop.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;


@Component
public class JWTutil{
    @Value("${jwt_secret}")
    private String secret;
    public Map<String,String> generateTokens(User user, String requestUrl) {
        Date expDateAccess = Date.from(ZonedDateTime.now().plusMinutes(10).toInstant());
        Date expDateRefresh = Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        String accessToken = JWT.create().withSubject(user.getUsername())
                .withExpiresAt(expDateAccess)
                .withIssuer(requestUrl)
                .withClaim("roles",user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())).sign(algorithm);
        String refreshToken = JWT.create().withSubject(user.getUsername())
                .withExpiresAt(expDateRefresh)
                .withIssuer(requestUrl)
                .sign(algorithm);
        Map<String,String> tokens = new HashMap<>();
        tokens.put("access_token",accessToken);
        tokens.put("refresh_token",refreshToken);
        return tokens;
    }

    public UsernamePasswordAuthenticationToken validateToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        return new UsernamePasswordAuthenticationToken(username,null,authorities);
    }
}
