package com.fasttime.domain.member.repository;

import com.fasttime.domain.member.entity.AdminEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminEmailRepository extends JpaRepository<AdminEmail,Long> {

    boolean existsAdminEmailByEmail(String email);

}
