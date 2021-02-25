package org.rainbow.websocket.standard;

import org.rainbow.websocket.annotation.ServerEndpoint;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.env.Environment;

import java.net.InetSocketAddress;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ApplicationObjectSupport Spring初始化的时候会通过该抽象类的setApplicationContext(ApplicationContext context)进行注入
 * 并提供getApplicationContext()方便获取ApplicationContext
 * <p>
 * SmartInitializingSingleton接口 实现该接口之后 当所有的单例bean都初始完成以后 容器会回调该接口的方法afterSingletonInstantiated()
 * <p>
 * BeanFactoryAware 实现该接口之后 Spring会将BeanFactory对象注入进来
 *
 * @author K
 * @date 2021/2/25  11:57
 */
public class ServerEndpointExporter extends ApplicationObjectSupport implements SmartInitializingSingleton, BeanFactoryAware {

    /**
     * 当前应用程序的环境
     */
    @Autowired
    Environment environment;

    /**
     * 抽象对象工厂
     */
    private AbstractBeanFactory beanFactory;



    /**
     * 实现BeanFactoryAware接口会自动调用该方法将BeanFactory注入
     *
     * @param beanFactory 对象工厂
     * @throws BeansException  Bean异常
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof AbstractBeanFactory)) {
            throw new IllegalArgumentException(
                    "AutowiredAnnotationBeanPostProcessor requires a AbstractBeanFactory: " + beanFactory);
        }
        this.beanFactory = (AbstractBeanFactory) beanFactory;
    }

    /**
     * 从Application中获取添加了ServerEndpoint注解的类 并将这些websocket端点注册
     */
    protected void registerEndpoints() {
        // 所有的websocket服务端点类集合
        Set<Class<?>> endpointClasses = new LinkedHashSet<>();

        // 获取ApplicationContext
        ApplicationContext context = getApplicationContext();
        if (context != null) {
            // 通过applicationContext获取添加了ServerEndpoint注解类
            String[] endpointBeanNames = context.getBeanNamesForAnnotation(ServerEndpoint.class);
            for (String beanName : endpointBeanNames) {
                endpointClasses.add(context.getType(beanName));
            }
        }

        for (Class<?> endpointClass : endpointClasses) {

        }

        // 初始化
        init();
    }

    private void init() {

    }

    @Override
    public void afterSingletonsInstantiated() {

    }
}
