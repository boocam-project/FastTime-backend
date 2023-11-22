package com.fasttime.global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Deprecated(since = "2023-11-22", forRemoval = false)
public class ApplcationEnvConfig {
    private final Environment env;

    @Autowired
    public ApplcationEnvConfig(Environment env) {
        this.env = env;
    }
}
