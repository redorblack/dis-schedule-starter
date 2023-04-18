package com.example.distributedscheduled.interceptor;

import cn.hutool.core.date.SystemClock;
import com.example.distributedscheduled.lock.RedissonExecute;
import com.example.distributedscheduled.propertie.DistributedProperties;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Red
 * @Descpription: 切点
 * @Date: 14:19 2023/4/18
 * @since 1.0
 */
public class DistributeMethodInterceptor implements MethodInterceptor, ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private StringRedisTemplate stringRedisTemplate;

    private RedissonExecute redissonExecute;

    private DistributedProperties distributedProperties;

    private ApplicationContext ctx;

    private RSource distributedSource;

    public DistributeMethodInterceptor() {

    }

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void setRedissonExecute(RedissonExecute redissonExecute) {
        this.redissonExecute = redissonExecute;
    }

    public void setDistributedProperties(DistributedProperties distributedProperties) {
        this.distributedProperties = distributedProperties;
    }

    public void setDistributedSource(RSource distributedSource) {
        this.distributedSource = distributedSource;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(invocation.getThis());
        ROperation<?> rOperation = distributedSource.getROperation(method, targetClass);
        if (Objects.isNull(rOperation)) {
            return invocation.proceed();
        }
        //key的名称 项目名称 + 业务名称
        long createTimeMillis = SystemClock.now();
        String property = StringUtils.isNotBlank(distributedProperties.getAppName()) ? distributedProperties.getAppName() : ctx.getEnvironment().getProperty("spring.application.name");
        // 如果没有值则使用方法全名
        String value = Optional.ofNullable(rOperation.getValue()).filter(StringUtils::isNotBlank).orElseGet(() -> "TMP_LOCK:" + invocation.getMethod().getDeclaringClass().getName() + "#" + invocation.getMethod().getName());
        String lockName = property + ":" + value;
        String successKey = lockName + "#result";
        RLock rLock = redissonExecute.getLock(lockName);
        String cron = rOperation.getCron();
        TimeUnit unit = rOperation.getUnit();
        int validity = rOperation.getValidity();
        try {
            boolean lock = rLock.tryLock();
            if (lock) {
                logger.warn("锁名称:{},线程ID:{},次数:{}", rLock.getName(), Thread.currentThread().getId(), rLock.getHoldCount());
                try {
                    String success = stringRedisTemplate.opsForValue().get(successKey);
                    if ("DOING".equalsIgnoreCase(success)) {
                        logger.warn("{} 任务执行中 cron : {} ", lockName, cron);
                        return null;
                    } else if ("SUCCESS".equalsIgnoreCase(success)) {
                        logger.debug("{} 任务已经成功执行, cron : {}", lockName, cron);
                        return null;
                    } else if ("FAIL".equalsIgnoreCase(success)) {
                        logger.warn("{} 任务已经执行失败,现在再次执行 cron : {}", lockName, cron);
                    }
                    Object o;
                    try {
                        setSuccessKey(successKey, "DOING", adjustExpireMillis(createTimeMillis, validity, unit));
                        o = invocation.proceed();
                        setSuccessKey(successKey, "SUCCESS", adjustExpireMillis(createTimeMillis, validity, unit));
                    } catch (Throwable throwable) {
                        setSuccessKey(successKey, "FAIL", adjustExpireMillis(createTimeMillis, validity, unit));
                        throw throwable;
                    }
                    return o;
                } finally {
                    redissonExecute.forceUnlock(rLock);
                }
            } else {
                logger.warn("{} 任务已被其他节点执行 cron : {}", lockName, cron);
            }
        } catch (Exception e) {
            logger.error("执行任务失败,error:{},e:{}", e.getMessage(), e);
        }
        return null;
    }

    private Long adjustExpireMillis(Long createTime, Integer validity, TimeUnit timeUnit) {
        long e = TimeoutUtils.toMillis(validity, timeUnit) - (SystemClock.now() - createTime - 1);
        return e < 0 ? 0 : e;
    }

    private void setSuccessKey(String successKey, String doing, Long validity) {
        if (validity != null && validity != 0) {
            stringRedisTemplate.opsForValue().set(successKey, doing, validity, TimeUnit.MILLISECONDS);
        } else {
            logger.trace("没有找到对应的key {}", successKey);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
