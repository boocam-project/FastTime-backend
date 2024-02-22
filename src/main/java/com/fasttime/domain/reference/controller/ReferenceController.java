package com.fasttime.domain.reference.controller;

import com.fasttime.domain.reference.dto.request.ReferencePageRequestDto;
import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.dto.response.ActivityPageResponseDto;
import com.fasttime.domain.reference.dto.response.ActivityResponseDto;
import com.fasttime.domain.reference.dto.response.CompetitionPageResponseDto;
import com.fasttime.domain.reference.service.usecase.ReferenceServiceUseCase;
import com.fasttime.global.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class ReferenceController {

    private final ReferenceServiceUseCase referenceServiceUseCase;

    @GetMapping("/activities")
    public ResponseEntity<ResponseDTO<ActivityPageResponseDto>> searchActivities(
        @RequestParam(required = false, name = "keyword") String keyword,
        @RequestParam(required = false, defaultValue = "true", name = "before") boolean before,
        @RequestParam(required = false, defaultValue = "true", name = "during") boolean during,
        @RequestParam(required = false, defaultValue = "false", name = "closed") boolean closed,
        @RequestParam(required = false, name = "orderBy") String orderBy,
        @RequestParam(defaultValue = "0", name = "page") int page,
        @RequestParam(defaultValue = "10", name = "pageSize") int pageSize) {
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK,
                "대외활동 목록을 성공적으로 조회했습니다.",
                referenceServiceUseCase.searchActivities(
                    ReferenceSearchRequestDto.builder()
                        .keyword(keyword)
                        .before(before)
                        .during(during)
                        .closed(closed)
                        .build(),
                    ReferencePageRequestDto.builder()
                        .orderBy(orderBy)
                        .page(page)
                        .pageSize(pageSize)
                        .build().toPageable()
                )
            )
        );
    }

    @GetMapping("/competitions")
    public ResponseEntity<ResponseDTO<CompetitionPageResponseDto>> searchCompetitions(
        @RequestParam(required = false, name = "keyword") String keyword,
        @RequestParam(required = false, defaultValue = "true", name = "before") boolean before,
        @RequestParam(required = false, defaultValue = "true", name = "during") boolean during,
        @RequestParam(required = false, defaultValue = "false", name = "closed") boolean closed,
        @RequestParam(required = false, name = "orderBy") String orderBy,
        @RequestParam(defaultValue = "0", name = "page") int page,
        @RequestParam(defaultValue = "10", name = "pageSize") int pageSize) {
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK,
                "공모전 목록을 성공적으로 조회했습니다.",
                referenceServiceUseCase.searchCompetitions(
                    ReferenceSearchRequestDto.builder()
                        .keyword(keyword)
                        .before(before)
                        .during(during)
                        .closed(closed)
                        .build(),
                    ReferencePageRequestDto.builder()
                        .orderBy(orderBy)
                        .page(page)
                        .pageSize(pageSize)
                        .build().toPageable()
                )
            )
        );
    }

    @GetMapping("/activities/{activityId}")
    public ResponseEntity<ResponseDTO<ActivityResponseDto>> searchActivities(
        @PathVariable(name = "activityId") int activityId) {
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK,
                "대외활동 상세 정보를 성공적으로 조회했습니다.",
                referenceServiceUseCase.getActivity(activityId)
            )
        );
    }
}
