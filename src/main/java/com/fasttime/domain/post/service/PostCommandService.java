package com.fasttime.domain.post.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.post.dto.service.request.PostCreateServiceDto;
import com.fasttime.domain.post.dto.service.request.PostDeleteServiceDto;
import com.fasttime.domain.post.dto.service.request.PostUpdateServiceDto;
import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.exception.NotPostWriterException;
import com.fasttime.domain.post.exception.PostNotFoundException;
import com.fasttime.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class PostCommandService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public PostCommandService(MemberRepository memberRepository, PostRepository postRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    public PostDetailResponseDto writePost(PostCreateServiceDto serviceDto) {

        Member member = memberRepository.findById(serviceDto.getMemberId())
            .orElseThrow(() -> new UserNotFoundException("회원 정보가 없습니다."));

        Post createdPost = Post.createNewPost(member, serviceDto.getTitle(),
            serviceDto.getContent(),
            false);

        Post savedPost = postRepository.save(createdPost);

        return PostDetailResponseDto.builder()
            .id(savedPost.getId())
            .title(savedPost.getTitle())
            .content(savedPost.getContent())
            .anonymity(savedPost.isAnonymity())
            .likeCount(savedPost.getLikeCount())
            .hateCount(savedPost.getHateCount())
            .build();
    }

    public void updatePost(PostUpdateServiceDto serviceDto) {

        Post post = findPostById(serviceDto);

        validateMemberAuthority(serviceDto.getMemberId(), post.getMember().getId());

        post.update(serviceDto.getContent());
    }

    public void deletePost(PostDeleteServiceDto serviceDto) {

        Post post = postRepository.findById(serviceDto.getPostId())
            .orElseThrow(PostNotFoundException::new);

        validateMemberAuthority(serviceDto.getMemberId(), post.getMember().getId());

        post.delete(serviceDto.getDeletedAt());
    }

    private Post findPostById(PostUpdateServiceDto serviceDto) {
        return postRepository.findById(serviceDto.getPostId())
            .orElseThrow(PostNotFoundException::new);
    }

    private static void validateMemberAuthority(Long requesterId, Long writerId) {
        if (!requesterId.equals(writerId)) {
            throw new NotPostWriterException();
        }
    }
}
