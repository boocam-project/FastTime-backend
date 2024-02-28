package com.fasttime.global.discord;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${discord.hook-name}", url = "${discord.url}")
public interface DiscordFeignClient {

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  void sendMessage(@RequestBody DiscordMessage message);
}