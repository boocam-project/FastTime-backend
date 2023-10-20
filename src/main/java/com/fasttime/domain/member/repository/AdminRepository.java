package com.fasttime.domain.member.repository;

import com.fasttime.domain.member.entity.Admin;
import com.fasttime.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin,Long> {

    Optional<Admin> findByMember(Member member);
}
