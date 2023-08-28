package com.example.stock.facade;

import com.example.stock.repository.LockRepository;
import com.example.stock.service.NamedLockStockService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Named Lock
 * - 이름을 가진 락을 획득한 후 해제될 떄 까지 다른 세션은 이 락을 획득할 수 없음
 * - 주의점 : 트랜젝션이 끝날 떄 락이 자동으로 해제되지 않아 별도의 명령으로 해제해주거나 시간이 지나야함
 * - Pessimistic lock stock 에 락을 걸지만 Named는 별도의 공간에 락을 걸어줌
 * - Pessimistic lock 타임 아웃을 구현하기 힘들지만 네임드 락은 쉬움
 * - 주로 분산락을 구현할 때, 삽입시 데이터 정합성을 맞춰야 할 때 사용
 * - 트랜젝션 종료시 락 해제와 세션 관리를 잘 해야함
 */
@Component
public class NamedLockStockFacade {

    private final LockRepository lockRepository;
    private final NamedLockStockService stockService;

    public NamedLockStockFacade(LockRepository lockRepository, NamedLockStockService stockService) {
        this.lockRepository = lockRepository;
        this.stockService = stockService;
    }

    @Transactional
    public void decrease(Long id, Long quantity) {
        try {
            lockRepository.getLock(String.valueOf(id));
            stockService.decrease(id, quantity);
        } finally {
            lockRepository.releaseLock(String.valueOf(id));
        }
    }
}
