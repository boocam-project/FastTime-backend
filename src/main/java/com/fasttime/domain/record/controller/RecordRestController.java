package com.fasttime.domain.record.controller;

import com.fasttime.domain.record.dto.RecordDTO;
import com.fasttime.domain.record.dto.request.CreateRecordRequestDTO;
import com.fasttime.domain.record.dto.request.DeleteRecordRequestDTO;
import com.fasttime.domain.record.service.RecordService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/record")
public class RecordRestController {

    private final RecordService recordService;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<ResponseDTO<Object>> createLike(
        @Valid @RequestBody CreateRecordRequestDTO createRecordRequestDTO) {
        log.info("CreateRecordRequest: " + createRecordRequestDTO);
        recordService.createRecord(createRecordRequestDTO, securityUtil.getCurrentMemberId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED, "좋아요/싫어요를 성공적으로 등록했습니다."));
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ResponseDTO<RecordDTO>> getRecord(@PathVariable long articleId) {
        Long memberId = securityUtil.getCurrentMemberId();
        log.info("getRecordRequest: articleId(" + articleId + "), memberId(" + memberId + ")");
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "좋아요/싫어요를 성공적으로 조회했습니다.",
                recordService.getRecord(memberId, articleId)));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<Object>> deleteRecord(
        @Valid @RequestBody DeleteRecordRequestDTO deleteRecordRequestDTO) {
        Long memberId = securityUtil.getCurrentMemberId();
        log.info(
            "DeleteRecordRequest: articleId(" + deleteRecordRequestDTO.getArticleId() + "), memberId("
                + memberId + ")");
        recordService.deleteRecord(deleteRecordRequestDTO, memberId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "좋아요/싫어요를 성공적으로 취소했습니다."));
    }
}
