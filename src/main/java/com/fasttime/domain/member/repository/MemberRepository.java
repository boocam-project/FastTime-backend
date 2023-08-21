package com.fasttime.domain.member.repository;

import com.fasttime.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Integer> {


    Optional<Member> findByNickname(String nickname);


    boolean existsByEmail(String email);
}