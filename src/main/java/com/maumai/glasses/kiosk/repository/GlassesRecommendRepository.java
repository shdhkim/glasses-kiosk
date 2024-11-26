package com.maumai.glasses.kiosk.repository;

import com.maumai.glasses.kiosk.entity.GlassesRecommend;
import com.maumai.glasses.kiosk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GlassesRecommendRepository extends JpaRepository<GlassesRecommend, Long> {
    List<GlassesRecommend> findByUser(User user);// 특정 사용자에 대한 안경 추천 리스트

    void deleteByUser(User user); // 사용자로 추천 안경을 삭제하는 메서드
}
