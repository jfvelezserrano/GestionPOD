package com.urjc.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class JWT {

    private static String SECRET_KEY = "aWR1ZmhvaXkzODkyM3VyZmhvc2lkamZvaUZQT0lTVUZmZ3NzZGZzZGYzNEc1NkVSRkdTMzIzNGdkZ3NkZnMtLWdydHcyMzUzNDYyMzQyNWRmc2ZHREdSWTM0NTQ2U0ZTLXdldzU2MzQ=";
    private static final int EXPIRATION_TIME = 60 * 60 * 1000;
    private static SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;


    public static String createJWT(String email, List<String> roles) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key finalKey = new SecretKeySpec(secretKeyBytes, algorithm.getJcaName());

        return Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setIssuedAt(new Date(new Date().getTime()))
                .setExpiration(new Date(new Date().getTime() + EXPIRATION_TIME))
                .setSubject(email)
                .claim("authorities",
                        authorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .signWith(algorithm, finalKey)
                .compact();
    }

    public static Authentication getAuthentication(Claims claims) {
        List<String> authorities = (List) claims.get("authorities");

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        return auth;
    }

    public static Boolean isTokenExpired(String token) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public static Claims decodeJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token).getBody();
        return claims;
    }

    public static boolean isCorrect(String token){
        SecretKeySpec secretKeySpec = new SecretKeySpec(DatatypeConverter.parseBase64Binary(SECRET_KEY), algorithm.getJcaName());
        String[] chunks = token.split("\\.");
        String tokenWithoutSignature = chunks[0] + "." + chunks[1];
        String signature = chunks[2];
        DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(algorithm, secretKeySpec);

        return validator.isValid(tokenWithoutSignature, signature);
    }
}
