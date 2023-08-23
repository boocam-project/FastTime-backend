package com.fasttime.domain.member.service;

import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.entity.ReportStatus;
import com.fasttime.domain.post.repository.PostRepository;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final PostRepository postRepository;

    public List<Post> FindReportedPost(){
        return postRepository.findAllByReportStatus(ReportStatus.REPORTED);
    }

    public Post FindOneReportedPost(Long id) {
        return postRepository.findById(id)
            .orElseThrow(()-> new IllegalArgumentException("게시글이 없습니다."));
    }
    public void DeletePost(Long id) {
        postRepository.delete(postRepository.findById(id).
            orElseThrow(()-> new IllegalArgumentException("게시글이 없습니다.")));
    }
    public void PassPost(Long id){
        Post post = postRepository.findById(id).
            orElseThrow(()-> new IllegalArgumentException("게시글이 없습니다."));
        post.ChangeReportStatus(ReportStatus.REPORTE_ABORTED);
    }

}
