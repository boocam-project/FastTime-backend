package com.fasttime.domain.record.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.service.ArticleCommandService;
import com.fasttime.domain.article.service.ArticleQueryService;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleLikeOrHateServiceRequest;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.record.dto.RecordDTO;
import com.fasttime.domain.record.dto.request.CreateRecordRequestDTO;
import com.fasttime.domain.record.dto.request.DeleteRecordRequestDTO;
import com.fasttime.domain.record.entity.Record;
import com.fasttime.domain.record.exception.AlreadyExistsRecordException;
import com.fasttime.domain.record.exception.DuplicateRecordException;
import com.fasttime.domain.record.exception.RecordNotFoundException;
import com.fasttime.domain.record.repository.RecordRepository;
import com.fasttime.domain.record.service.RecordService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Transactional
@ExtendWith(MockitoExtension.class)
public class RecordServiceTest {

    @InjectMocks
    private RecordService recordService;

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private ArticleQueryService articleQueryService;

    @Mock
    private ArticleCommandService articleCommandService;

    @Mock
    private MemberService memberService;

    @Nested
    @DisplayName("createRecord()는 ")
    class Context_createRecord {

        @Test
        @DisplayName("게시글을 좋아요 할 수 있다.")
        void like_willSuccess() {
            // given
            CreateRecordRequestDTO request = CreateRecordRequestDTO.builder().articleId(1L)
                .isLike(true).build();
            ArticleResponse article = ArticleResponse.builder().id(1L).build();
            Member member = Member.builder().id(1L).build();
            Optional<Record> record = Optional.empty();

            given(articleQueryService.queryById(any(Long.class))).willReturn(article);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(recordRepository.findByMemberIdAndArticleId(any(long.class),
                any(long.class))).willReturn(record);

            // when
            recordService.createRecord(request, 1L);

            // then
            verify(recordRepository, times(1)).save(any(Record.class));
            verify(articleCommandService, times(1)).likeOrHate(any(ArticleLikeOrHateServiceRequest.class));
        }

        @Test
        @DisplayName("게시글을 싫어요 할 수 있다.")
        void hate_willSuccess() {
            // given
            CreateRecordRequestDTO request = CreateRecordRequestDTO.builder()
                .articleId(1L).isLike(false).build();
            ArticleResponse article = ArticleResponse.builder().id(1L).build();
            Member member = Member.builder().id(1L).build();
            Optional<Record> record = Optional.empty();

            given(articleQueryService.queryById(any(Long.class))).willReturn(article);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(recordRepository.findByMemberIdAndArticleId(any(long.class),
                any(long.class))).willReturn(record);

            // when
            recordService.createRecord(request, 1L);

            // then
            verify(recordRepository, times(1)).save(any(Record.class));
            verify(articleCommandService, times(1)).likeOrHate(any(ArticleLikeOrHateServiceRequest.class));
        }

        @Test
        @DisplayName("이미 좋아요(싫어요)를 한 게시글에 다시 좋아요(싫어요)를 등록할 수 없다.")
        void duplicateRecord1_willFail() {
            // given
            CreateRecordRequestDTO request = CreateRecordRequestDTO.builder()
                .articleId(1L).isLike(true).build();
            ArticleResponse article = ArticleResponse.builder().id(1L).build();
            Member member = Member.builder().id(1L).build();
            Optional<Record> record = Optional.of(
                Record.builder().member(member).article(Article.builder().id(article.id()).build())
                    .isLike(true).build());

            given(articleQueryService.queryById(any(Long.class))).willReturn(article);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(recordRepository.findByMemberIdAndArticleId(any(long.class),
                any(long.class))).willReturn(record);

            // when, then
            Throwable exception = assertThrows(DuplicateRecordException.class, () -> {
                recordService.createRecord(request, 1L);
            });
            assertEquals("중복된 좋아요/싫어요 등록 요청입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("좋아요와 싫어요를 중복으로 등록할 수 없다.")
        void duplicateRecord2_willFail() {
            // given
            CreateRecordRequestDTO request = CreateRecordRequestDTO.builder()
                .articleId(1L).isLike(true).build();
            ArticleResponse article = ArticleResponse.builder().id(1L).build();
            Member member = Member.builder().id(1L).build();
            Optional<Record> record = Optional.of(
                Record.builder().member(member).article(Article.builder().id(article.id()).build())
                    .isLike(false).build());

            given(articleQueryService.queryById(any(Long.class))).willReturn(article);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(recordRepository.findByMemberIdAndArticleId(any(long.class),
                any(long.class))).willReturn(record);

            // when, then
            Throwable exception = assertThrows(AlreadyExistsRecordException.class, () -> {
                recordService.createRecord(request, 1L);
            });
            assertEquals("한 게시글에 좋아요와 싫어요를 모두 등록할 수는 없습니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("getRecord()는 ")
    class Context_getRecord {

        @Test
        @DisplayName("회원이 해당 게시물에 대해 등록한 좋아요/싫어요 내역을 가져올 수 있다.")
        void _willSuccess() {
            // given
            Article article = Article.builder().id(1L).build();
            Member member = Member.builder().id(1L).build();
            Optional<Record> record = Optional.of(
                Record.builder().id(1L).article(article).member(member).isLike(true).build());
            given(recordRepository.findByMemberIdAndArticleId(any(Long.class),
                any(Long.class))).willReturn(record);

            // when
            RecordDTO result = recordService.getRecord(1L, 1L);

            // then
            assertThat(result).extracting("id", "articleId", "memberId", "isLike")
                .containsExactly(1L, 1L, 1L, true);
            verify(recordRepository, times(1)).findByMemberIdAndArticleId(any(Long.class),
                any(Long.class));
        }

        @Test
        @DisplayName("회원이 해당 게시물에 대해 등록한 좋아요/싫어요 데이터가 없다면 id값이 null인 ResponseDTO를 반환한다.")
        void noRecord_willSuccess() {
            // given
            Optional<Record> record = Optional.empty();
            given(recordRepository.findByMemberIdAndArticleId(any(Long.class),
                any(Long.class))).willReturn(record);

            // when
            RecordDTO result = recordService.getRecord(1L, 1L);

            // then
            assertThat(result).extracting("id", "articleId", "memberId", "isLike")
                .containsExactly(null, null, null, null);
            verify(recordRepository, times(1)).findByMemberIdAndArticleId(any(Long.class),
                any(Long.class));
        }
    }

    @Nested
    @DisplayName("deleteRecord()는 ")
    class Context_deleteRecord {

        @Test
        @DisplayName("게시글 좋아요/싫어요를 취소할 수 있다.")
        void _willSuccess() {
            // given
            DeleteRecordRequestDTO request = DeleteRecordRequestDTO.builder()
                .articleId(1L).build();
            Article article = Article.builder().id(1L).build();
            Member member = Member.builder().id(1L).build();
            Optional<Record> record = Optional.of(
                Record.builder().id(1L).member(member).article(article).isLike(true).build());
            given(recordRepository.findByMemberIdAndArticleId(any(long.class),
                any(long.class))).willReturn(record);

            // when
            recordService.deleteRecord(request, 1L);

            // then
            verify(recordRepository, times(1)).delete(any(Record.class));
        }

        @Test
        @DisplayName("게시글 좋아요/싫어요 한 적이 없다면 좋아요/싫어요를 취소할 수 없다.")
        void recordNotFound_willSuccess() {
            // given
            DeleteRecordRequestDTO request = DeleteRecordRequestDTO.builder()
                .articleId(1L).build();
            Optional<Record> record = Optional.empty();
            given(recordRepository.findByMemberIdAndArticleId(any(long.class),
                any(long.class))).willReturn(record);

            // when, then
            Throwable exception = assertThrows(RecordNotFoundException.class, () -> {
                recordService.deleteRecord(request, 1L);
            });
            assertEquals("존재하지 않는 좋아요/싫어요 입니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("getRecordCount()는 ")
    class Context_getRecordCount {

        @Test
        @DisplayName("게시물에 등록된 좋아요/싫어요 합계를 가져올 수 있다.")
        void _willSuccess() {
            // given
            Article article = Article.builder().id(1L).build();
            Member member = Member.builder().id(1L).build();
            List<Record> list = new ArrayList<>();
            list.add(Record.builder().id(1L).article(article).member(member).isLike(true).build());
            list.add(Record.builder().id(2L).article(article).member(member).isLike(true).build());
            list.add(Record.builder().id(3L).article(article).member(member).isLike(false).build());
            Optional<List<Record>> records = Optional.of(list);
            given(recordRepository.findAllByArticleId(any(Long.class))).willReturn(records);

            // when
            Map<String, Integer> result = recordService.getRecordCount(1L);

            // then
            assertThat(result).extracting("likeCount", "hateCount").containsExactly(2, 1);
            verify(recordRepository, times(1)).findAllByArticleId(any(Long.class));
        }
    }
}
