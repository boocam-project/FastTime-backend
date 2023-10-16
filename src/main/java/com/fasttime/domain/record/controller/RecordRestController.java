package com.fasttime.domain.record.controller;

import com.fasttime.domain.record.dto.RecordDTO;
import com.fasttime.domain.record.dto.request.CreateRecordRequestDTO;
import com.fasttime.domain.record.dto.request.DeleteRecordRequestDTO;
import com.fasttime.domain.record.service.RecordService;
import com.fasttime.global.util.ResponseDTO;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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

    @PostMapping("/like")
    public ResponseEntity<ResponseDTO<Void>> createLike(
        @Valid @RequestBody CreateRecordRequestDTO createRecordRequestDTO) {
        log.info("CreateRecordRequest: " + createRecordRequestDTO + "(like)");
        recordService.createRecord(createRecordRequestDTO, true);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED, "좋아요를 성공적으로 접수했습니다.", null));
    }

    @PostMapping("/hate")
    public ResponseEntity<ResponseDTO<RecordDTO>> createHate(
        @Valid @RequestBody CreateRecordRequestDTO createRecordRequestDTO) {
        log.info("CreateRecordRequest: " + createRecordRequestDTO + "(hate)");
        recordService.createRecord(createRecordRequestDTO, false);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED, "싫어요를 성공적으로 접수했습니다.", null));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseDTO<RecordDTO>> getRecord(@PathVariable long postId,
        HttpSession session) {
        long memberId = (long) session.getAttribute("MEMBER");
        log.info("getRecord: " + postId + "(postId) + " + memberId + "(memberId)");
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "좋아요/싫어요를 성공적으로 조회했습니다.",
                recordService.getRecord(memberId, postId)));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<Void>> deleteRecord(
        @Valid @RequestBody DeleteRecordRequestDTO deleteRecordRequestDTO) {
        log.info("DeleteRecordRequest: " + deleteRecordRequestDTO);
        recordService.deleteRecord(deleteRecordRequestDTO);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "좋아요/싫어요를 성공적으로 취소했습니다.", null));
    }
}
