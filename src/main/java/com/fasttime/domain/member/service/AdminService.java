package com.fasttime.domain.member.service;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.exception.BadArticleReportStatusException;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.article.service.ArticleQueryService;
import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase.ReportedArticlesSearchRequestServiceDto;
import com.fasttime.domain.member.dto.request.CreateMemberRequest;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.entity.Role;
import com.fasttime.domain.member.exception.AdminNotFoundException;
import com.fasttime.domain.member.exception.MemberNotMatchInfoException;
import com.fasttime.domain.member.repository.AdminEmailRepository;
import com.fasttime.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final ArticleQueryService articleQueryService;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final AdminEmailRepository adminEmailRepository;


    public void save(CreateMemberRequest dto) {

        if (memberService.isEmailExistsInMember(dto.getEmail())) {
            throw new MemberNotMatchInfoException();
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
        return articleQueryService.findReportedArticles(
            new ReportedArticlesSearchRequestServiceDto(page, DEFAULT_PAGE_SIZE,
                ReportStatus.WAIT_FOR_REPORT_REVIEW));
    }

    public ArticleResponse findOneReportedPost(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new ArticleNotFoundException());
        if (!article.getReportStatus().equals(ReportStatus.WAIT_FOR_REPORT_REVIEW)) {
            throw new BadArticleReportStatusException();
        }
        return ArticleResponse.builder()
            .id(article.getId())
            .title(article.getTitle())
            .content(article.getContent())
            .nickname(article.getMember().getNickname())
            .isAnonymity(article.isAnonymity())
            .likeCount(article.getLikeCount())
            .hateCount(article.getHateCount())
            .createdAt(article.getCreatedAt())
            .lastModifiedAt(article.getUpdatedAt())
            .build();
    }

    public void deletePost(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(ArticleNotFoundException::new);
        articleRepository.delete(article);
    }

    public void passPost(Long id) {
        Article article = articleRepository.findById(id).
            orElseThrow(ArticleNotFoundException::new);
        article.rejectReport();
    }
}
