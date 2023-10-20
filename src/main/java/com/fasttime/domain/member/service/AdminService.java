package com.fasttime.domain.member.service;

import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.request.saveAdminDTO;
import com.fasttime.domain.member.entity.Admin;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.AdminNotFoundException;
import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.repository.AdminEmailRepository;
import com.fasttime.domain.member.repository.AdminRepository;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;
import com.fasttime.domain.post.dto.service.response.PostsResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.entity.ReportStatus;
import com.fasttime.domain.post.repository.PostRepository;
import java.rmi.AccessException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final AdminRepository adminRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final AdminEmailRepository adminEmailRepository;

    // 수정이 필요한 버전
    public void save(saveAdminDTO dto) {

        if (memberService.isEmailExistsInMember(dto.getEmail())) {
            throw new IllegalArgumentException("이미 생성된 이메일입니다.");
        }
        if (!adminEmailRepository.existsAdminEmailByEmail(dto.getEmail())) {
            throw new AdminNotFoundException("Admin not found");
          
        memberService.save(new MemberDto(dto.getEmail(), dto.getPassword(), dto.getEmail()));
        adminRepository.save(Admin.builder().member(memberRepository.findByEmail
            (dto.getEmail()).get()).build());
    }

    public Long loginAdmin(LoginRequestDTO dto) {
        Optional<Member> byEmail = memberRepository.findByEmail(dto.getEmail());
        if (byEmail.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + dto.getEmail());
        }

        Member member = byEmail.get();
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("Not match password!");
        }
        Optional<Admin> byMember = adminRepository.findByMember(member);
        if (byMember.isEmpty()) {
            throw new AdminNotFoundException("Admin not found");
        }
        return byMember.get().getId();
    }

    public List<PostsResponseDto> findReportedPost(int page) {
        return postRepository.findAllByReportStatus(
                createSortCondition(page, "createdAt"), ReportStatus.REPORTED)
            .stream()
            .map(post -> PostsResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .nickname(post.getMember().getNickname())
                .anonymity(post.isAnonymity())
                .likeCount(post.getLikeCount())
                .hateCount(post.getHateCount())
                .createdAt(post.getCreatedAt())
                .lastModifiedAt(post.getUpdatedAt())
                .build())
            .collect(Collectors.toList());
    }

    @NotNull
    private static PageRequest createSortCondition(int searchPage, String propertyName) {
        return PageRequest.of(searchPage, DEFAULT_PAGE_SIZE)
            .withSort(Sort.by(propertyName).descending());
    }

    public PostDetailResponseDto findOneReportedPost(Long id) throws AccessException {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        System.out.println(post.getReportStatus());
        if (!post.getReportStatus().equals(ReportStatus.REPORTED)) {
            throw new AccessException("잘못된 접근입니다.");
        }
        return PostDetailResponseDto.entityToDto(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        postRepository.delete(post);
    }

    public void passPost(Long id) {
        Post post = postRepository.findById(id).
            orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        post.rejectReport();
    }

}
