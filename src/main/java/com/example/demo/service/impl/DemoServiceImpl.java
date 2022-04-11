package com.example.demo.service.impl;

import com.example.demo.constant.RetryConstant;
import com.example.demo.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wangyq
 * @date 2022年04月10日 21:08
 * @Description 基于Spring-retry注解尝试重试
 */
@Service("DemoServiceImpl")
@Slf4j
public class DemoServiceImpl implements DemoService {

    private static AtomicLong helloTimes = new AtomicLong();

    /**
     * @author wangyq
     * @date 2022/4/10 21:41
     * @Description 配置元数据情况：
     * 最大重试次数为5
     * 第一次重试延迟2s
     * 每次重试时间间隔是前一次2倍
     * Exception类异常情况下重试
     * @return 方法是否运行成功
     * 使用异步线程池的方式处理
     */
    @Override
    @Retryable(value = Exception.class,maxAttempts = 4  , backoff = @Backoff(delay = 2000, multiplier = 6))
    @Async("asyncExecutor")
    public CompletableFuture<String> coreService() throws RuntimeException{
        long times = helloTimes.incrementAndGet();
        log.info("AnnotationServiceImpl被调用, 尝试次数:{}，尝试时间:{}", times, LocalTime.now());
        if (times % RetryConstant.RETRY_NUMBER != 0){
            log.warn("发生异常时间:{}", LocalTime.now() );
            throw new RuntimeException("业务执行失败情况！");
        }
        log.info("AnnotationServiceImpl执行成功!");
        return CompletableFuture.completedFuture("SUCCESS");
    }

}
