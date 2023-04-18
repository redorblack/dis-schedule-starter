package com.example.distributedscheduled.interceptor;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Red
 * @Descpription: 注解信息解析
 * @Date: 11:43 2023/4/18
 * @since 1.0
 */
public class ROperation<A extends Annotation> {
    /**
     * 执行状态保存时间
     * 默认1分钟
     *
     * @return
     */
    int validity;

    /**
     * cron 表达式
     *
     * @return
     */
    String cron;


    /**
     * 信息字符串，可用于提供构建分布式锁key的信息
     *
     * @return 信息字符串
     */
    String value;

    /**
     * 过期时间的单位
     *
     * @return
     */
    TimeUnit unit;

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }
}
