package com.fasttime.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_email")
@NoArgsConstructor
@Getter
public class AdminEmail {

    @Id
    private String email;
}
