//package com.richjun.campride.room.service;
//
//import com.richjun.campride.global.auth.domain.CustomOAuth2User;
//import com.richjun.campride.global.auth.response.OAuth2UserResponse;
//import com.richjun.campride.global.location.domain.Location;
//import com.richjun.campride.room.domain.Room;
//import com.richjun.campride.room.domain.repository.RoomRepository;
//import com.richjun.campride.room.domain.type.RoomType;
//import com.richjun.campride.room.request.RoomRequest;
//import com.richjun.campride.user.domain.User;
//import com.richjun.campride.user.domain.repository.UserRepository;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class RoomServiceTest {
//
//    @Autowired
//    private RoomService roomService;
//
//    @Autowired
//    private RoomRepository roomRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private Room testRoom;
//    private static final int MAX_PARTICIPANTS = 5;
//    private static final int CONCURRENT_USERS = 10;
//    List<Long> testUserIds = new ArrayList<>();
//
//    @BeforeEach
//    void setUp() {
//        // 테스트용 방 생성
//        RoomRequest roomRequest = new RoomRequest("Test Room", "Test Departure", "Test Destination",
//                LocalDateTime.now(), 0, MAX_PARTICIPANTS,
//                RoomType.ONE);
//        testRoom = Room.of(roomRequest, 1L, "Leader", new Location(123.0, 123.0), new Location(123.0, 123.0));
//
//        long roomId = roomRepository.save(testRoom).getId();
//
//        System.out.println("roomId : " + roomId);
//
//        // 테스트용 사용자들 생성
//        for (int i = 0; i < CONCURRENT_USERS; i++) {
//            User user = new User("User" + (i + 1), "User" + (i + 1), "ROLE_USER", "deviceToken");
//            testUserIds.add(userRepository.save(user).getId());
//            System.out.print("User" + (i + 1) + " is created\n");
//            userRepository.save(user);
//        }
//    }
//
//    @AfterEach
//    void tearDown() {
//        roomRepository.deleteById(testRoom.getId());
//        userRepository.deleteAllById(testUserIds);
//    }
//
//    @Test
//    void testConcurrentRoomJoining() throws InterruptedException {
//        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
//        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS);
//        AtomicInteger successfulJoins = new AtomicInteger(0);
//        AtomicInteger failedJoins = new AtomicInteger(0);
//        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
//
//        for (int i = 0; i < CONCURRENT_USERS; i++) {
//            final int userId = i + 1;
//            executorService.submit(() -> {
//                try {
//                    System.out.print("User" + userId + " is trying to join the room\n");
//                    System.out.println("testRoom.getId() : " + testRoom.getId());
//                    CustomOAuth2User oAuth2User = new CustomOAuth2User(
//                            new OAuth2UserResponse("User" + userId, "User" + userId, "ROLE_USER"));
//                    roomService.joinRoom(testRoom.getId(), oAuth2User);
//                    successfulJoins.incrementAndGet();
//                } catch (Exception e) {
//                    failedJoins.incrementAndGet();
//                    exceptions.add(e);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await(30, TimeUnit.SECONDS); // 최대 30초 대기
//        executorService.shutdown();
//        executorService.awaitTermination(1, TimeUnit.MINUTES);
//
//        // 검증
//        assertEquals(MAX_PARTICIPANTS, successfulJoins.get(), "성공적으로 참가한 사용자 수가 최대 참가자 수와 일치해야 합니다.");
//        assertEquals(CONCURRENT_USERS - MAX_PARTICIPANTS, failedJoins.get(), "실패한 참가 시도 수가 예상과 일치해야 합니다.");
//
//        // 수집된 예외 정보 출력
//        for (Exception e : exceptions) {
//            System.out.println("Caught exception: " + e.getClass().getName() + " - " + e.getMessage());
//        }
//    }
//}