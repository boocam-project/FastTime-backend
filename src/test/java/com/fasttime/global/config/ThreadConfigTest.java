package com.fasttime.global.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ContextConfiguration(classes = ThreadConfig.class)
@TestPropertySource(properties = {"spring.thread-executor=virtual"})
public class ThreadConfigTest {

  @Qualifier("applicationTaskExecutor")
  @Autowired
  private AsyncTaskExecutor taskExecutor;

  @Test
  public void testAsyncTaskExecutor() throws ExecutionException, InterruptedException {
    assertNotNull(taskExecutor, "AsyncTaskExecutor should not be null");
    Future<Boolean> future =
        taskExecutor.submit(
            () -> {
              return Thread.currentThread().isVirtual();
            });

    boolean isVirtualThread = future.get();
    assertThat(isVirtualThread).isTrue();
  }

}
