package com.fasttime.domain.member.service;

import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;
import com.fasttime.domain.post.dto.service.response.PostsResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.entity.ReportStatus;
import com.fasttime.domain.post.repository.PostRepository;
import java.rmi.AccessException;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final PostRepository postRepository;

    public List<PostsResponseDto> findReportedPost(){
        return postRepository.findAllByReportStatus(ReportStatus.REPORTED).stream()
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

    public PostDetailResponseDto findOneReportedPost(Long id) throws AccessException {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        System.out.println(post.getReportStatus());
        if (!post.getReportStatus().equals(ReportStatus.REPORTED)){
            throw new AccessException("잘못된 접근입니다.");
        }
        return PostDetailResponseDto.entityToDto(post);
    }
    public void deletePost(Long id){
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        postRepository.delete(post);
    }
    public void passPost(Long id) {
        Post post = postRepository.findById(id).
            orElseThrow(()-> new IllegalArgumentException("게시글이 없습니다."));
        post.rejectReport();
    }

}
