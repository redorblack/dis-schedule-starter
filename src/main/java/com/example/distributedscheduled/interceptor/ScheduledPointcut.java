package com.example.distributedscheduled.interceptor;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;

/**
 * @Author: Red
 * @Descpription:
 * @Date: 14:31 2023/4/18
 * @since 1.0
 */
public abstract class ScheduledPointcut extends StaticMethodMatcherPointcut {
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        RSource source = getCacheOperationSource();
        return (source != null && source.getROperation(method, targetClass) != null);
    }

    protected abstract RSource getCacheOperationSource();

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ScheduledPointcut)) {
            return false;
        }
        ScheduledPointcut otherPc = (ScheduledPointcut) other;
        return ObjectUtils.nullSafeEquals(getCacheOperationSource(), otherPc.getCacheOperationSource());
    }
}
