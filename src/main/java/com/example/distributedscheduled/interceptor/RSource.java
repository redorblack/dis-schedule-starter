package com.example.distributedscheduled.interceptor;

import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

/**
 * @Author: Red
 * @Descpription: 资源解析
 * @Date: 11:43 2023/4/18
 * @since 1.0
 */
public interface RSource {

    @Nullable
    ROperation<?> getROperation(Method method, @Nullable Class<?> targetClass);

}
