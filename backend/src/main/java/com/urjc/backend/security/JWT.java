package com.urjc.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Component
public class JWT {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Value("#{T(java.lang.Integer).parseInt('${jwt.expiration.time}')}")
    private int EXPIRATION_TIME;

    @Value("#{T(io.jsonwebtoken.SignatureAlgorithm).forName('${jwt.algorithm}')}")
    private SignatureAlgorithm ALGORITHM;

    public String createJWT(String email) {
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key finalKey = new SecretKeySpec(secretKeyBytes, ALGORITHM.getJcaName());

        return Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setIssuedAt(new Date(new Date().getTime()))
                .setExpiration(new Date(new Date().getTime() + EXPIRATION_TIME))
                .setSubject(email)
                .signWith(ALGORITHM, finalKey)
                .compact();
    }

    public Boolean isTokenExpired(String token) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public Claims decodeJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token).getBody();
        return claims;
    }

    public boolean isSignatureVerified(String token){
        SecretKeySpec secretKeySpec = new SecretKeySpec(DatatypeConverter.parseBase64Binary(SECRET_KEY), ALGORITHM.getJcaName());
        String[] chunks = token.split("\\.");
        String tokenWithoutSignature = chunks[0] + "." + chunks[1];
        String signature = chunks[2];
        DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(ALGORITHM, secretKeySpec);

        return validator.isValid(tokenWithoutSignature, signature);
    }
}
