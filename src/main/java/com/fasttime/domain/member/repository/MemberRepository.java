package com.fasttime.domain.member.repository;

import com.fasttime.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long>{


    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
}