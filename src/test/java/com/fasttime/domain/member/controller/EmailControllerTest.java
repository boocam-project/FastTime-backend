package com.fasttime.domain.member.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasttime.domain.member.service.EmailService;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Test
    @DisplayName("Send Mail Confirmation Code")
    public void testSendMailConfirmationCode() throws Exception {
        String email = "test@example.com";
        String code = "123456"; // Sample confirmation code

        when(emailService.sendSimpleMessage(email)).thenReturn(code);

        mockMvc.perform(post("/emailconfirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"" + email + "\"}")
            )
            .andExpect(status().isOk())
            .andExpect(content().string("success"));

        // You can add more assertions or verifications as needed
    }

    @Test
    @DisplayName("Verify Email - Success")
    public void testVerifyEmail_Success() throws Exception {
        String code = "123456"; // Sample confirmation code
        HttpSession session = mockMvc.perform(get("/verify/" + code))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andReturn()
            .getRequest()
            .getSession();

        // You can add more assertions or verifications on the session as needed
    }

    @Test
    @DisplayName("Verify Email - Failure")
    public void testVerifyEmail_Failure() throws Exception {
        String code = "wrongcode"; // Invalid confirmation code
        HttpSession session = mockMvc.perform(get("/verify/" + code))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andReturn()
            .getRequest()
            .getSession();

        // You can add more assertions or verifications on the session as needed
    }
}
