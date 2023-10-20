package com.fasttime.domain.member.unit.service;

import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.service.AdminService;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;
import com.fasttime.domain.post.dto.service.response.PostsResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.entity.ReportStatus;
import com.fasttime.domain.post.repository.PostRepository;
import com.fasttime.domain.post.service.PostCommandService;
import com.fasttime.domain.post.service.PostCommandUseCase.PostCreateServiceDto;
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
    private PostCommandService postCommandService;
    @Autowired
    private PostRepository postRepository;
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
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);
            PostCreateServiceDto dto2 = new PostCreateServiceDto
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle2", "testContent2", false);
          
            PostDetailResponseDto newPost1 = postCommandService.writePost(dto1);
            PostDetailResponseDto newPost2 = postCommandService.writePost(dto2);
            Post post1 = postRepository.findById(newPost1.getId()).get();
            Post post2 = postRepository.findById(newPost2.getId()).get();
            post1.report();
            post2.report();
            post1.approveReport(LocalDateTime.now());
            post2.approveReport(LocalDateTime.now());

            // when
            List<PostsResponseDto> postList = adminService.findReportedPost(0);
            System.out.println(postList.size());

            //then
            Assertions.assertThat(postList.get(1).getTitle()).isEqualTo(post1.getTitle());
            Assertions.assertThat(postList.get(0).getTitle()).isEqualTo(post2.getTitle());
        }
        @DisplayName("없어 조회 할 수 없다.")
        @Test
        void _willFail(){
            //given
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);
            PostCreateServiceDto dto2 = new PostCreateServiceDto
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle2", "testContent2", false);

            // when
            List<PostsResponseDto> postList = adminService.findReportedPost(0);
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
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);

            PostDetailResponseDto newPost = postCommandService.writePost(dto1);
            Post post1 = postRepository.findById(newPost.getId()).get();

            post1.report();
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
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);

            PostDetailResponseDto newPost = postCommandService.writePost(dto1);
            Post post1 = postRepository.findById(newPost.getId()).get();

            post1.report();
            post1.approveReport(LocalDateTime.now());

            //when
            adminService.passPost(post1.getId());

            //then
            Assertions.assertThat(postRepository.findById(post1.getId()).get().getReportStatus())
                .isEqualTo(ReportStatus.REPORT_REJECTED);
        }
        //IllegalArgumentException
        @DisplayName("잘못된 접근으로 바꿀 수 없다.")
        @Test
        void _willFail() throws IllegalArgumentException {
            //given
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);

            PostDetailResponseDto newPost = postCommandService.writePost(dto1);
            Post post1 = postRepository.findById(newPost.getId()).get();

            //when ,then
            Assertions.assertThatThrownBy(() -> adminService.passPost(100L));
        }
    }

}
