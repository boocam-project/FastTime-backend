package com.fasttime.domain.member.service;

import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface EmailUseCase {

    String sendVerificationEmail(String to) throws MessagingException, UnsupportedEncodingException;

    boolean verifyEmailCode(String email, String code);
}
