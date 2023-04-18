package com.example.distributedscheduled.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Red
 * @Descpription: 增强
 * @Date: 11:29 2023/4/18
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Scheduled
public @interface RScheduled {

    /**
     * 执行状态保存时间
     * 默认1分钟
     * @return
     */
    int validity() default 1;


    /**
     * cron 表达式
     * @return
     */
    @AliasFor(annotation = Scheduled.class, attribute = "cron")
    String cron() default "";


    /**
     * 信息字符串，可用于提供构建分布式锁key的信息
     * @return 信息字符串
     */
    String value() default "";

    /**
     * 过期时间的单位
     *
     * @return
     */
    TimeUnit unit() default TimeUnit.MINUTES;
}
