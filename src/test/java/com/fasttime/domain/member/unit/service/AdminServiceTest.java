package com.fasttime.domain.member.unit.service;

import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.service.AdminService;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.article.service.ArticleCommandService;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleCreateServiceRequest;
import java.rmi.AccessException;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class AdminServiceTest {

    @Autowired
    private AdminService adminService;
    @Autowired
    private ArticleCommandService postCommandService;
    @Autowired
    private ArticleRepository postRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void addMember(){
        memberService.save(new MemberDto("test","test","test"));
    }

    @DisplayName("신고된 게시물들을(이)")
    @Nested
    class PostList{
        @DisplayName("조회할 수 있다. ")
        @Test
        void _willSuccess(){
            //given
            ArticleCreateServiceRequest dto1 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);
            ArticleCreateServiceRequest dto2 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);
          
            ArticleResponse newPost1 = postCommandService.write(dto1);
            ArticleResponse newPost2 = postCommandService.write(dto2);
            Article post1 = postRepository.findById(newPost1.getId()).get();
            Article post2 = postRepository.findById(newPost2.getId()).get();
            post1.transToWaitForReview();
            post2.transToWaitForReview();

            // when
            List<ArticlesResponse> postList = adminService.findReportedPost(0);
            System.out.println(postList.size());

            //then
            Assertions.assertThat(postList.get(1).getTitle()).isEqualTo(post1.getTitle());
            Assertions.assertThat(postList.get(0).getTitle()).isEqualTo(post2.getTitle());
        }
        @DisplayName("없어 조회 할 수 없다.")
        @Test
        void _willFail(){
            //given
            ArticleCreateServiceRequest dto1 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);
            ArticleCreateServiceRequest dto2 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle2", "testContent2", false);

            // when
            List<ArticlesResponse> postList = adminService.findReportedPost(0);
            //then
            Assertions.assertThat(postList.isEmpty()).isTrue();
        }
    }
    @DisplayName("게시물을")
    @Nested
    class PostDetail{
        @DisplayName("삭제할 수 있다.")
        @Test
        void Delete_willSuccess() throws AccessException {
            //given
            ArticleCreateServiceRequest dto1 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);

            ArticleResponse newPost = postCommandService.write(dto1);
            Article post1 = postRepository.findById(newPost.getId()).get();

            post1.transToWaitForReview();
            post1.approveReport(LocalDateTime.now());
            //when
            adminService.deletePost(newPost.getId());
            //then
            Assertions.assertThat(postRepository.findById(newPost.getId()).isEmpty()).isTrue();
        }
        @DisplayName("검토완료로 바꿀 수 있다.")
        @Test
        void Pass_willSuccess() throws AccessException {
            //given
            ArticleCreateServiceRequest dto1 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);

            ArticleResponse newPost = postCommandService.write(dto1);
            Article post1 = postRepository.findById(newPost.getId()).get();

            post1.transToWaitForReview();

            //when
            adminService.passPost(post1.getId());

            //then
            Assertions.assertThat(postRepository.findById(post1.getId()).get().getReportStatus())
                .isEqualTo(ReportStatus.REPORT_REJECT);
        }
        //IllegalArgumentException
        @DisplayName("잘못된 접근으로 바꿀 수 없다.")
        @Test
        void _willFail() throws IllegalArgumentException {
            //given
            ArticleCreateServiceRequest dto1 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);

            ArticleResponse newPost = postCommandService.write(dto1);
            Article post1 = postRepository.findById(newPost.getId()).get();

            //when ,then
            Assertions.assertThatThrownBy(() -> adminService.passPost(100L));
        }
    }

}
