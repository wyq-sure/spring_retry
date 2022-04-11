package com.example.demo.proxy;

import com.example.demo.constant.RetryConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.time.LocalTime;

/**
 * @author wangyongqiang
 */
@Slf4j
public class CGLibRetryProxyHandler implements MethodInterceptor {

    /** 需要代理的目标对象*/
    private Object target;

    /**
     * @author wangyq
     * @date 2022/4/11 15:35
     * @Description 重写拦截方法
     * 1,obj: 拦截的方法
     * 2,method：拦截的方法
     * 3,arr：拦截的方法的参数数组
     * 4,proxy：方法的代理对象，用于执行父类的方法
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] arr, MethodProxy proxy) throws Throwable {
        int times = 0;

        while (times < RetryConstant.RETRY_NUMBER) {
            try {
                return method.invoke(target, arr);
            } catch (Exception e) {
                times++;
                log.info("cglib retry :{},time:{}", times, LocalTime.now());
                if (times >= RetryConstant.RETRY_NUMBER) {
                    throw new RuntimeException(e);
                }
            }

            // 延时一秒
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**定义获取代理对象方法*/
    Object getCglibProxy(Object objectTarget){
        this.target = objectTarget;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(objectTarget.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }
}
