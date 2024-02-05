package com.msr.rnip.reconciliation.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

//@author p4r53c
@Configuration
@EnableAsync
public class ReconciliationConfig {

    @Bean(name = "reconciliationThreadPoolTaskExecutor")
    public Executor reconciliationThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        //executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("reconciliationThread-");
        executor.initialize();
        return executor;
    }

}
