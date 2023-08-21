package com.fasttime.domain.member.service;

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



    public void save(MemberDto memberDto) {


        Member member = new Member();
        member.setEmail(memberDto.getEmail());
        member.setPassword(memberDto.getPassword()); // 인코딩된 패스워드 저장?
        member.setNickname(memberDto.getNickname());

        memberRepository.save(member);
    }


    public void save(Member member) {
        memberRepository.save(member);
    }

/*

    public Member findByEmail(String email){
        return fc_memberRepository.findByEmail(email)

            .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다." + email));
    }
*/



    public boolean checkDuplicateNickname(String username) {
        return memberRepository.findByNickname(username).isPresent();
    }



    public void deleteMember(int userId) throws UserNotFoundException {
        Optional<Member> member = memberRepository.findById(userId);
        if (!member.isPresent()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        memberRepository.delete(member.get());
    }


}