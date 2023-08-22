package com.fasttime.domain.member.repository;

import com.fasttime.domain.member.entity.FcMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcMemberRepository extends JpaRepository<FcMember, String> {


    boolean existsByEmail(String email);
}