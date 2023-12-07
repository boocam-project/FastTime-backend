package com.fasttime.domain.member.service;

import com.fasttime.domain.member.dto.response.MyPageInfoDTO;
import com.fasttime.domain.member.dto.request.EditRequest;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.exception.EmailAlreadyExistsException;
import com.fasttime.domain.member.exception.NicknameAlreadyExistsException;
import com.fasttime.domain.member.dto.request.CreateMemberDTO;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.MemberNotFoundException;
import com.fasttime.domain.member.exception.MemberNotMatchInfoException;
import com.fasttime.domain.member.exception.MemberNotMatchRePasswordException;
import com.fasttime.domain.member.exception.MemberSoftDeletedException;
import com.fasttime.domain.member.repository.FcMemberRepository;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.dto.request.RePasswordRequest;
import com.fasttime.domain.member.dto.response.MemberResponse;
import com.fasttime.global.util.ResponseDTO;
import java.time.LocalDateTime;
import jakarta.servlet.http.HttpSession;
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


    public ResponseDTO<Object> registerOrRecoverMember(CreateMemberDTO createMemberDTO) {

        Optional<Member> softDeletedMember = memberRepository.findSoftDeletedByEmail(
            createMemberDTO.getEmail(), LocalDateTime.now().minusYears(1));

        if (softDeletedMember.isPresent()) {
            Member member = softDeletedMember.get();
            member.restore();
            member.setNickname(createMemberDTO.getNickname());
            save(member);
            return ResponseDTO.res(HttpStatus.OK, "계정이 성공적으로 복구되었습니다!");
        }
        if (isEmailExistsInMember(createMemberDTO.getEmail())) {
            throw new EmailAlreadyExistsException();
        } else if (checkDuplicateNickname(createMemberDTO.getNickname())) {
            throw new NicknameAlreadyExistsException();
        }
        save(createMemberDTO);
        return ResponseDTO.res(HttpStatus.OK, "가입 성공!");
    }


    public void save(CreateMemberDTO createMemberDTO) {

        Member member = new Member();
        member.setEmail(createMemberDTO.getEmail());
        member.setNickname(createMemberDTO.getNickname());
        member.setPassword(passwordEncoder.encode(createMemberDTO.getPassword()));
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

    public Optional<Member> updateMemberInfo(EditRequest editRequest, HttpSession session) {
        Long memberId = (Long) session.getAttribute("MEMBER");
        if (memberId == null) {
            return Optional.empty();
        }

        return memberRepository.findById(memberId).map(member -> {
            member.update(editRequest.getNickname(), editRequest.getImage());
            return memberRepository.save(member);
        });
    }

    public MyPageInfoDTO getMyPageInfoById(Long memberId) throws MemberNotFoundException {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException());

        return new MyPageInfoDTO(
            member.getNickname(),
            member.getImage(),
            member.getEmail()
        );
    }


    public boolean checkDuplicateNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }


    public void softDeleteMember(Member member) {

        member.delete(LocalDateTime.now());
        memberRepository.save(member);
    }

    public Member getMember(Long id) throws MemberNotFoundException {
        return memberRepository.findById(id)
            .orElseThrow(() -> new MemberNotFoundException("User not found with id: " + id));
    }

    public MemberResponse loginMember(LoginRequestDTO dto) throws MemberNotFoundException {
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(
            () -> new MemberNotMatchInfoException());
        if (member.getDeletedAt() != null) {
            throw new MemberSoftDeletedException();
        }

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new MemberNotMatchInfoException();
        }
        return new MemberResponse(member.getId(), member.getNickname());

    }

    public MemberResponse rePassword(RePasswordRequest request, Long id) {
        if (request.getPassword().equals(request.getRePassword())) {
            Member member = memberRepository.findById(id).get();
            member.setPassword(passwordEncoder.encode(request.getPassword()));
            return new MemberResponse(member.getId(), member.getNickname());
        }
        throw new MemberNotMatchRePasswordException();

    }


}
