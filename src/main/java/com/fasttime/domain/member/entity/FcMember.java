package com.fasttime.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "fc_member")
public class FcMember {

    @Id
    private String email;


}
