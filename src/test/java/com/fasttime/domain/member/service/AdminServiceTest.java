package com.fasttime.domain.member.service;

import com.fasttime.domain.post.dto.service.request.PostCreateServiceDto;
import com.fasttime.domain.post.dto.service.response.PostResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.entity.ReportStatus;
import com.fasttime.domain.post.repository.PostRepository;
import com.fasttime.domain.post.service.PostCommandService;
import java.util.List;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
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

    @DisplayName("신고된 게시물들을(이)")
    @Nested
    class PostList{
        @DisplayName("조회할 수 있다. ")
        @Test
        void _willSuccess(){
            //given
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (0L, "testTitle1", "testContent1", false);
            PostCreateServiceDto dto2 = new PostCreateServiceDto
                (1L, "testTitle2", "testContent2", false);
            PostResponseDto postResponseDto1 = postCommandService.writePost(dto1);
            PostResponseDto postResponseDto2 = postCommandService.writePost(dto2);
            Post post1 = postRepository.findById(postResponseDto1.getId()).get();
            Post post2 = postRepository.findById(postResponseDto2.getId()).get();
            post1.ChangeReportStatus(ReportStatus.REPORTED);
            post2.ChangeReportStatus(ReportStatus.REPORTED);
            // when
            List<Post> postList = adminService.FindReportedPost();
            for (Post post : postList) {
                System.out.println(post.getTitle());
            }
            //then
            Assertions.assertThat(postList.get(0)).isEqualTo(post1);
            Assertions.assertThat(postList.get(1)).isEqualTo(post2);
        }
        @DisplayName("없어 조회 할 수 없다.")
        @Test
        void _willFail(){
            //given
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (0L, "testTitle1", "testContent1", false);
            PostCreateServiceDto dto2 = new PostCreateServiceDto
                (1L, "testTitle2", "testContent2", false);
            PostResponseDto postResponseDto1 = postCommandService.writePost(dto1);
            PostResponseDto postResponseDto2 = postCommandService.writePost(dto2);
            Post post1 = postRepository.findById(postResponseDto1.getId()).get();
            Post post2 = postRepository.findById(postResponseDto2.getId()).get();
            // when
            List<Post> postList = adminService.FindReportedPost();
            //then
            Assertions.assertThat(postList.isEmpty()).isTrue();
        }
    }
    @DisplayName("게시물을")
    @Nested
    class PostDetail{
        @DisplayName("삭제할 수 있다.")
        @Test
        void Delete_willSuccess(){
            //given
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (0L, "testTitle1", "testContent1", false);
            PostResponseDto dto = postCommandService.writePost(dto1);
            //when
            adminService.DeletePost(dto.getId());
            //then
            Assertions.assertThat(postRepository.findById(dto.getId()).isEmpty()).isTrue();
        }
        @DisplayName("검토완료로 바꿀 수 있다.")
        @Test
        void Pass_willSuccess(){
            //given
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (0L, "testTitle1", "testContent1", false);
            PostResponseDto postResponseDto1 = postCommandService.writePost(dto1);
            Post post1 = postRepository.findById(postResponseDto1.getId()).get();
            post1.ChangeReportStatus(ReportStatus.REPORTED);
            //when
            adminService.PassPost(post1.getId());
            //then
            Assertions.assertThat(postRepository.findById(post1.getId()).get().getReportStatus())
                .isEqualTo(ReportStatus.REPORTE_ABORTED);
        }
    }


}
