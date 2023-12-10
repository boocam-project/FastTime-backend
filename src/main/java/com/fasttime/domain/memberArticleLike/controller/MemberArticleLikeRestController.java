package com.fasttime.domain.memberArticleLike.controller;

import com.fasttime.domain.memberArticleLike.dto.MemberArticleLikeDTO;
import com.fasttime.domain.memberArticleLike.dto.request.CreateMemberArticleLikeRequestDTO;
import com.fasttime.domain.memberArticleLike.dto.request.DeleteMemberArticleLikeRequestDTO;
import com.fasttime.domain.memberArticleLike.service.MemberArticleLikeService;
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

/**
 * REST 컨트롤러 클래스로, 좋아요/싫어요 기능을 처리한다.
 *
 * @author chimaek,JeongUiJeong
 * @apiNote 이 클래스는 사용자의 좋아요/싫어요 요청을 생성, 조회, 삭제하는 API 엔드포인트를 제공한다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article-like")
public class MemberArticleLikeRestController {

  private final MemberArticleLikeService memberArticleLikeService;
  private final SecurityUtil securityUtil;

  /**
   * @apiNote 이 메서드는 사용자의 좋아요/싫어요 요청을 생성한다.
   * @param createMemberArticleLikeRequestDTO 좋아요/싫어요 생성 요청 데이터
   * @return 생성된 좋아요/싫어요 상태에 대한 응답
   */
  @PostMapping
  public ResponseEntity<ResponseDTO<Object>> createLike(
      @Valid @RequestBody CreateMemberArticleLikeRequestDTO createMemberArticleLikeRequestDTO) {
    log.info("CreateRecordRequest: " + createMemberArticleLikeRequestDTO);
    Long memberId = this.securityUtil.getCurrentMemberId();
    memberArticleLikeService.createRecord(createMemberArticleLikeRequestDTO, memberId);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ResponseDTO.res(HttpStatus.CREATED, "좋아요/싫어요를 성공적으로 등록했습니다."));
  }

  /**
   * @apiNote 이 메서드는 사용자의 좋아요/싫어요 데이터를 조회한다.
   * @param postId 조회할 게시글의 ID
   * @return 조회된 좋아요/싫어요 상태에 대한 응답
   */
  @GetMapping("/{postId}")
  public ResponseEntity<ResponseDTO<MemberArticleLikeDTO>> getRecord(@PathVariable long postId) {
    Long memberId = this.securityUtil.getCurrentMemberId();
    log.info("getRecordRequest: postId(" + postId + "), memberId(" + memberId + ")");
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ResponseDTO.res(
                HttpStatus.OK,
                "좋아요/싫어요를 성공적으로 조회했습니다.",
                memberArticleLikeService.getRecord(memberId, postId)));
  }

  /**
   * @apiNote 이 메서드는 사용자의 좋아요/싫어요 요청을 삭제한다.
   * @param deleteMemberArticleLikeRequestDTO 좋아요/싫어요 삭제 요청 데이터
   * @return 삭제된 좋아요/싫어요 상태에 대한 응답
   */
  @DeleteMapping
  public ResponseEntity<ResponseDTO<Object>> deleteRecord(
      @Valid @RequestBody DeleteMemberArticleLikeRequestDTO deleteMemberArticleLikeRequestDTO) {
    Long memberId = this.securityUtil.getCurrentMemberId();
    log.info(
        "DeleteRecordRequest: postId("
            + deleteMemberArticleLikeRequestDTO.getPostId()
            + "), memberId("
            + memberId
            + ")");
    memberArticleLikeService.deleteRecord(deleteMemberArticleLikeRequestDTO, memberId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ResponseDTO.res(HttpStatus.OK, "좋아요/싫어요를 성공적으로 취소했습니다."));
  }
}
