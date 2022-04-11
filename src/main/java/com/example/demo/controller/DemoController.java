package com.example.demo.controller;

import com.example.demo.proxy.RetryProxyHandler;
import com.example.demo.service.IHelloService;
import com.example.demo.service.impl.DemoServiceImpl;
import com.example.demo.service.impl.HelloServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wangyq
 * @date 2022年04月10日 21:13
 * @Description 使用接口的方式测试对应的重试方法
 */
@RestController
@Slf4j
public class DemoController {

    @Resource(name="DemoServiceImpl")
    private DemoServiceImpl annotationServiceImpl;

    @Resource
    RetryProxyHandler retryProxyHandler;

    /**
     * @author wangyq
     * @date 2022/4/11 16:09
     * @Description 使用spring-retry注解方式
     */
    @GetMapping("/testRetry")
    public String testRetry() {
        try {
            annotationServiceImpl.coreService();
        } catch ( Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "SUCCESS";
    }

    /**
     * @author wangyq
     * @date 2022/4/11 16:09
     * @Description 使用代理类的方式
     */
    @GetMapping("/testRetryByProxy")
    public String testRetryByProxy() {
        try {
            IHelloService proxy = (IHelloService) retryProxyHandler.getProxy(HelloServiceImpl.class);
            String hello = proxy.hello();
            log.info("hello:{}", hello);
        } catch ( Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "SUCCESS";
    }

}
