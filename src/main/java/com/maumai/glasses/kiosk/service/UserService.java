package com.maumai.glasses.kiosk.service;

import com.maumai.glasses.kiosk.entity.User;
import com.maumai.glasses.kiosk.repository.UserRepository;
import com.maumai.glasses.kiosk.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User save(User user, MultipartFile[] files) throws IOException {
        if(files[0] != null){
            byte[] compressedImage = ImageUtils.compressImage(files[0].getBytes());
            user.setUserImage(compressedImage);
        }
        return user;
    }

    @Transactional
    public List<byte[]> send(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. 게시글ID : " + userId));

        List<byte[]> compressedImages = new ArrayList<>();

        if (user.getUserImage() != null) {
            byte[] compressedImage = ImageUtils.decompressImage(user.getUserImage());
            compressedImages.add(compressedImage);
        }
        return compressedImages;
    }

    @Transactional
    public User saveFeedBack(User user, String feedBack) throws IOException {
        user.setFeedBack(feedBack);
        return user;
    }

    @Transactional
    public String returnFeedBack(User user) throws IOException {
        return user.getFeedBack();
    }

    @Transactional
    public User saveFaceShape(User user, String faceShape) throws IOException {
        user.setFaceShape(faceShape);
        return user;
    }

    @Transactional
    public String returnFaceShape(User user) throws IOException {
        return user.getFaceShape();
    }

    @Transactional
    public User savePersonalColor(User user, String personalColor) throws IOException {
        user.setPersonalColor(personalColor);
        return user;
    }

    @Transactional
    public String returnPersonalColor(User user) throws IOException {
        return user.getPersonalColor();
    }

    @Transactional
    public User saveGlassesFrame(User user, String glassesFrame) throws IOException {
        user.setGlassesFrame(glassesFrame);
        return user;
    }

    @Transactional
    public String returnGlassesFrame(User user) throws IOException {
        return user.getGlassesFrame();
    }

    @Transactional
    public User saveGlassesColor(User user, String glassesColor) throws IOException {
        user.setGlassesColor(glassesColor);
        return user;
    }

    @Transactional
    public String returnGlassesColor(User user) throws IOException {
        return user.getGlassesColor();
    }

    @Transactional
    public User findUser(Long userid) {
        return userRepository.findById(userid).orElseThrow(()-> {
            return new IllegalArgumentException("User ID를 찾을 수 없습니다.");
        });
    }
}
