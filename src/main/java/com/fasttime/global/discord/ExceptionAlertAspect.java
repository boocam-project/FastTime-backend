package com.fasttime.global.discord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class ExceptionAlertAspect {
  private final DiscordFeignClient discordFeignClient;

  public void sendAlert(String message) {
    discordFeignClient.sendMessage(DiscordMessage.createDiscordMessage(message));
  }

  @AfterThrowing(
      pointcut =
          "within(@org.springframework.web.bind.annotation.RestController *)",
      throwing = "e")
  public void sendExceptionAlert(JoinPoint joinPoint, Exception e) {
    log.error("예외 발생!", e);
    String alertMessage =
        String.format(
            "예외 발생! 클래스: %s, 메서드: %s, 메시지: %s",
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(),
            e.getMessage());
    sendAlert(alertMessage);
  }
}