package com.fasttime.domain.member.unit.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.domain.member.dto.request.EmailRequest;
import com.fasttime.util.ControllerUnitTestSupporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class EmailControllerTest extends ControllerUnitTestSupporter {

    @Nested
    @DisplayName("이메일 인증코드 전송 API 테스트")
    class testMailConfirm {

        @Test
        @DisplayName("성공한다. : 등록된 이메일")
        void testMailConfirm_RegisteredEmail() throws Exception {
            String email = "test@example.com";
            EmailRequest request = new EmailRequest(email);

            when(emailUseCase.sendVerificationEmail(anyString())).thenReturn("123456");

            mockMvc.perform(post("/api/v1/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk());

            verify(emailUseCase).sendVerificationEmail(email);
        }

        @Test
        @DisplayName("실패한다. : 이메일 형식이 아닌 경우")
        void requestEmail_isNotEmailFormat_will400Error() throws Exception {
            String email = "this_is_not_email_format_request";
            EmailRequest request = new EmailRequest(email);

            mockMvc.perform(post("/api/v1/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());

            verify(emailUseCase, never()).sendVerificationEmail(email);
        }
    }

    @Nested
    @DisplayName("이메일 인증코드 전송 API 테스트")
    class testEmailVerify {

        @Test
        @DisplayName("이메일 인증 코드를 확인한다.")
        void server_hasVerificationCode_will200() throws Exception {
            // given
            String email = "test@email.com";
            when(emailUseCase.verifyEmailCode(anyString(), anyString())).thenReturn(true);

            // when then
            mockMvc.perform(get("/api/v1/verify/{code}", 12345)
                    .queryParam("email", email))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("이메일 인증 코드가 일치하지 않으면 실패한다.")
        void server_doesntHave_verificationCode_will200() throws Exception {

            // given
            String email = "test@email.com";
            when(emailUseCase.verifyEmailCode(anyString(), anyString())).thenReturn(false);

            // when then
            mockMvc.perform(get("/api/v1/verify/{code}", 11111)
                    .queryParam("email", email))
                .andExpect(status().isBadRequest());
        }
    }
}
