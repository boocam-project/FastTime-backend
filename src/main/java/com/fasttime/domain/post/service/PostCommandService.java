package com.fasttime.domain.post.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
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
public class PostCommandService implements PostCommandUseCase {

    private final MemberService memberService;
    private final PostRepository postRepository;

    public PostCommandService(MemberService memberService, PostRepository postRepository) {
        this.memberService = memberService;
        this.postRepository = postRepository;
    }

    @Override
    public Post writePost(PostCreateServiceDto serviceDto) {

        final Member writeMember = memberService.getMember(serviceDto.getMemberId());
        final Post createdPost = Post.createNewPost(writeMember, serviceDto.getTitle(),
            serviceDto.getContent(),
            false);

        return postRepository.save(createdPost);
    }

    @Override
    public PostResponseDto updatePost(PostUpdateServiceDto serviceDto) {

        final Member updateRequestMember = memberService.getMember(serviceDto.getMemberId());
        Post post = findPostById(serviceDto);

        validateAuthority(updateRequestMember, post);

        post.update(serviceDto.getContent());

        return PostDetailResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .anonymity(post.isAnonymity())
            .likeCount(post.getLikeCount())
            .hateCount(post.getHateCount())
            .build();
    }

    @Override
    public void deletePost(PostDeleteServiceDto serviceDto) {

        final Member deleteRequestMember = memberService.getMember(serviceDto.getMemberId());
        final Post post = postRepository.findById(serviceDto.getPostId())
            .orElseThrow(PostNotFoundException::new);

        validateAuthority(deleteRequestMember, post);

        post.delete(serviceDto.getDeletedAt());
    }

    private Post findPostById(PostUpdateServiceDto serviceDto) {
        return postRepository.findById(serviceDto.getPostId())
            .orElseThrow(PostNotFoundException::new);
    }

    private void validateAuthority(Member requestUser, Post post) {
        isAdmin(requestUser);
        isWriter(requestUser, post);
    }

    private static void isAdmin(Member targetUser) {
        // TODO Admin 정보를 가져와 Admin 유저인지 확인해야 함.
    }

    private void isWriter(Member targetUser, Post post) {
        if (!targetUser.getId().equals(post.getMember().getId())) {
            throw new NotPostWriterException();
        }
    }

}
