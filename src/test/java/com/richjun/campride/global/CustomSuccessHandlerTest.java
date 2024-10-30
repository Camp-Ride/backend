//package com.richjun.campride.global;
//
//import static org.hibernate.validator.internal.util.Contracts.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//import com.richjun.campride.global.jwt.domain.RefreshToken;
//import com.richjun.campride.user.domain.User;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//public class CustomSuccessHandlerTest {
//
//
//    @Test
//    @DisplayName("만료된 리프레시 토큰은 새로운 토큰으로 업데이트되어야 한다")
//    void whenTokenExpired_thenUpdateToken() {
//        // given
//        Instant expiredTime = Instant.now().minus(1, ChronoUnit.DAYS); // 만료된 시간 설정
//
//        User user = new User();
//
//        RefreshToken expiredToken = new RefreshToken();
//
//        expiredToken.setToken("5defda2e-7e55-4698-a5a5-b0125bff3ef8");
//        expiredToken.setUser(user);
//        expiredToken.setExpiryDate(expiredTime);
//
//        String currentExpiredToken = expiredToken.getToken();
//
//        // when
//        RefreshToken updatedToken = verifyExpirationAndUpdateToken(expiredToken);
//
//
//
//        // then
//        assertAll(
//                () -> assertNotEquals(currentExpiredToken, updatedToken.getToken(), "토큰 값이 업데이트되어야 합니다"),
//                () -> assertTrue(updatedToken.getExpiryDate().isAfter(Instant.now()), "새로운 만료 시간은 현재시간 이후여야 합니다"),
//                () -> assertEquals(expiredToken.getUser().getId(), updatedToken.getUser().getId(), "유저 정보는 동일해야 합니다")
//        );
//    }
//
//    private RefreshToken verifyExpirationAndUpdateToken(RefreshToken refreshToken) {
//        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
//            refreshToken.updateToken();
//        }
//        return refreshToken;
//    }
////
////    @Test
////    @DisplayName("만료되지 않은 리프레시 토큰은 업데이트되지 않아야 한다")
////    void whenTokenNotExpired_thenDoNotUpdateToken() {
////        // given
////        Instant validTime = Instant.now().plus(1, ChronoUnit.DAYS); // 유효한 시간 설정
////        String originalToken = "5defda2e-7e55-4698-a5a5-b0125bff3ef8";
////        RefreshToken validToken = RefreshToken.builder()
////                .token(originalToken)
////                .user(User.builder().id(9L).build())
////                .expiryDate(validTime)
////                .build();
////
////        // when
////        RefreshToken checkedToken = verifyExpirationAndUpdateToken(validToken);
////
////        // then
////        assertAll(
////                () -> assertEquals(originalToken, checkedToken.getToken(), "만료되지 않은 토큰은 값이 변경되지 않아야 합니다"),
////                () -> assertEquals(validTime, checkedToken.getExpiryDate(), "만료 시간이 변경되지 않아야 합니다")
////        );
////    }
//}
