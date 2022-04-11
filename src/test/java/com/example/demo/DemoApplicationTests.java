package com.example.demo;

import com.example.demo.proxy.RetryInvocationHandler;
import com.example.demo.proxy.RetryProxyHandler;
import com.example.demo.service.IHelloService;
import com.example.demo.service.impl.DemoServiceImpl;
import com.example.demo.service.impl.HelloServiceImpl;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
class DemoApplicationTests {

    @Resource
    RetryProxyHandler retryProxyHandler;


    @Resource(name="DemoServiceImpl")
    private DemoServiceImpl annotationServiceImpl;

    @Test
    void contextLoads() {
    }

    /**
     * @author wangyq
     * @date 2022/4/11 14:45
     * @Description 基于单个测试类进行处理
     */
    @Test
    void helloDynamicProxy() {
        // 创建目标接口
        IHelloService realService = new HelloServiceImpl();
        // 从代理对象中获取增加之后的目标对象
        IHelloService proxyService = (IHelloService) RetryInvocationHandler.getProxy(realService);
        String hello = proxyService.hello();
        log.info("hello:{}", hello);
    }

    /*
     * 调用代理对象的方法，在其内部根据不同的代理对象的获取，实现不同的动态代理
     * 1，JDK
     * 2，CGLIB
     */
    @Test
    void helloProxy(){
        IHelloService proxy = (IHelloService) retryProxyHandler.getProxy(HelloServiceImpl.class);
        String hello = proxy.hello();
        log.info("hello:{}", hello);
    }


    /**
     * @author wangyq
     * @date 2022/4/11 17:07
     * @Description 使用 spring-retry 的方式实现重试
     */
    @Test
    void testRetryAnnotation() throws RuntimeException, ExecutionException, InterruptedException {
        long begin = System.currentTimeMillis();
        CompletableFuture<String> stringCompletableFuture = annotationServiceImpl.coreService();
        long end = System.currentTimeMillis();
        log.info("主线程已经执行完成，可以进一步执行其他的任务。");
        // 在实际的业务中如果需要获取异步任务的执行结果，那么可以使用当下的返回值方式进行捕捉，同时在调用get方法的时候会导致阻塞，直到获取道结果停止
        //log.info("程序运行耗时:{}ms, 程序返回结果:{}", end - begin, stringCompletableFuture.get());
        log.info("程序运行耗时:{}ms", end - begin);
    }



    /**
     * @author wangyq
     * @date 2022/4/11 17:07
     * @Description 使用 guava-retry 的方式实现重试,具体的使用可以再进行查询
     */
    @Test
    void guavaRetry() {
        // 先创建一个Retryer实例，然后使用这个实例对需要重试的方法进行调用，可以通过很多方法来设置重试机制
        Retryer<String> retryer = RetryerBuilder.<String>newBuilder()
                .retryIfExceptionOfType(RuntimeException.class)
                .retryIfResult(String::isEmpty)
                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();

        try {
            /// 对于字符串的类型转换，如果方法函数本身没有提供对应的get方法的时候，可以直接使用String的方法
            //retryer.call(() -> String.valueOf(annotationServiceImpl.coreService()));
            retryer.call(() -> annotationServiceImpl.coreService().get());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
