package com.example.stock.facade;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Component;

/**
 * LettuceLockStockFacade
 * <pre>
 * Describe here
 * </pre>
 *
 * @version 1.0,
 */

@Component
public class LettuceLockStockFacade {

    private final RedisLockRepository redisLockRepository;
    private final StockService stockService;

    public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
        this.redisLockRepository = redisLockRepository;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
        // lock 획득 실패 시 다시 일정 시간 뒤 다시 시도 하도록 작성
        while(!redisLockRepository.lock(id)) {
            Thread.sleep(100);
        }

        // lock 획득 시 재고 감소 > 마지막으로 lock 해제
        try {
            stockService.decrease(id, quantity);
        } finally {
            redisLockRepository.unlock(id);
        }
    }
}
