package com.example.demo.service.impl;

import com.example.demo.constant.RetryConstant;
import com.example.demo.service.IHelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wangyongqiang
 */
@Slf4j
@Component
public class HelloServiceImpl implements IHelloService {

    private static AtomicLong helloTimes = new AtomicLong();

    @Autowired
    private NameServiceImpl nameService;

    @Override
    public String hello(){
        long times = helloTimes.incrementAndGet();
        log.info("hello times:{}", times);
        if (times % RetryConstant.RETRY_NUMBER != 0){
            log.warn("发生异常，time：{}", LocalTime.now() );
            throw new RuntimeException("发生Hello异常");
        }
        return "hello--->" + nameService.getName();
    }
}
