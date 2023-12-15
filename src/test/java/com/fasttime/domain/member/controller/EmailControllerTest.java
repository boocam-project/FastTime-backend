package com.fasttime.domain.member.controller;

import com.fasttime.util.ControllerUnitTestSupporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class EmailControllerTest extends ControllerUnitTestSupporter {

    @Nested
    @DisplayName("이메일 확인 API 테스트")
    class testMailConfirm {

//        @Test
//        @DisplayName("성공한다. : 등록된 이메일")
//        public void testMailConfirm_RegisteredEmail() throws Exception {
//            String email = "test@example.com";
//
//            when(memberService.isEmailExistsInFcmember(anyString())).thenReturn(true);
//            when(emailService.sendSimpleMessage(anyString())).thenReturn("123456");
//
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/emailconfirm")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content("{\"email\": \"" + email + "\"}"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string("success"));
//
//            verify(emailService).sendSimpleMessage(email);
//        }
//
//        @Test
//        @DisplayName("실패한다. : 등록되지 않은 이메일")
//        public void testMailConfirm_UnregisteredEmail() throws Exception {
//            String email = "unregistered@example.com";
//
//            when(memberService.isEmailExistsInFcmember(anyString())).thenReturn(
//                false);
//
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/emailconfirm")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content("{\"email\": \"" + email + "\"}"))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.content().string("FastCampus에 등록된 이메일이 아닙니다."));
//
//            verify(emailService, never()).sendSimpleMessage(email);
//        }
    }


    @Test
    @DisplayName("이메일 인증 코드를 확인한다.")
    void Element_Email() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("emailCode", "123456");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/verify/123456")
                .session(session))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/verify/111111")
                .session(session))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false));
    }
}
