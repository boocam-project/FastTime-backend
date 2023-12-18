package com.fasttime.domain.member.service;

import com.fasttime.domain.member.dto.request.CreateMemberDTO;
import com.fasttime.domain.member.dto.request.EditRequest;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.request.RePasswordRequest;
import com.fasttime.domain.member.dto.request.RefreshRequestDto;
import com.fasttime.domain.member.dto.response.LogInResponseDto;
import com.fasttime.domain.member.dto.response.MemberResponse;
import com.fasttime.domain.member.dto.response.MemberResponseDto;
import com.fasttime.domain.member.dto.response.MyPageInfoDTO;
import com.fasttime.domain.member.dto.response.TokenResponseDto;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.entity.RefreshToken;
import com.fasttime.domain.member.entity.Role;
import com.fasttime.domain.member.exception.EmailAlreadyExistsException;
import com.fasttime.domain.member.exception.InvalidRefreshTokenException;
import com.fasttime.domain.member.exception.LoggedOutException;
import com.fasttime.domain.member.exception.MemberNotFoundException;
import com.fasttime.domain.member.exception.MemberNotMatchInfoException;
import com.fasttime.domain.member.exception.MemberNotMatchRePasswordException;
import com.fasttime.domain.member.exception.MemberSoftDeletedException;
import com.fasttime.domain.member.exception.NicknameAlreadyExistsException;
import com.fasttime.domain.member.exception.UnmatchedMemberException;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.repository.RefreshTokenRepository;
import com.fasttime.global.jwt.JwtPayload;
import com.fasttime.global.jwt.JwtProvider;
import com.fasttime.global.util.ResponseDTO;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final JwtProvider provider;
    private final RefreshTokenRepository refreshTokenRepository;
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
        member.setRole(Role.ROLE_USER);
        memberRepository.save(member);
    }


    public boolean isEmailExistsInMember(String email) {
        return memberRepository.existsByEmail(email);
    }


    public void save(Member member) {
        memberRepository.save(member);
    }

    public Optional<Member> updateMemberInfo(EditRequest editRequest, Long memberId) {

        return memberRepository.findById(memberId).map(member -> {
            member.update(editRequest.getNickname(), editRequest.getImage());
            return memberRepository.save(member);
        });
    }

    public MyPageInfoDTO getMyPageInfoById(Long memberId) throws MemberNotFoundException {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);

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
            .orElseThrow(MemberNotFoundException::new);
    }

    public MemberResponse rePassword(RePasswordRequest request, Long id) {
        if (request.getPassword().equals(request.getRePassword())) {
            Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
            member.setPassword(passwordEncoder.encode(request.getPassword()));
            return new MemberResponse(member.getId(), member.getNickname());
        }
        throw new MemberNotMatchRePasswordException();

    }

    public LogInResponseDto loginMember(LoginRequestDTO dto) throws MemberNotFoundException {
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(
            MemberNotMatchInfoException::new);
        if (member.getDeletedAt() != null) {
            throw new MemberSoftDeletedException();
        }
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new MemberNotMatchInfoException();
        }
        UsernamePasswordAuthenticationToken authenticationToken = dto.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);

        String serializedGrantedAuthority = extractGrantedAuthority(authentication);

        TokenResponseDto tokenResponseDto = provider.createToken(
            new JwtPayload(authentication.getName(), serializedGrantedAuthority));

        RefreshToken refreshToken = RefreshToken.builder()
            .id(Long.parseLong(authentication.getName()))
            .token(tokenResponseDto.getRefreshToken())
            .build();

        refreshTokenRepository.save(refreshToken);
        return LogInResponseDto.builder()
            .member(MemberResponseDto.of(member))
            .token(tokenResponseDto).build();
    }

    private String extractGrantedAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
    }

    public LogInResponseDto refresh(RefreshRequestDto dto) {
        if (!provider.validateToken(dto.getRefreshToken())) {
            throw new InvalidRefreshTokenException();
        }
        JwtPayload jwtPayload = provider.resolveToken(dto.getAccessToken());
        RefreshToken refreshToken = refreshTokenRepository.findById(
            Long.parseLong(jwtPayload.name())).orElseThrow(LoggedOutException::new);

        if (!refreshToken.getToken().equals(dto.getRefreshToken())) {
            throw new UnmatchedMemberException();
        }

        TokenResponseDto extendedExpirationRefreshTokenResponse = provider.createToken(jwtPayload);
        refreshToken.updateToken(extendedExpirationRefreshTokenResponse.getRefreshToken());

        return LogInResponseDto.builder()
            .member(MemberResponseDto.of(getMember(refreshToken.getId())))
            .token(extendedExpirationRefreshTokenResponse)
            .build();
    }
}
