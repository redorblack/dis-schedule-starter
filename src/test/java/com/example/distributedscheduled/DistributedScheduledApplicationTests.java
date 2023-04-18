package com.example.distributedscheduled;

import com.example.distributedscheduled.lock.RedissonExecute;
import com.example.distributedscheduled.test.Rask;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DistributedScheduledApplication.class)
class DistributedScheduledApplicationTests {

    @Autowired
    private Rask rask;

    @Autowired
    private RedissonExecute redissonExecute;


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void redis(){
        redisTemplate.opsForValue().set("dsadsa","1221",10, TimeUnit.MINUTES);
    }

    @Test
    void ceshi() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.execute(()-> rask.execute());
        executorService.execute(()-> rask.execute());
        executorService.execute(()-> rask.execute());
        executorService.execute(()-> rask.execute());
        executorService.execute(()-> rask.execute());
        executorService.execute(()-> rask.execute());

        executorService.shutdown();
        while (!executorService.isTerminated()){}

    }

}
