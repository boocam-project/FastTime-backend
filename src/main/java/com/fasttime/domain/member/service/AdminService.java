package com.fasttime.domain.member.service;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.exception.BadArticleReportStatusException;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.request.saveAdminDTO;
import com.fasttime.domain.member.entity.Admin;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.AdminNotFoundException;
import com.fasttime.domain.member.exception.MemberNotFoundException;
import com.fasttime.domain.member.repository.AdminEmailRepository;
import com.fasttime.domain.member.repository.AdminRepository;
import com.fasttime.domain.member.repository.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.transaction.Transactional;
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
    private final ArticleRepository postRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final AdminEmailRepository adminEmailRepository;


    public void save(saveAdminDTO dto) {

        if (memberService.isEmailExistsInMember(dto.getEmail())) {
            throw new IllegalArgumentException("이미 생성된 이메일입니다.");
        }
        if (!adminEmailRepository.existsAdminEmailByEmail(dto.getEmail())) {
            throw new AdminNotFoundException("Admin not found");
        }
        memberService.save(new MemberDto(dto.getEmail(), dto.getPassword(), dto.getEmail()));
        adminRepository.save(Admin.builder().member(memberRepository.findByEmail
            (dto.getEmail()).get()).build());
    }

    public Long loginAdmin(LoginRequestDTO dto) {

        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(
            () -> new MemberNotFoundException("User not found with email: " + dto.getEmail()));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("Not match password!");
        }
        return adminRepository.findByMember(member)
            .orElseThrow(() -> new AdminNotFoundException("Admin not found")).getId();

    }

    public List<ArticlesResponse> findReportedPost(int page) {
        return postRepository.findAllByReportStatus(
                createSortCondition(page, "createdAt"), ReportStatus.WAIT_FOR_REPORT_REVIEW)
            .stream()
            .map(post -> ArticlesResponse.builder()
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


    public ArticleResponse findOneReportedPost(Long id) {
        Article post = postRepository.findById(id)
            .orElseThrow(() -> new ArticleNotFoundException());
        if (!post.getReportStatus().equals(ReportStatus.WAIT_FOR_REPORT_REVIEW)) {
            throw new BadArticleReportStatusException();
        }
        return ArticleResponse.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .nickname(post.getMember().getNickname())
            .anonymity(post.isAnonymity())
            .likeCount(post.getLikeCount())
            .hateCount(post.getHateCount())
            .createdAt(post.getCreatedAt())
            .lastModifiedAt(post.getUpdatedAt())
            .build();
    }

    public void deletePost(Long id) {
        Article post = postRepository.findById(id)
            .orElseThrow(() -> new ArticleNotFoundException());
        postRepository.delete(post);
    }

    public void passPost(Long id) {
        Article post = postRepository.findById(id).
            orElseThrow(() -> new ArticleNotFoundException());
        post.rejectReport();
    }

}
