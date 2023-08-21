package com.fasttime.domain.member.service;

import com.fasttime.domain.member.entity.FcMember;
import com.fasttime.domain.member.repository.FcMemberRepository;

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

        Member member = new Member();
        member.setEmail(memberDto.getEmail());
        member.setPassword(memberDto.getPassword()); // 인코딩된 패스워드 저장?
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


    public void deleteMember(int userId) throws UserNotFoundException {
        Optional<Member> member = memberRepository.findById(userId);
        if (!member.isPresent()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        memberRepository.delete(member.get());
    }


}