package com.fasttime.domain.member.service;

import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.repository.FcMemberRepository;
import com.fasttime.domain.member.request.RePasswordRequest;
import com.fasttime.domain.member.response.MemberResponse;
import java.time.LocalDateTime;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void deleteExpiredSoftDeletedMembers() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        memberRepository.deleteByDeletedAtBefore(oneYearAgo);
    }


    public void save(MemberDto memberDto) {

        Member member = new Member();
        member.setEmail(memberDto.getEmail());
        member.setNickname(memberDto.getNickname());
        member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        memberRepository.save(member);
    }

    public boolean isEmailExistsInFcmember(String email) {
        return fcMemberRepository.existsByEmail(email);
    }

    public boolean isEmailExistsInMember(String email) {
        return memberRepository.existsByEmail(email); // 저장소에 existsByEmail 메소드가 있다고 가정합니다.
    }



    public void save(Member member) {
        memberRepository.save(member);
    }


    public boolean checkDuplicateNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }


    public void softDeleteMember(Member member) {

        member.delete(LocalDateTime.now());
        memberRepository.save(member);
    }

    public Member getMember(Long id) throws UserNotFoundException {
        return memberRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public MemberResponse loginMember(LoginRequestDTO dto) throws UserNotFoundException {
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(
            () -> new UserNotFoundException("User not found with email: " + dto.getEmail()));
        if (member.getDeletedAt() != null) {
            throw new UserNotFoundException("이미 탈퇴한 계정입니다");
        }

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("Not match password!");
        }
        return new MemberResponse(member.getId(), member.getNickname());

    }

    public MemberResponse rePassword(RePasswordRequest request, Long id) {
        if (request.getPassword().equals(request.getRePassword())) {
            Member member = memberRepository.findById(id).get();
            member.setPassword(passwordEncoder.encode(request.getPassword()));
            return new MemberResponse(member.getId(), member.getNickname());
        }
        throw new BadCredentialsException("Not Match RePassword!");

    }


}