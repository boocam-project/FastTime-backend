package com.fasttime.domain.member.service;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.exception.BadArticleReportStatusException;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.entity.Role;
import com.fasttime.domain.member.exception.AdminNotFoundException;
import com.fasttime.domain.member.exception.UserNotMatchInfoException;
import com.fasttime.domain.member.repository.AdminEmailRepository;
import com.fasttime.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final ArticleRepository postRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final AdminEmailRepository adminEmailRepository;


    public void save(MemberDto dto) {

        if (memberService.isEmailExistsInMember(dto.getEmail())) {
            throw new UserNotMatchInfoException();
        }
        if (!adminEmailRepository.existsAdminEmailByEmail(dto.getEmail())) {
            throw new AdminNotFoundException();
        }

        Member member = new Member();
        member.setEmail(dto.getEmail());
        member.setNickname(dto.getNickname());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
        member.setRole(Role.ROLE_ADMIN);
        memberRepository.save(member);
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
            .orElseThrow(ArticleNotFoundException::new);
        postRepository.delete(post);
    }

    public void passPost(Long id) {
        Article post = postRepository.findById(id).
            orElseThrow(ArticleNotFoundException::new);
        post.rejectReport();
    }

}
