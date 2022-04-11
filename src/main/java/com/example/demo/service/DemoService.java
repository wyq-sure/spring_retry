package com.example.demo.service;

import java.util.concurrent.CompletableFuture;

public interface DemoService  {

    /**
     * @author wangyq
     * @date 2022/4/11 10:37
     * @Description 使用spring-retry注解实现重试
     * @return 异步调用的返回值
     */
    public CompletableFuture<String> coreService() throws Exception;
}
