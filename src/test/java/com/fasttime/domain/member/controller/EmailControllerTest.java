package com.fasttime.domain.member.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasttime.domain.member.request.EmailRequest;
import com.fasttime.domain.member.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(EmailController.class)
public class EmailControllerTest {
    //MockMvc를 사용해 API 요청을 모방. MockBean을 사용해 EmailService 모킹하여 테스트 진행

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Test
    @DisplayName("이메일 확인 API 테스트")
    public void testMailConfirm() throws Exception {
        String email = "test@example.com";

        when(emailService.sendSimpleMessage(anyString())).thenReturn("123456");//인증번호 고정

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/emailconfirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"" + email + "\"}"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("success"));

        // sendSimpleMessage호출여부 확인
        verify(emailService).sendSimpleMessage(email);
    }

    @Test
    @DisplayName("이메일 인증 코드를 확인한다.")
    public void Element_Email() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("emailCode", "123456");

        //발송된 이메일 인증번호와 입력된 인증번호가 일치할 때
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/verify/123456")
                .session(session))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));

        //발송된 이메일 인증번호와 입력된 인증번호가 일치하지 않을 때
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/verify/111111")
                .session(session))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false));
    }
}
