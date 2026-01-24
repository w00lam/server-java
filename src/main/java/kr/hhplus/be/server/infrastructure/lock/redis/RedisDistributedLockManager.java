package kr.hhplus.be.server.infrastructure.lock.redis;

import kr.hhplus.be.server.infrastructure.lock.DistributedLockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisDistributedLockManager implements DistributedLockManager {
    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public String lock(String key, Duration ttl) {
        String lockValue = UUID.randomUUID().toString();

        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, lockValue, ttl);

        if(Boolean.TRUE.equals(success)){
            return lockValue;
        }
        return null;
    }

    @Override
    public void unlock(String key, String lockValue) {
        String currentValue = redisTemplate.opsForValue().get(key);

        if(lockValue.equals(currentValue)){
            redisTemplate.delete(key);
        }
    }
}
