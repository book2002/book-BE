package com.team2002.capstone.config.jwt;

import com.team2002.capstone.domain.Member;
import com.team2002.capstone.domain.Profile;
import com.team2002.capstone.dto.JwtTokenDTO;
import com.team2002.capstone.dto.LoginResponseDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Base64로 인코딩된 key 디코딩
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Transactional
    public LoginResponseDTO generateTokenForOAuth(Member member) {
        Optional<Profile> profile = Optional.ofNullable(member.getProfile());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()))
        );

        JwtTokenDTO jwtTokenDTO = generateToken(authentication, profile); // 기존 메서드 사용

        return LoginResponseDTO.builder()
                .grantType(jwtTokenDTO.getGrantType())
                .accessToken(jwtTokenDTO.getAccessToken())
                .refreshToken(jwtTokenDTO.getRefreshToken())
                .build();
    }

    public JwtTokenDTO generateToken(Member member, boolean isNewUser) {
        Optional<Profile> profile = Optional.ofNullable(member.getProfile());
        // Member의 권한을 Authentication 객체로 변환
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getEmail(),
                member.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()))
        );

        return generateToken(authentication, profile);
    }

    public JwtTokenDTO generateToken(Authentication authentication, Optional<Profile> optionalProfile) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date accessTokenExpiresIn = new Date(now + 86400000);

        Optional<Long> profileIdOpt = optionalProfile.map(Profile::getId); // profileId

        // AccessToken
        JwtBuilder accessTokenBuilder = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities);
        if (profileIdOpt.isPresent()) {
            accessTokenBuilder = accessTokenBuilder.claim("profileId", profileIdOpt.get());
        }
        String accessToken = accessTokenBuilder
                .setIssuedAt(issuedAt)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // RefreshToken
        JwtBuilder refreshTokenBuilder = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities);
        if (profileIdOpt.isPresent()) {
            refreshTokenBuilder = refreshTokenBuilder.claim("profileId", profileIdOpt.get());
        }
        String refreshToken = refreshTokenBuilder
                .setIssuedAt(new Date(now))
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtTokenDTO.builder()
                .grantType("bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .filter(s -> !s.isBlank())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    // accessToken
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
