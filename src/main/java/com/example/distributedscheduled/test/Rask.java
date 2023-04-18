package com.example.distributedscheduled.test;

import com.example.distributedscheduled.annotation.RScheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Red
 * @Descpription:
 * @Date: 14:56 2023/4/18
 * @since 1.0
 */
@Component
public class Rask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @RScheduled(value = "xxxxxx",cron = "* 0/3 * * * ?", validity = 10, unit = TimeUnit.SECONDS)
    public void execute() {
        logger.info("我触发l===============");
    }
}
