package com.example.distributedscheduled.autoconfigure;

import com.example.distributedscheduled.interceptor.AnnotationDistributedOperationSource;
import com.example.distributedscheduled.interceptor.DistributeMethodInterceptor;
import com.example.distributedscheduled.interceptor.DistributedScheduledAdvisor;
import com.example.distributedscheduled.lock.RedissonExecute;
import com.example.distributedscheduled.propertie.DistributedProperties;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Author: Red
 * @Descpription: 配置
 * @Date: 18:06 2023/4/18
 * @since 1.0
 */
@Configuration
@ConditionalOnProperty(prefix = "distributed.scheduled", name = "enable", havingValue = "open")
@EnableConfigurationProperties(DistributedProperties.class)
public class DistributeConfiguration {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 拦截
     *
     * @param stringRedisTemplate
     * @param redissonExecute
     * @return
     */
    @Bean
    public DistributeMethodInterceptor distributeMethodInterceptor(RedissonExecute redissonExecute, DistributedProperties distributedProperties) {
        DistributeMethodInterceptor distributeMethodInterceptor = new DistributeMethodInterceptor();
        distributeMethodInterceptor.setRedissonExecute(redissonExecute);
        distributeMethodInterceptor.setDistributedProperties(distributedProperties);
        distributeMethodInterceptor.setDistributedSource(createSource());
        distributeMethodInterceptor.setStringRedisTemplate(stringRedisTemplate);
        return distributeMethodInterceptor;
    }

    @Bean
    @ConditionalOnClass(RedissonClient.class)
    public RedissonExecute redissonExecute(RedissonClient redissonClient) {
        return new RedissonExecute(redissonClient);
    }

    @Bean
    public DistributedScheduledAdvisor scheduledAdvisor(DistributeMethodInterceptor distributeMethodInterceptor) {
        logger.info("分布式定时任务启动====================");
        DistributedScheduledAdvisor distributedScheduledAdvisor = new DistributedScheduledAdvisor();
        distributedScheduledAdvisor.setrSource(createSource());
        distributedScheduledAdvisor.setAdvice(distributeMethodInterceptor);
        return distributedScheduledAdvisor;
    }


    @Bean
    public AnnotationDistributedOperationSource createSource() {
        return new AnnotationDistributedOperationSource();
    }
}
