package com.example.distributedscheduled.interceptor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

import java.lang.reflect.Method;

/**
 * @Author: Red
 * @Descpription: advisor
 * @Date: 14:33 2023/4/18
 * @since 1.0
 */
public class DistributedScheduledAdvisor extends AbstractBeanFactoryPointcutAdvisor {


    private static final long serialVersionUID = -2629415015046434725L;
    private RSource rSource;

    /**
     * 切点
     */
    private final ScheduledPointcut pointcut = new ScheduledPointcut() {
        @Override
        protected RSource getCacheOperationSource() {
            return rSource;
        }
    };

    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    public void setrSource(RSource rSource) {
        this.rSource = rSource;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }
}
