package com.example.distributedscheduled.interceptor;

import com.example.distributedscheduled.annotation.RScheduled;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

/**
 * @Author: Red
 * @Descpription: 注解解析
 * @Date: 14:28 2023/4/18
 * @since 1.0
 */
public class AnnotationDistributedOperationSource extends AbstractDistributedOperationSource{
    @Override
    protected ROperation<?> findCacheOperation(Method method, Class<?> targetType) {
        //equalsMethod 过滤
        boolean equalsMethod = AopUtils.isEqualsMethod(method);
        if (equalsMethod) {
            return null;
        }
        boolean annotation = AnnotatedElementUtils.hasAnnotation(method, RScheduled.class);
        if (annotation) {
            RScheduled scheduled = method.getAnnotation(RScheduled.class);
            ROperation<RScheduled> distributedOperation = new ROperation<RScheduled>();
            distributedOperation.setCron(scheduled.cron());
            distributedOperation.setUnit(scheduled.unit());
            distributedOperation.setValidity(scheduled.validity());
            distributedOperation.setValue(scheduled.value());
            return distributedOperation;
        }
        return null;
    }
}
