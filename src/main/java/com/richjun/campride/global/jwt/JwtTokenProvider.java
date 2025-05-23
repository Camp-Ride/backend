package com.richjun.campride.global.jwt;


import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.auth.response.OAuth2UserResponse;
import com.richjun.campride.global.auth.service.CustomOAuth2UserService;
import com.richjun.campride.global.exception.AuthException;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.global.exception.ExceptionCode;
import com.richjun.campride.global.jwt.domain.RefreshToken;
import com.richjun.campride.global.jwt.domain.repository.RefreshTokenRepository;
import com.richjun.campride.global.jwt.dto.TokenResponse;
import com.richjun.campride.global.jwt.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;               //30분
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;     //7일
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    private final Key key;

    //The specified key byte array is 248 bits which is not secure enough for any JWT HMAC-SHA algorithm.
    // The JWT JWA Specification (RFC 7518, Section 3.2) states that keys used with HMAC-SHA algorithms MUST have a size >= 256 bits (the key size must be greater than or equal to the hash output size).
    // Consider using the io.jsonwebtoken.security.Keys#secretKeyFor(SignatureAlgorithm) method to create a key guaranteed to be secure enough for your preferred HMAC-SHA algorithm.
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //Authentication 을 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public TokenResponse generateToken(Authentication authentication) {
        return generateToken(authentication.getName(), authentication.getAuthorities());
    }

    //name, authorities 를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public TokenResponse generateToken(String name, Collection<? extends GrantedAuthority> inputAuthorities) {
        //권한 가져오기
        String authorities = inputAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();

        //Generate AccessToken
        String accessToken = Jwts.builder()
                .setSubject(name)
                .claim(AUTHORITIES_KEY, authorities)
                .claim("type", TYPE_ACCESS)
                .setIssuedAt(now)   //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))  //토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        //Generate RefreshToken
        String refreshToken = Jwts.builder()
                .claim("type", TYPE_REFRESH)
                .setIssuedAt(now)   //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME)) //토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new TokenResponse(accessToken, refreshToken);
    }


    public String generateAccessToken(String name, Collection<? extends GrantedAuthority> inputAuthorities) {
        //권한 가져오기
        String authorities = inputAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();

        //Generate AccessToken
        String accessToken = Jwts.builder()
                .setSubject(name)
                .claim(AUTHORITIES_KEY, authorities)
                .claim("type", TYPE_ACCESS)
                .setIssuedAt(now)   //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))  //토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    public String generateAccessToken(String name, String authorities) {

        Date now = new Date();

        //Generate AccessToken
        String accessToken = Jwts.builder()
                .setSubject(name)
                .claim(AUTHORITIES_KEY, authorities)
                .claim("type", TYPE_ACCESS)
                .setIssuedAt(now)   //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))  //토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    public String generateRefreshToken(String name, Collection<? extends GrantedAuthority> inputAuthorities) {
        //권한 가져오기
        String authorities = inputAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();

        //Generate AccessToken
        String refreshToken = Jwts.builder()
                .setSubject(name)
                .claim(AUTHORITIES_KEY, authorities)
                .claim("type", TYPE_REFRESH)
                .setIssuedAt(now)   //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))  //토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return refreshToken;
    }

    public String generateRefreshToken(String name, String authorities) {

        Date now = new Date();

        //Generate AccessToken
        String refreshToken = Jwts.builder()
                .setSubject(name)
                .claim(AUTHORITIES_KEY, authorities)
                .claim("type", TYPE_REFRESH)
                .setIssuedAt(now)   //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))  //토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return refreshToken;
    }


    //JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        //토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            //TODO:: Change Custom Exception
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        OAuth2UserResponse oAuth2UserResponse = new OAuth2UserResponse(getNameFromToken(accessToken),
                getNicknameFromToken(accessToken),
                getRoleFromToken(accessToken));

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2UserResponse);

        return new UsernamePasswordAuthenticationToken(customOAuth2User, "",
                customOAuth2User.getAuthorities());

    }


    //토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) throws AuthException {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw new AuthException(ExceptionCode.INVALID_ACCESS_TOKEN);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            throw new AuthException(ExceptionCode.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new AuthException(ExceptionCode.UNSUPPORTED_ACCESS_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new AuthException(ExceptionCode.EMPTY_ACCESS_TOKEN);
        } catch (Exception e) {
            log.info("Unexpected JWT Token error", e);
            throw new AuthException(ExceptionCode.UNKNOWN_ACCESS_TOKEN);
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            // ???
            return e.getClaims();
        }
    }

    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }


    public String getNameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("name", String.class));
    }

    public String getNicknameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }

    // JWT 토큰에서 특정 클레임 추출
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // JWT 토큰에서 모든 클레임 추출
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

}