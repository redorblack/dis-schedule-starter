package com.example.distributedscheduled.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * @Author: Red
 * @Descpription: 支持拓展
 * @Date: 14:34 2023/3/24
 * @since 1.0
 */
public class RedissonExecute {

    RedissonClient redissonClient;

    public RedissonExecute(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取锁
     *
     * @param lockName
     * @return
     */
    public RLock getLock(String lockName) {
        return redissonClient.getLock(lockName);
    }

    /**
     * 解锁
     *
     * @param rLock
     */
    public void unlock(RLock rLock) {
        if (rLock.isHeldByCurrentThread() && rLock.isLocked()) {
            rLock.unlock();
        }
    }

    /**
     * 强制释放锁
     *
     * @param rLock
     */
    public void forceUnlock(RLock rLock) {
        rLock.forceUnlock();
    }

}
