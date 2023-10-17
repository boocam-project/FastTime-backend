package com.fasttime.domain.record.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.post.dto.service.response.PostDetailResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.service.PostQueryService;
import com.fasttime.domain.record.dto.RecordDTO;
import com.fasttime.domain.record.dto.request.CreateRecordRequestDTO;
import com.fasttime.domain.record.dto.request.DeleteRecordRequestDTO;
import com.fasttime.domain.record.entity.Record;
import com.fasttime.domain.record.exception.DuplicateRecordException;
import com.fasttime.domain.record.exception.RecordNotFoundException;
import com.fasttime.domain.record.repository.RecordRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final PostQueryService postQueryService;
    private final MemberService memberService;

    public void createRecord(CreateRecordRequestDTO req, boolean isLike) {
        PostDetailResponseDto postResponse = postQueryService.findById(req.getPostId());
        Member member = memberService.getMember(req.getMemberId());
        checkDuplicateRecord(member.getId(), postResponse.getId(), isLike);
        recordRepository.save(
            Record.builder().member(member).post(Post.builder().id(postResponse.getId()).build())
                .isLike(isLike).build());
    }

    public RecordDTO getRecord(long memberId, long postId) {
        Optional<Record> record = recordRepository.findByMemberIdAndPostId(memberId, postId);
        return record.map(Record::toDTO)
            .orElse(RecordDTO.builder().id(null).memberId(null).postId(null).isLike(null).build());
    }

    public void deleteRecord(DeleteRecordRequestDTO req) {
        Record record = recordRepository.findByMemberIdAndPostId(req.getMemberId(), req.getPostId())
            .orElseThrow(RecordNotFoundException::new);
        recordRepository.delete(record);
    }

    private void checkDuplicateRecord(long memberId, long postId, boolean isLike) {
        Optional<Record> record = recordRepository.findByMemberIdAndPostId(memberId, postId);
        if (record.isPresent()) {
            if (record.get().isLike() == isLike) {
                throw new DuplicateRecordException("중복된 요청입니다.");
            } else {
                throw new DuplicateRecordException("한 게시글에 대해 좋아요와 싫어요를 모두 할 수는 없습니다.");
            }
        }
    }

    public Map<String, Integer> getRecordCount(long postId) {
        Optional<List<Record>> records = recordRepository.findAllByPostId(postId);
        Map<String, Integer> recordCount = new HashMap<>();
        if (records.isPresent()) {
            for (Record record : records.get()) {
                if (record.isLike()) {
                    recordCount.put("likeCount", recordCount.getOrDefault("likeCount", 0) + 1);
                } else {
                    recordCount.put("hateCount", recordCount.getOrDefault("hateCount", 0) + 1);
                }
            }
        } else {
            recordCount.put("likeCount", 0);
            recordCount.put("hateCount", 0);
        }
        return recordCount;
    }
}
