package com.fasttime.domain.member.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
