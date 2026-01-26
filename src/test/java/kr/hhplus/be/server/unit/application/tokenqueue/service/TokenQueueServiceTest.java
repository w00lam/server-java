package kr.hhplus.be.server.unit.application.tokenqueue.service;

import kr.hhplus.be.server.application.tokenqueue.port.in.TokenQueueUseCase;
import kr.hhplus.be.server.application.tokenqueue.port.out.TokenQueueRepositoryPort;
import kr.hhplus.be.server.application.tokenqueue.service.TokenQueueUseCaseImpl;
import kr.hhplus.be.server.infrastructure.persistence.tokenqueue.adapter.TokenQueueRepositoryImpl;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TokenQueueServiceTest extends BaseUnitTest {
    private TokenQueueUseCase tokenQueueService;
    private TokenQueueRepositoryPort repository;
    private RedisTemplate<String, String> redisTemplate;
    private ZSetOperations<String, String> zSetOperations;


    @BeforeEach
    void 테스트_준비() {
        redisTemplate = Mockito.mock(RedisTemplate.class);
        zSetOperations = Mockito.mock(ZSetOperations.class);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        repository = new TokenQueueRepositoryImpl(redisTemplate);
        tokenQueueService = new TokenQueueUseCaseImpl(repository);
    }


    @Test
    void 유저_추가시_Redis에_정상_추가되는지() {
        String userId = fixedUUID().toString();
        tokenQueueService.enqueueUser(userId);

        verify(redisTemplate.opsForZSet(), times(1))
                .add(eq("queue:token"), eq(userId), anyDouble());
    }

    @Test
    void 대기열_길이_조회가_정상적인지() {
        when(zSetOperations.zCard("queue:token")).thenReturn(3L);

        Integer length = tokenQueueService.getQueueLength();
        assertThat(length).isEqualTo(3);
    }

    @Test
    void 유저_순위_조회가_정상적인지() {
        when(zSetOperations.rank("queue:token", "user1")).thenReturn(0L);
        when(zSetOperations.rank("queue:token", "user2")).thenReturn(1L);

        Integer rank1 = tokenQueueService.getUserRank("user1");
        Integer rank2 = tokenQueueService.getUserRank("user2");

        assertThat(rank1).isEqualTo(1);
        assertThat(rank2).isEqualTo(2);
    }

    @Test
    void 대기열에서_첫번째_유저_제거가_정상적인지() {
        when(zSetOperations.range("queue:token", 0, 0)).thenReturn(Set.of("user1"));

        tokenQueueService.dequeueUser();

        verify(redisTemplate.opsForZSet(), times(1)).remove("queue:token", "user1");
    }

    @Test
    void 대기열_첫번째_유저_조회가_정상적인지() {
        when(zSetOperations.range("queue:token", 0, 0)).thenReturn(Set.of("user1"));

        String nextUser = tokenQueueService.getNextUser();

        assertThat(nextUser).isEqualTo("user1");
    }
}
