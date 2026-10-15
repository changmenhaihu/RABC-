package com.example.rabc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncPoolConfig {

    @Value("${async.pool.core-size:2}")
    private Integer coreSize;
    @Value("${async.pool.max-size:8}")
    private Integer maxSize;
    @Value("${async.pool.queue-capacity:200}")
    private Integer queueCapacity;

    @Bean("operateLogAsyncPool")
    public Executor operateLogAsyncPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("log-async-thread-");
        // CallerRunsPolicy:队列满时由调用线程执行，不丢任务 ，但可能短暂阻塞调用线程
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
