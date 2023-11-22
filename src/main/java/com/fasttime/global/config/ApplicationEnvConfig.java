package com.fasttime.global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Deprecated(since = "2023-11-22", forRemoval = false)
public class ApplicationEnvConfig {

    private final Environment env;

    @Autowired
    public ApplicationEnvConfig(Environment env) {
        this.env = env;
    }
}
