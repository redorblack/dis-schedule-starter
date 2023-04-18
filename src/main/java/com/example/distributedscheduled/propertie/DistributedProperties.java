package com.example.distributedscheduled.propertie;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Red
 * @Descpription: 支持拓展
 * @Date: 21:38 2023/4/3
 * @since 1.0
 */
@ConfigurationProperties(prefix = "distributed.scheduled")
public class DistributedProperties {

    /**
     * 名称
     */
    private String  appName;

    /**
     * 是否打开 open  close
     */
    private String enable;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }
}
