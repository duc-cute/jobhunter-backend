package vn.hoidanit.jobhunter.util;

import com.nimbusds.jwt.JWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class SercurityUtil {
    private JwtEncoder jwtEncoder;//dùng để mã hóa + tạo jwt

    public SercurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public static  final MacAlgorithm JWT_ALGORITHM =MacAlgorithm.HS256;

    @Value("${duccute.jwt.base64-secret}")
    private String jwtKey;//khóa bí mật

    @Value("${duccute.jwt.token-validity-in-seconds}")
    private long jwtExpiration;

    public String createToken(Authentication authentication ) {
        Instant now = Instant.now();
        Instant validity =now.plus(this.jwtExpiration, ChronoUnit.SECONDS);//tính thời gian hết hạn = lấy now + thời gian expire

        JwtClaimsSet claims =JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim("duccute",authentication)
                .build();

        JwsHeader jwsHeader =JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims)).getTokenValue();

    }
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));


    }

    private static String extractPrincipal(Authentication authentication) {
        if(authentication == null) return null;
        else if(authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        }else if(authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();

        }else if(authentication.getPrincipal() instanceof  String s) {
            return s;
        }
        return null;
    }
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

}