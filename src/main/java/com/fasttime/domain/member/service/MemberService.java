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

    public Member getMember(Long id) throws UserNotFoundException {
        return memberRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public MemberResponse loginMember(LoginRequestDTO dto) throws UserNotFoundException {
        Optional<Member> byEmail = memberRepository.findByEmail(dto.getEmail());
        if (!byEmail.isPresent()) {
            throw new UserNotFoundException("User not found with email: " + dto.getEmail());
        }
        Member member = byEmail.get();
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("Not match password!");
        } else {
            return new MemberResponse(member.getId(), member.getNickname());
        }
    }

    public MemberResponse rePassword(RePasswordRequest request,Long id) {
        if (request.getPassword().equals(request.getRePassword())) {
            Member member = memberRepository.findById(id).get();
            member.setPassword(passwordEncoder.encode(request.getPassword()));
            return new MemberResponse(member.getId(), member.getNickname());
        } else {
            throw new BadCredentialsException("Not Match RePassword!");
        }
    }

    public MemberDto getMyPageInfo(Member member) throws UserNotFoundException {
        if (member != null) {
            return new MemberDto(member.getNickname(),member.getPassword(), member.getEmail(), member.getImage());
        } else {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다."); // 예외 던지기
        }
    }

}