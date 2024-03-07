package com.intentionman.vkselectiontask.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.util.Collection;
import java.util.Collections;

import static io.jsonwebtoken.io.Decoders.BASE64;

@Component
public class JwtDecoder {
    private final SecretKey jwtAccessSecret;

    public JwtDecoder(@Value("${jwt.secret.access}") String jwtAccessSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(BASE64.decode(jwtAccessSecret));
    }

    public Claims decodeToken(String auth) {
        try {
            return Jwts.parserBuilder().setSigningKey(jwtAccessSecret).build().parseClaimsJws(auth).getBody();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }
}
