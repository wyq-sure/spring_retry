package com.example.demo.config;

import com.example.demo.demain.ThreadProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wangyq
 * @date 2021/10/13 17:07
 * @Description 线程池配置类
 */
@Configuration
@Slf4j
public class AsyncThreadPoolConfig implements AsyncConfigurer {

    @Resource
    ThreadProperties threadProperties;

    @Bean(name = "asyncExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        log.info("测试输出从配置文件中获取的核心数:{}", threadProperties.getCorePoolSize());
        executor.setCorePoolSize(threadProperties.getCorePoolSize());
        // 线程池最大线程数
        executor.setMaxPoolSize(threadProperties.getMaxPoolSize());
        // 设置队列大小
        executor.setQueueCapacity(threadProperties.getQueueCapacity());
        // 线程池中线程最大空闲存活时间
        executor.setKeepAliveSeconds(threadProperties.getKeepAliveSeconds());
        //核心线程是否允许超时，默认:false
        executor.setAllowCoreThreadTimeOut(false);
        // 线程名称前缀
        executor.setThreadNamePrefix(threadProperties.getThreadNamePrefix());
        // IOC容器关闭时是否阻塞等待剩余的任务执行完成，默认:false（true则必须设置setAwaitTerminationSeconds）
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 阻塞IOC容器关闭的时间
        executor.setAwaitTerminationSeconds(30);
        // 这里的拒绝策略是将交给主线程去处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
