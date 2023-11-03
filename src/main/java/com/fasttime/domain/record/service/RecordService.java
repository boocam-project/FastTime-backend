package com.fasttime.domain.record.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.service.ArticleCommandService;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleLikeOrHateServiceRequest;
import com.fasttime.domain.article.service.ArticleQueryService;
import com.fasttime.domain.record.dto.RecordDTO;
import com.fasttime.domain.record.dto.request.CreateRecordRequestDTO;
import com.fasttime.domain.record.dto.request.DeleteRecordRequestDTO;
import com.fasttime.domain.record.entity.Record;
import com.fasttime.domain.record.exception.AlreadyExistsRecordException;
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
    private final ArticleQueryService postQueryService;
    private final ArticleCommandService postCommandService;
    private final MemberService memberService;

    public void createRecord(CreateRecordRequestDTO createRecordRequestDTO, Long memberId) {
        ArticleResponse postResponse = postQueryService.queryById(
            createRecordRequestDTO.getPostId());
        Member member = memberService.getMember(memberId);
        checkDuplicateRecords(member.getId(), postResponse.getId(),
            createRecordRequestDTO.getIsLike());

        recordRepository.save(
            Record.builder().member(member).article(Article.builder().id(postResponse.getId()).build())
                .isLike(createRecordRequestDTO.getIsLike()).build());

        postCommandService.likeOrHate(new ArticleLikeOrHateServiceRequest(postResponse.getId(),
            createRecordRequestDTO.getIsLike(), true));
    }

    public RecordDTO getRecord(long memberId, long postId) {
        Optional<Record> record = recordRepository.findByMemberIdAndArticleId(memberId, postId);
        return record.map(Record::toDTO)
            .orElse(RecordDTO.builder().id(null).memberId(null).postId(null).isLike(null).build());
    }

    public void deleteRecord(DeleteRecordRequestDTO req, Long memberId) {
        Record record = recordRepository.findByMemberIdAndArticleId(memberId, req.getPostId())
            .orElseThrow(RecordNotFoundException::new);
        recordRepository.delete(record);
    }

    private void checkDuplicateRecords(long memberId, long postId, boolean isLike) {
        Optional<Record> record = recordRepository.findByMemberIdAndArticleId(memberId, postId);
        if (record.isPresent()) {
            if (record.get().isLike() == isLike) {
                throw new DuplicateRecordException();
            } else {
                throw new AlreadyExistsRecordException();
            }
        }
    }

    public Map<String, Integer> getRecordCount(long postId) {
        Optional<List<Record>> records = recordRepository.findAllByArticleId(postId);
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
