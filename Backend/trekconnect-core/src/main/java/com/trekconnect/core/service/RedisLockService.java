package com.trekconnect.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * Service providing Redis-based distributed locking for event seat reservations.
 */
@Service
public class RedisLockService {

    private static final Logger logger = LoggerFactory.getLogger(RedisLockService.class);

    private final StringRedisTemplate redisTemplate;

    @Autowired(required = false)
    public RedisLockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Tries to acquire a Redis distributed lock for lockKey.
     * 
     * @param lockKey Key name (e.g., "lock:event:123")
     * @param expireSeconds Key expiry TTL in seconds
     * @return Lock owner token if acquired, or null if lock is currently held by another thread.
     */
    public String acquireLock(String lockKey, long expireSeconds) {
        if (redisTemplate == null) {
            logger.debug("RedisTemplate not present. Bypassing lock for key: {}", lockKey);
            return UUID.randomUUID().toString();
        }

        String lockValue = UUID.randomUUID().toString();
        try {
            Boolean acquired = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, lockValue, Duration.ofSeconds(expireSeconds));
            if (Boolean.TRUE.equals(acquired)) {
                logger.info("Acquired Redis lock [{}] with token: {}", lockKey, lockValue);
                return lockValue;
            } else {
                logger.warn("Failed to acquire Redis lock [{}] - locked by another request", lockKey);
                return null;
            }
        } catch (Exception e) {
            logger.warn("Redis error acquiring lock [{}]: {}. Falling back to DB optimistic locking.", lockKey, e.getMessage());
            return lockValue;
        }
    }

    /**
     * Releases the Redis lock if the lock owner token matches.
     * 
     * @param lockKey Key name
     * @param lockValue Lock owner token received during acquisition
     * @return true if released or bypassed, false otherwise.
     */
    public boolean releaseLock(String lockKey, String lockValue) {
        if (redisTemplate == null || lockValue == null) {
            return true;
        }
        try {
            String currentValue = redisTemplate.opsForValue().get(lockKey);
            if (lockValue.equals(currentValue)) {
                redisTemplate.delete(lockKey);
                logger.info("Released Redis lock [{}]", lockKey);
                return true;
            }
        } catch (Exception e) {
            logger.warn("Redis error releasing lock [{}]: {}", lockKey, e.getMessage());
        }
        return false;
    }
}

