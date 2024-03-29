package com.fasttime.domain.report.contoller;

import com.fasttime.domain.report.dto.request.CreateReportRequestDTO;
import com.fasttime.domain.report.service.ReportService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportRestController {

    private final ReportService reportService;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<ResponseDTO<Object>> createReport(
        @Valid @RequestBody CreateReportRequestDTO createReportRequestDTO) {
        log.info("CreateReportRequest: " + createReportRequestDTO);
        reportService.createReport(createReportRequestDTO, securityUtil.getCurrentMemberId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED, "신고를 성공적으로 접수했습니다."));
    }
}
