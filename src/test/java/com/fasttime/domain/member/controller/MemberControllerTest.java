package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 - Success")
    public void testJoinUser_Success() throws Exception {
        MemberDto userDto = new MemberDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        doNothing().when(memberService).save(any(MemberDto.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/new-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"test@example.com\", \"password\": \"password\"}")
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Join success"));
    }

    @Test
    @DisplayName("회원가입 - Duplicate Email")
    public void testJoinUser_DuplicateEmail() throws Exception {
        MemberDto userDto = new MemberDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        doThrow(DuplicateKeyException.class).when(memberService).save(any(MemberDto.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/new-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"test@example.com\", \"password\": \"password\"}")
            )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().string("Join failed: Email already exists"));
    }



}
