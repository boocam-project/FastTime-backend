package com.fasttime.domain.member.service;


import com.fasttime.domain.member.repository.FcMemberRepository;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;


import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FcMemberRepository fcMemberRepository;


    public void save(MemberDto memberDto) {
        //패스워드 인코딩(security적용 후 주석 해제)
        //String encodedPassword = passwordEncoder.encode(memberDto.getPassword());

        Member member = new Member();
        member.setEmail(memberDto.getEmail());
        member.setPassword(memberDto.getPassword());
        //security적용 후 주석 해제
        //member.setPassword(encodedPassword);
        member.setNickname(memberDto.getNickname());

        memberRepository.save(member);
    }

    public boolean isEmailExistsInFcmember(String email) {
        return fcMemberRepository.existsByEmail(email);
    }

    public boolean isEmailExistsInMember(String email) {
        return memberRepository.existsByEmail(email);
    }


    public void save(Member member) {
        memberRepository.save(member);
    }


    public boolean checkDuplicateNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }


    public void softDeleteMember(Member member) {
        //deleted_at 칼럼에 값이 있으면 탈퇴된 회원으로 간주
        member.setDeletedAt(LocalDateTime.now()); // 현재 시간으로 소프트 삭제 시간 설정
        memberRepository.save(member); // 업데이트된 정보를 데이터베이스에 저장
    }


}