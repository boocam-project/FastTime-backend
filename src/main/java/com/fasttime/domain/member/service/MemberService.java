package com.fasttime.domain.member.service;

import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.exception.UserNotMatchInfoException;
import com.fasttime.domain.member.exception.UserNotMatchRePasswordException;
import com.fasttime.domain.member.exception.UserSoftDeletedException;
import com.fasttime.domain.member.repository.FcMemberRepository;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.request.RePasswordRequest;
import com.fasttime.domain.member.response.MemberResponse;
import com.fasttime.global.util.ResponseDTO;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FcMemberRepository fcMemberRepository;
    private final PasswordEncoder passwordEncoder;


    public void deleteExpiredSoftDeletedMembers() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        memberRepository.deleteByDeletedAtBefore(oneYearAgo);
    }

    public ResponseDTO<Object> registerOrRecoverMember(MemberDto memberDto) {
        try {
            LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
            Optional<Member> softDeletedMember = memberRepository.findSoftDeletedByEmail(
                memberDto.getEmail(), oneYearAgo);

            if (softDeletedMember.isPresent()) {

                Member member = softDeletedMember.get();

                member.restore();
                member.setNickname(memberDto.getNickname());
                save(member);
                return ResponseDTO.res(HttpStatus.OK, "계정이 성공적으로 복구되었습니다!");
            }

            if (isEmailExistsInMember(memberDto.getEmail())) {

                return ResponseDTO.res(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다.");
            } else if (checkDuplicateNickname(memberDto.getNickname())) {
                return ResponseDTO.res(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임 입니다.");
            }

            save(memberDto);
            return ResponseDTO.res(HttpStatus.OK, "가입 성공!");

        } catch (Exception e) {
            return ResponseDTO.res(HttpStatus.INTERNAL_SERVER_ERROR, "회원가입 실패 " + e.getMessage());
        }
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
        return memberRepository.existsByEmail(email);
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
            () -> new UserNotMatchInfoException());
        if (member.getDeletedAt() != null) {
            throw new UserSoftDeletedException();
        }

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new UserNotMatchInfoException();
        }
        return new MemberResponse(member.getId(), member.getNickname());

    }

    public MemberResponse rePassword(RePasswordRequest request, Long id) {
        if (request.getPassword().equals(request.getRePassword())) {
            Member member = memberRepository.findById(id).get();
            member.setPassword(passwordEncoder.encode(request.getPassword()));
            return new MemberResponse(member.getId(), member.getNickname());
        }
        throw new UserNotMatchRePasswordException();

    }


}
