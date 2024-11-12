package com.maumai.glasses.kiosk.service;

import com.maumai.glasses.kiosk.entity.User;
import com.maumai.glasses.kiosk.entity.UserDto;
import com.maumai.glasses.kiosk.repository.UserRepository;
import com.maumai.glasses.kiosk.response.Response;
import com.maumai.glasses.kiosk.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public UserService(UserRepository userRepository, RestTemplateBuilder restTemplateBuilder) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Transactional
    public ResponseEntity<Response<String>> save(Long userId, MultipartFile[] files) throws IOException {
        User user;

        // ID로 사용자 조회. 없으면 새로운 User 객체 생성
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = new User();
            user.setUserId(userId);
        }

        // 이미지 저장
        if (files != null && files.length > 0 && !files[0].isEmpty()) {
            byte[] compressedImage = ImageUtils.compressImage(files[0].getBytes());
            user.setUserImage(compressedImage);
        }
        User savedUser = userRepository.save(user); // 데이터베이스에 먼저 저장

        // Flask 서버로 전송 (트랜잭션 외부에서 수행)
        try {
            String flaskUrl = "http://127.0.0.1:5000/upload";

            // 파일을 ByteArrayResource로 변환
            ByteArrayResource byteArrayResource = new ByteArrayResource(files[0].getBytes()) {
                @Override
                public String getFilename() {
                    return files[0].getOriginalFilename(); // 파일 이름 설정
                }
            };

            // 전송 데이터 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", byteArrayResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Flask 서버로 전송 및 응답 수신
            ResponseEntity<Map> response = restTemplate.exchange(flaskUrl, HttpMethod.POST, requestEntity, Map.class);
            Map<String, Object> result = response.getBody();

            // 분석 결과를 user 엔티티에 저장
            if (result != null && result.containsKey("tone")) {
                String tone = (String) result.get("tone");
                user.setPersonalColor(tone);
                userRepository.save(user); // DB에 저장
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>("성공", "이미지 저장 및 Flask 서버 전송 성공", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("부분 성공", "Flask 서버 통신 실패. 데이터베이스에는 저장됨", null));
        }
    }

    @Transactional
    public ResponseEntity<Response<UserDto>> getUserDtoById(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    // User 엔티티를 UserDto로 변환
                    UserDto userDto = new UserDto();
                    userDto.setGlassesFrame(user.getGlassesFrame());
                    userDto.setGlassesColor(user.getGlassesColor());
                    userDto.setFaceShape(user.getFaceShape());
                    userDto.setPersonalColor(user.getPersonalColor());

                    // 성공 응답
                    Response<UserDto> response = new Response<>("성공", "유저 정보 조회 성공", userDto);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    // 유저가 존재하지 않을 때 오류 응답
                    Response<UserDto> response = new Response<>("실패", "해당 ID의 사용자가 존재하지 않습니다.", null);
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                });
    }

    @Transactional
    public ResponseEntity<Response<String>> sendFeedbackToFlask(Long userId, String feedback) {
        // 사용자 확인 및 피드백 저장
        User user = findUserById(userId);
        user.setFeedBack(feedback);
        userRepository.save(user); // DB에 피드백 업데이트

        // Flask 서버로 피드백 전송
        String flaskUrl = "http://localhost:5000/process-feedback";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", userId);
        requestBody.put("feedback", feedback);

        try {
            ResponseEntity<String> flaskResponse = restTemplate.postForEntity(flaskUrl, requestBody, String.class);

            if (flaskResponse.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response<>("성공", "피드백 저장 및 Flask 서버 전송 성공", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new Response<>("실패", "Flask 서버 전송 실패", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("실패", "Flask 서버 전송 중 예외 발생: " + e.getMessage(), null));
        }
    }

    @Transactional
    public ResponseEntity<Response<User>> findUser(Long userId) {
        User user = findUserById(userId);
        return ResponseEntity.ok(new Response<>("true", "유저 조회 성공", user));
    }

    @Transactional
    public List<byte[]> send(Long userId) {
        User user = findUserById(userId);
        List<byte[]> compressedImages = new ArrayList<>();

        if (user.getUserImage() != null) {
            byte[] compressedImage = ImageUtils.decompressImage(user.getUserImage());
            compressedImages.add(compressedImage);
        }
        return compressedImages;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));
    }
}