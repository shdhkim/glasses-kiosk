package com.maumai.glasses.kiosk.repository;

import com.maumai.glasses.kiosk.entity.Glasses;
import com.maumai.glasses.kiosk.entity.GlassesRecommend;
import com.maumai.glasses.kiosk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GlassesRepository extends JpaRepository<Glasses, Long> {

}