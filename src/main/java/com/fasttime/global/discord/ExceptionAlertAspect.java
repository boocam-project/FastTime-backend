package com.fasttime.global.discord;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class ExceptionAlertAspect {
  private final DiscordFeignClient discordFeignClient;

  @Value("${spring.profiles.active}")
  private String serverType;

  public void sendAlert(String message) {
    discordFeignClient.sendMessage(DiscordMessage.createErrorDiscordMessage(message));
  }

  @AfterThrowing(
      pointcut = "within(@org.springframework.web.bind.annotation.RestController *)",
      throwing = "e")
  public void sendExceptionAlert(JoinPoint joinPoint, Exception e) {
    String alertMessage =
        String.format(
            "server-profile = `%s` 예외 발생! \n 클래스:` %s ` \n 메서드:` %s ` \n 메시지:` %s `",
            serverType,
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(),
            e.getMessage());
    sendAlert(alertMessage);
  }
}