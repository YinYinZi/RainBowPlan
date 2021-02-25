package org.rainbow.websocket.pojo;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.rainbow.websocket.annotation.*;
import org.rainbow.websocket.exception.DeploymentException;
import org.rainbow.websocket.support.*;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 此类的实例创建并缓存onXXX调用的方法处理程序，方法信息和参数信息。
 *
 * @author K
 * @date 2021/2/23  18:04
 */
public class PojoMethodMapping {
    /**
     * ParameterNameDiscoverer是用于发现方法和构造函数的参数名称的接口
     * DefaultParameterNameDiscoverer类是ParameterNameDiscoverer策略接口的默认实现，使用Java 8标准反射机制
     */
    private static final ParameterNameDiscoverer DISCOVERER = new DefaultParameterNameDiscoverer();

    private final Method beforeHandshake;
    private final Method onOpen;
    private final Method onClose;
    private final Method onError;
    private final Method onMessage;
    private final Method onBinary;
    private final Method onEvent;
    private final MethodParameter[] beforeHandshakeParameters;
    private final MethodParameter[] onOpenParameters;
    private final MethodParameter[] onCloseParameters;
    private final MethodParameter[] onErrorParameters;
    private final MethodParameter[] onMessageParameters;
    private final MethodParameter[] onBinaryParameters;
    private final MethodParameter[] onEventParameters;
    private final MethodArgumentResolver[] beforeHandshakeArgResolvers;
    private final MethodArgumentResolver[] onOpenArgResolvers;
    private final MethodArgumentResolver[] onCloseArgResolvers;
    private final MethodArgumentResolver[] onErrorArgResolvers;
    private final MethodArgumentResolver[] onMessageArgResolvers;
    private final MethodArgumentResolver[] onBinaryArgResolvers;
    private final MethodArgumentResolver[] onEventArgResolvers;
    private final Class pojoClazz;
    private final ApplicationContext applicationContext;
    private final AbstractBeanFactory beanFactory;

    public PojoMethodMapping(Class<?> pojoClazz, ApplicationContext context, AbstractBeanFactory beanFactory) throws DeploymentException {
        this.applicationContext = context;
        this.pojoClazz = pojoClazz;
        this.beanFactory = beanFactory;
        Method handshake = null;
        Method open = null;
        Method close = null;
        Method error = null;
        Method message = null;
        Method binary = null;
        Method event = null;
        Method[] pojoClazzMethods = null;
        Class<?> currentClazz = pojoClazz;
        while (!currentClazz.equals(Object.class)) {
            // 返回pojoClazz的所有Method数组
            Method[] currentClazzMethods = currentClazz.getDeclaredMethods();
            if (currentClazz == pojoClazz) {
                pojoClazzMethods = currentClazzMethods;
            }
            // 循环pojoClazz的Method数组
            for (Method method : currentClazzMethods) {
                // 如果Method注解了@BeforeHandshake
                if (method.getAnnotation(BeforeHandshake.class) != null) {
                    // 检查方法的可访问性
                    checkPublic(method);
                    // 检查是否重复注解
                    if (handshake == null) {
                        handshake = method;
                    } else {
                        if (currentClazz == pojoClazz || !isMethodOverride(handshake, method)) {
                            throw new DeploymentException(
                                    "pojoMethodMapping.duplicateAnnotation BeforeHandshake");
                        }
                    }
                } else if (method.getAnnotation(OnOpen.class) != null) {
                    // 检查方法的可访问性
                    checkPublic(method);
                    // 检查是否重复注解
                    if (open == null) {
                        open = method;
                    } else {
                        if (currentClazz == pojoClazz || !isMethodOverride(open, method)) {
                            throw new DeploymentException(
                                    "pojoMethodMapping.duplicateAnnotation OnOpen");
                        }
                    }
                } else if (method.getAnnotation(OnClose.class) != null) {
                    // 检查方法的可访问性
                    checkPublic(method);
                    // 检查是否重复注解
                    if (close == null) {
                        close = method;
                    } else {
                        if (currentClazz == pojoClazz ||
                                !isMethodOverride(close, method)) {
                            // Duplicate annotation
                            throw new DeploymentException(
                                    "pojoMethodMapping.duplicateAnnotation OnClose");
                        }
                    }
                } else if (method.getAnnotation(OnError.class) != null) {
                    // 检查方法的可访问性
                    checkPublic(method);
                    // 检查是否重复注解
                    if (error == null) {
                        error = method;
                    } else {
                        if (currentClazz == pojoClazz ||
                                !isMethodOverride(error, method)) {
                            // Duplicate annotation
                            throw new DeploymentException(
                                    "pojoMethodMapping.duplicateAnnotation OnError");
                        }
                    }
                } else if (method.getAnnotation(OnMessage.class) != null) {
                    // 检查方法的可访问性
                    checkPublic(method);
                    // 检查是否重复注解
                    if (message == null) {
                        message = method;
                    } else {
                        if (currentClazz == pojoClazz ||
                                !isMethodOverride(message, method)) {
                            // Duplicate annotation
                            throw new DeploymentException(
                                    "pojoMethodMapping.duplicateAnnotation onMessage");
                        }
                    }
                } else if (method.getAnnotation(OnBinary.class) != null) {
                    // 检查方法的可访问性
                    checkPublic(method);
                    // 检查是否重复注解
                    if (binary == null) {
                        binary = method;
                    } else {
                        if (currentClazz == pojoClazz ||
                                !isMethodOverride(binary, method)) {
                            // Duplicate annotation
                            throw new DeploymentException(
                                    "pojoMethodMapping.duplicateAnnotation OnBinary");
                        }
                    }
                } else if (method.getAnnotation(OnEvent.class) != null) {
                    // 检查方法的可访问性
                    checkPublic(method);
                    // 检查是否重复注解
                    if (event == null) {
                        event = method;
                    } else {
                        if (currentClazz == pojoClazz ||
                                !isMethodOverride(event, method)) {
                            // Duplicate annotation
                            throw new DeploymentException(
                                    "pojoMethodMapping.duplicateAnnotation OnEvent");
                        }
                    }
                } else {

                }
            }
            // 继续向上进行检查并赋值
            currentClazz = currentClazz.getSuperclass();
        }

        // 如果注解对应的class上的Method不为空 但是method所定义的class与pojoClass不一致
        // 检查重载方法后有无标注注解 如果没有将method置为空
        if (handshake != null && handshake.getDeclaringClass() != pojoClazz) {
            if (isOverrideWithoutAnnotation(pojoClazzMethods, handshake, BeforeHandshake.class)) {
                handshake = null;
            }
        }
        if (open != null && open.getDeclaringClass() != pojoClazz) {
            if (isOverrideWithoutAnnotation(pojoClazzMethods, open, OnOpen.class)) {
                open = null;
            }
        }
        if (close != null && close.getDeclaringClass() != pojoClazz) {
            if (isOverrideWithoutAnnotation(pojoClazzMethods, close, OnClose.class)) {
                close = null;
            }
        }
        if (error != null && error.getDeclaringClass() != pojoClazz) {
            if (isOverrideWithoutAnnotation(pojoClazzMethods, error, OnError.class)) {
                error = null;
            }
        }
        if (message != null && message.getDeclaringClass() != pojoClazz) {
            if (isOverrideWithoutAnnotation(pojoClazzMethods, message, OnMessage.class)) {
                message = null;
            }
        }
        if (binary != null && binary.getDeclaringClass() != pojoClazz) {
            if (isOverrideWithoutAnnotation(pojoClazzMethods, binary, OnBinary.class)) {
                binary = null;
            }
        }
        if (event != null && event.getDeclaringClass() != pojoClazz) {
            if (isOverrideWithoutAnnotation(pojoClazzMethods, event, OnEvent.class)) {
                event = null;
            }
        }

        this.beforeHandshake = handshake;
        this.onOpen = open;
        this.onClose = close;
        this.onError = error;
        this.onMessage = message;
        this.onBinary = binary;
        this.onEvent = event;
        beforeHandshakeParameters = getParameters(beforeHandshake);
        onOpenParameters = getParameters(onOpen);
        onCloseParameters = getParameters(onClose);
        onMessageParameters = getParameters(onMessage);
        onErrorParameters = getParameters(onError);
        onBinaryParameters = getParameters(onBinary);
        onEventParameters = getParameters(onEvent);
        beforeHandshakeArgResolvers = getResolvers(beforeHandshakeParameters);
        onOpenArgResolvers = getResolvers(onOpenParameters);
        onCloseArgResolvers = getResolvers(onCloseParameters);
        onMessageArgResolvers = getResolvers(onMessageParameters);
        onErrorArgResolvers = getResolvers(onErrorParameters);
        onBinaryArgResolvers = getResolvers(onBinaryParameters);
        onEventArgResolvers = getResolvers(onEventParameters);
    }

    /**
     * 检查方法的可访问性 如果不为Public 则抛出{@link org.rainbow.websocket.exception.DeploymentException}异常
     *
     * @param m Method
     * @throws DeploymentException {@link org.rainbow.websocket.exception.DeploymentException}异常
     */
    private void checkPublic(Method m) throws DeploymentException {
        if (!Modifier.isPublic(m.getModifiers())) {
            throw new DeploymentException("pojoMethodMapping.methodNotPublic" + m.getName());
        }
    }

    /**
     * 判断method A和method B是否为同一个方法 获取为重载父类方法
     *
     * @param method1 方法1
     * @param method2 方法2
     * @return true / false
     */
    private boolean isMethodOverride(Method method1, Method method2) {
        return (method1.getName().equals(method2.getName())
                && method1.getReturnType().equals(method2.getReturnType())
                && Arrays.equals(method1.getParameterTypes(), method2.getParameterTypes()));
    }

    /**
     * 是否重载方法但是没有进行注解
     *
     * @param methods          方法
     * @param superClazzMethod 父类方法
     * @param annotation       注解
     * @return true / false
     */
    private boolean isOverrideWithoutAnnotation(Method[] methods,
                                                Method superClazzMethod,
                                                Class<? extends Annotation> annotation) {
        for (Method method : methods) {
            if (isMethodOverride(method, superClazzMethod) && (method.getAnnotation(annotation) == null)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取websocket服务端点的实例
     *
     * @return websocket服务端点对象
     */
    Object getEndpointInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object implement = pojoClazz.getDeclaredConstructor().newInstance();
        AutowiredAnnotationBeanPostProcessor postProcessor = applicationContext.getBean(AutowiredAnnotationBeanPostProcessor.class);
        postProcessor.postProcessProperties(null, implement, null);
        return implement;
    }


    Object[] getBeforeHandshakeArgs(Channel channel, FullHttpRequest req) throws Exception {
        return getMethodArgumentValues(channel, req, beforeHandshakeParameters, beforeHandshakeArgResolvers);
    }

    Method getOnOpen() {
        return onOpen;
    }

    Object[] getOnOpenArgs(Channel channel, FullHttpRequest req) throws Exception {
        return getMethodArgumentValues(channel, req, onOpenParameters, onOpenArgResolvers);
    }

    MethodArgumentResolver[] getOpenArgResolvers() {
        return onOpenArgResolvers;
    }

    Method getOnClose() {
        return onClose;
    }

    Object[] getOnCloseArgs(Channel channel) throws Exception {
        return getMethodArgumentValues(channel, null, onCloseParameters, onCloseArgResolvers);
    }

    Method getOnError() {
        return onError;
    }

    Object[] getOnErrorArgs(Channel channel, Throwable throwable) throws Exception {
        return getMethodArgumentValues(channel, throwable, onErrorParameters, onErrorArgResolvers);
    }

    Method getOnMessage() {
        return onMessage;
    }

    Object[] getOnMessageArgs(Channel channel, TextWebSocketFrame textWebSocketFrame) throws Exception {
        return getMethodArgumentValues(channel, textWebSocketFrame, onMessageParameters, onMessageArgResolvers);
    }

    Method getOnBinary() {
        return onBinary;
    }

    Object[] getOnBinaryArgs(Channel channel, BinaryWebSocketFrame binaryWebSocketFrame) throws Exception {
        return getMethodArgumentValues(channel, binaryWebSocketFrame, onBinaryParameters, onBinaryArgResolvers);
    }

    Method getOnEvent() {
        return onEvent;
    }

    Object[] getOnEventArgs(Channel channel, Object evt) throws Exception {
        return getMethodArgumentValues(channel, evt, onEventParameters, onEventArgResolvers);
    }

    /**
     * 通过方法参数和方法参数解析器获取方法参数值
     *
     * @param channel    通过
     * @param object     类对象
     * @param parameters 方法参数数组
     * @param resolvers  方法参数解析器数组
     * @return 方法参数值数组
     * @throws Exception 异常
     */
    private Object[] getMethodArgumentValues(Channel channel, Object object, MethodParameter[] parameters,
                                             MethodArgumentResolver[] resolvers) throws Exception {
        Object[] objects = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            MethodArgumentResolver resolver = resolvers[i];
            Object arg = resolver.resolveArgument(parameter, channel, object);
            objects[i] = arg;
        }
        return objects;
    }

    /**
     * 获取方法参数解析器
     *
     * @param parameters 方法参数数组
     * @return 方法参数解析器数组
     * @throws DeploymentException 异常
     */
    private MethodArgumentResolver[] getResolvers(MethodParameter[] parameters) throws DeploymentException {
        MethodArgumentResolver[] methodArgumentResolvers = new MethodArgumentResolver[parameters.length];
        List<MethodArgumentResolver> resolvers = getDefaultResolvers();
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            for (MethodArgumentResolver resolver : resolvers) {
                if (resolver.supportsParameter(parameter)) {
                    methodArgumentResolvers[i] = resolver;
                    break;
                }
            }
            if (methodArgumentResolvers[i] == null) {
                throw new DeploymentException("pojoMethodMapping.paramClassIncorrect parameter name : " + parameter.getParameterName());
            }
        }
        return methodArgumentResolvers;
    }

    /**
     * 获取默认的方法参数解析器
     *
     * @return 方法参数解析器
     */
    private List<MethodArgumentResolver> getDefaultResolvers() {
        List<MethodArgumentResolver> resolvers = new ArrayList<>();
        resolvers.add(new SessionMethodArgumentResolver());
        resolvers.add(new HttpHeadersMethodArgumentResolver());
        resolvers.add(new TextMethodArgumentResolver());
        resolvers.add(new ThrowableMethodArgumentResolver());
        resolvers.add(new ByteMethodArgumentResolver());
        resolvers.add(new RequestParamMapMethodArgumentResolver());
        resolvers.add(new RequestParamMethodArgumentResolver(beanFactory));
        resolvers.add(new PathVariableMapMethodArgumentResolver());
        resolvers.add(new PathVariableMethodArgumentResolver(beanFactory));
        resolvers.add(new EventMethodArgumentResolver(beanFactory));
        return resolvers;
    }

    /**
     * 获取方法的方法参数数组
     *
     * @param m 方法
     * @return 方法参数数组
     */
    private static MethodParameter[] getParameters(Method m) {
        if (m == null) {
            return new MethodParameter[0];
        }
        // 方法参数个数
        int parameterCount = m.getParameterCount();
        MethodParameter[] result = new MethodParameter[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            // 获取对应位置上的方法参数
            MethodParameter methodParameter = new MethodParameter(m, i);
            // 初始化该方法参数的参数名发现器
            methodParameter.initParameterNameDiscovery(DISCOVERER);
            result[i] = methodParameter;
        }
        return result;
    }
}
