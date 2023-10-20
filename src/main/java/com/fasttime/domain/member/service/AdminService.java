package com.fasttime.domain.member.service;

import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.dto.request.LoginRequestDTO;
import com.fasttime.domain.member.dto.request.saveAdminDTO;
import com.fasttime.domain.member.entity.Admin;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.AdminNotFoundException;
import com.fasttime.domain.member.exception.UserNotFoundException;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;




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
            throw new AdminNotFoundException("Admin not found ");
        }
        return byMember.get().getId();
    }

    public List<PostsResponseDto> findReportedPost() {
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
