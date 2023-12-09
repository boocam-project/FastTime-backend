package com.fasttime.domain.member.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(email)
            .map(this::createUserDetails)
            .orElseThrow(() -> new UsernameNotFoundException(email + "을 DB에서 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
            member.getRole().toString());
        return new User(
            String.valueOf(member.getId()),
            member.getPassword(),
            Collections.singleton(grantedAuthority));
    }

}
