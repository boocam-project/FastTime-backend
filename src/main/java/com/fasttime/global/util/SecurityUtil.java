package com.fasttime.global.util;

import com.fasttime.domain.member.exception.AuthenticationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    public Long getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AuthenticationNotFoundException();
        }
        return Long.parseLong(authentication.getName());
    }
}
