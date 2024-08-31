package vn.hoidanit.jobhunter.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
}
