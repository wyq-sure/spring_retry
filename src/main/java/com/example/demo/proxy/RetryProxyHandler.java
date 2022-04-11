package com.example.demo.proxy;

import com.example.demo.constant.RetryConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @author wangyq
 * @date 2022/4/11 14:43
 * @Description 获取代理的对象的工具类
 */
@Component
public class RetryProxyHandler {

    @Autowired
    private ConfigurableApplicationContext context;

    public Object getProxy(Class clazz) {
        // 1. 从Bean中获取对象
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        Map<String, Object> beans = beanFactory.getBeansOfType(clazz);
        Set<Map.Entry<String, Object>> entries = beans.entrySet();
        if (entries.size() <= 0){
            throw new RuntimeException();
        }
        // 如果有多个候选bean, 判断其中是否有代理bean
        Object bean = null;
        if (entries.size() > 1){
            for (Map.Entry<String, Object> entry : entries) {
                if (entry.getKey().contains(RetryConstant.PROXY_BEAN_SUFFIX)){
                    bean = entry.getValue();
                }
            };
            if (bean != null){
                return bean;
            }
            throw new RuntimeException();
        }

        Object source = beans.entrySet().iterator().next().getValue();

        // 2. 判断该对象的代理对象是否存在
        String proxyBeanName = clazz.getSimpleName() + RetryConstant.PROXY_BEAN_SUFFIX;
        boolean exist = beanFactory.containsBean(proxyBeanName);
        if (exist) {
            bean = beanFactory.getBean(proxyBeanName);
            return bean;
        }

        // 3. 不存在则生成代理对象
        /// 这里是使用jdk的动态代理去实现的，
        //bean = RetryInvocationHandler.getProxy(source);
        // 使用CGLIB动态代理的方式实现
        CGLibRetryProxyHandler proxyHandler = new CGLibRetryProxyHandler();
        bean = proxyHandler.getCglibProxy(source);

        // 4. 将bean注入spring容器
        beanFactory.registerSingleton(proxyBeanName, bean);
        return bean;
    }
}
