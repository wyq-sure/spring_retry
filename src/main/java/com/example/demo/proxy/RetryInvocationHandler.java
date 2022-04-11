package com.example.demo.proxy;

import com.example.demo.constant.RetryConstant;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalTime;

/**
 * @author wangyq
 * @date 2022/4/11 10:31
 * @Description 定义一个动态代理的类，对原始的方法进行增强
 * [基于JDK的动态代理]
 */
@Slf4j
public class RetryInvocationHandler implements InvocationHandler {

    private final Object subject;

    public RetryInvocationHandler(Object subject) {
        this.subject = subject;
    }

    /**
     * @author wangyq
     * @date 2022/4/11 10:32
     * @Description 所有动态代理类中需要实现的逻辑方法都要在invoke方法中书写
     * proxy: 被代理之后的对象
     * method: 将要被执行的方法信息
     * args: 执行方法时需要的参数
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        int times = 0;

        while (times < RetryConstant.RETRY_NUMBER) {
            try {
                // TODO: 这里可以再定义一个切面(其实就是一个普通类，其中实现了一些增强方法)，然后在切面中编写一些增强方法；
                //       那么在之后使用代理对象调用的过程中就可以使用切面中的方法
                // TODO: 在调用方法之前，可以执行前置增强方法

                // 在目标类上调用方法，并传入参数
                return method.invoke(subject, args);
                // TODO: 在调用方法之后，可以执行后置增强方法
            } catch (Exception e) {
                times++;
                log.info("times:{},time:{}", times, LocalTime.now());
                if (times >= RetryConstant.RETRY_NUMBER) {
                    log.error("正常捕获异常:{}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            // 延时一秒
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("延迟的过程中捕获异常:{}", e.getMessage());
            }
        }

        return null;
    }

    /**
     * 获取动态代理
     *
     * @param realSubject 代理对象
     */
    public static Object getProxy(Object realSubject) {
        InvocationHandler handler = new RetryInvocationHandler(realSubject);
        return Proxy.newProxyInstance(handler.getClass().getClassLoader(),
                realSubject.getClass().getInterfaces(), handler);
    }

}
