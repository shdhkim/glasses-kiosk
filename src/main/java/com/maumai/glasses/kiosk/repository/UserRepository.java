package com.maumai.glasses.kiosk.repository;

import com.maumai.glasses.kiosk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}

