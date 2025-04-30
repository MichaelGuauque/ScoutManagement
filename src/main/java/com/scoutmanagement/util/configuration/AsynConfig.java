package com.scoutmanagement.util.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsynConfig {

    @Bean(name = "mailTaskExecutor")
    public ThreadPoolTaskExecutor mailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);    // Hilos mínimos
        executor.setMaxPoolSize(10);    // Hilos máximos
        executor.setQueueCapacity(25);  // Cola de tareas
        executor.setThreadNamePrefix("mail-sender-");
        executor.initialize();
        return executor;
    }
}
