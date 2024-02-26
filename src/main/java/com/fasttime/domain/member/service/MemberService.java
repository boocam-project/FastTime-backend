package com.fasttime.domain.member.service;

import com.fasttime.domain.member.dto.request.CreateMemberRequest;
import com.fasttime.domain.member.dto.request.UpdateMemberRequest;
import com.fasttime.domain.member.dto.request.LoginRequest;
import com.fasttime.domain.member.dto.request.RePasswordRequest;
import com.fasttime.domain.member.dto.request.RefreshRequest;
import com.fasttime.domain.member.dto.response.LoginResponse;
import com.fasttime.domain.member.dto.response.RepasswordResponse;
import com.fasttime.domain.member.dto.response.RefreshResponse;
import com.fasttime.domain.member.dto.response.GetMyInfoResponse;
import com.fasttime.domain.member.dto.response.TokenResponse;
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


    public ResponseDTO<Object> recoverMember(CreateMemberRequest createMemberRequest) {
        Optional<Member> softDeletedMember = memberRepository.findSoftDeletedByEmail(
            createMemberRequest.getEmail(), LocalDateTime.now().minusYears(1));

        if (softDeletedMember.isPresent()) {
            Member member = softDeletedMember.get();
            member.restore();
            member.setNickname(createMemberRequest.getNickname());
            memberRepository.save(member);
            return ResponseDTO.res(HttpStatus.OK, "계정이 성공적으로 복구되었습니다!");
        }
        return null;
    }

    public ResponseDTO<Object> registerMember(CreateMemberRequest createMemberRequest) {
        if (isEmailExistsInMember(createMemberRequest.getEmail())) {
            throw new EmailAlreadyExistsException();
        } else if (checkDuplicateNickname(createMemberRequest.getNickname())) {
            throw new NicknameAlreadyExistsException();
        }
        saveNewMember(createMemberRequest);
        return ResponseDTO.res(HttpStatus.OK, "가입 성공!");
    }

    public ResponseDTO<Object> registerOrRecoverMember(CreateMemberRequest createMemberRequest) {
        ResponseDTO<Object> recoveryResponse = recoverMember(createMemberRequest);
        if (recoveryResponse != null) {
            return recoveryResponse;
        }
        return registerMember(createMemberRequest);
    }


    public void saveNewMember(CreateMemberRequest createMemberRequest) {

        Member member = new Member(
            createMemberRequest.getEmail(),
            createMemberRequest.getNickname(),
            passwordEncoder.encode(createMemberRequest.getPassword()),
            Role.ROLE_USER
        );
        memberRepository.save(member);
    }


    public boolean isEmailExistsInMember(String email) {
        return memberRepository.existsByEmail(email);
    }


    public Optional<Member> updateMemberInfo(UpdateMemberRequest updateMemberRequest,
        Long memberId) {

        return memberRepository.findById(memberId).map(member -> {
            member.update(updateMemberRequest.getNickname(), updateMemberRequest.getImage());
            return memberRepository.save(member);
        });
    }

    public GetMyInfoResponse getMyPageInfoById(Long memberId) throws MemberNotFoundException {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);

        String bootcampName = null;
        if (member.isCampCrtfc() && member.getBootCamp() != null) {
            bootcampName = member.getBootCamp().getName();
        }

        return GetMyInfoResponse.builder()
            .nickname(member.getNickname())
            .image(member.getImage())
            .email(member.getEmail())
            .campCrtfc(member.isCampCrtfc())
            .bootcampName(bootcampName)
            .build();
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

    public RepasswordResponse rePassword(RePasswordRequest request, Long id) {
        if (request.getPassword().equals(request.getRePassword())) {
            Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
            member.setPassword(passwordEncoder.encode(request.getPassword()));
            return new RepasswordResponse(member.getId(), member.getNickname());
        }
        throw new MemberNotMatchRePasswordException();

    }

    public LoginResponse loginMember(LoginRequest dto) throws MemberNotFoundException {
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

        TokenResponse tokenResponse = provider.createToken(
            new JwtPayload(authentication.getName(), serializedGrantedAuthority));

        RefreshToken refreshToken = RefreshToken.builder()
            .id(Long.parseLong(authentication.getName()))
            .token(tokenResponse.getRefreshToken())
            .build();

        refreshTokenRepository.save(refreshToken);
        return LoginResponse.builder()
            .member(RefreshResponse.of(member))
            .token(tokenResponse).build();
    }

    private String extractGrantedAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
    }

    public LoginResponse refresh(RefreshRequest dto) {
        if (!provider.validateToken(dto.getRefreshToken())) {
            throw new InvalidRefreshTokenException();
        }
        JwtPayload jwtPayload = provider.resolveToken(dto.getAccessToken());
        RefreshToken refreshToken = refreshTokenRepository.findById(
            Long.parseLong(jwtPayload.name())).orElseThrow(LoggedOutException::new);

        if (!refreshToken.getToken().equals(dto.getRefreshToken())) {
            throw new UnmatchedMemberException();
        }

        TokenResponse extendedExpirationRefreshTokenResponse = provider.createToken(jwtPayload);
        refreshToken.updateToken(extendedExpirationRefreshTokenResponse.getRefreshToken());

        return LoginResponse.builder()
            .member(RefreshResponse.of(getMember(refreshToken.getId())))
            .token(extendedExpirationRefreshTokenResponse)
            .build();
    }
}
