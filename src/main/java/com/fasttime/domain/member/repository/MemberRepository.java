package com.fasttime.domain.member.repository;

import com.fasttime.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteByDeletedAtBefore(LocalDateTime dateTime);

    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.deletedAt > :dateTime")
    Optional<Member> findSoftDeletedByEmail(@Param("email") String email,
        @Param("dateTime") LocalDateTime dateTime);
}
