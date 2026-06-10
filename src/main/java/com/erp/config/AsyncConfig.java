package com.erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务线程池配置.
 *
 * @author AI
 * @since 2026-06-03
 */
@Configuration
public class AsyncConfig {

    /**
     * 日志异步写入专用线程池.
     *
     * <p>corePoolSize=2, maxPoolSize=5, queueCapacity=1000.
     * 队列满时使用 CallerRunsPolicy 降级, 由调用线程执行.</p>
     */
    @Bean("logAsyncExecutor")
    public Executor logAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("async-log-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
