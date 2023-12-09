package com.fasttime.domain.member.repository;

import com.fasttime.domain.member.entity.RefreshToken;;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
}
