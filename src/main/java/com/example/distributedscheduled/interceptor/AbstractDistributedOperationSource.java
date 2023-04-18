package com.example.distributedscheduled.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodClassKey;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Red
 * @Descpription: 缓存
 * @Date: 14:26 2023/4/18
 * @since 1.0
 */
public abstract class AbstractDistributedOperationSource implements RSource {

    private static final Object NULL_CACHING_ATTRIBUTE = new Object();

    protected final Log logger = LogFactory.getLog(getClass());

    private final Map<MethodClassKey, Object> cache = new ConcurrentHashMap<>(512);

    @Override
    public ROperation<?> getROperation(Method method, Class<?> targetClass) {
        MethodClassKey cacheKey = new MethodClassKey(method, targetClass);
        Object cached = this.cache.get(cacheKey);

        if (cached != null) {
            return (cached != NULL_CACHING_ATTRIBUTE ? (ROperation<?>) cached : null);
        } else {
            ROperation<?> operation = computeCacheOperation(method, targetClass);
            if (operation != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Adding cacheable method '" + method.getName() + "' with operation: " + operation);
                }
                this.cache.put(cacheKey, operation);
            } else {
                this.cache.put(cacheKey, NULL_CACHING_ATTRIBUTE);
            }
            return operation;
        }
    }

    /**
     * 判断
     * @param method
     * @param targetClass
     * @return
     */
    private ROperation<?> computeCacheOperation(Method method, @Nullable Class<?> targetClass) {

        // Don't allow no-public methods as required.
        if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }

        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);

        // First try is the method in the target class.
        ROperation<?> operation = findCacheOperation(specificMethod, targetClass);
        if (operation != null) {
            return operation;
        }
        if (specificMethod != method) {
            // Fallback is to look at the original method.
            operation = findCacheOperation(method, targetClass);
            if (operation != null) {
                return operation;
            }
        }
        return null;
    }

    protected boolean allowPublicMethodsOnly() {
        return false;
    }

    /**
     * 子类实现
     *
     * @param method
     * @param targetType
     * @return
     */
    @Nullable
    protected abstract ROperation<?> findCacheOperation(Method method, @Nullable Class<?> targetType);

}
