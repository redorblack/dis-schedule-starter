
package com.example.distributedscheduled.autoconfigure;

import org.redisson.config.Config;

@FunctionalInterface
public interface RedissonAutoConfigurationCustomizer {

  
    void customize(final Config configuration);
}
