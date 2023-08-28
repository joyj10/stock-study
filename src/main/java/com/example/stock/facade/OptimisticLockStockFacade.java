package com.example.stock.facade;

import com.example.stock.service.OptimisticLockStockService;
import org.springframework.stereotype.Component;

/**
 * OptimisticLockStockFacade
 * <pre>
 * Describe here
 * </pre>
 *
 * @version 1.0,
 */

@Component
public class OptimisticLockStockFacade {
    private final OptimisticLockStockService optimisticLockStockService;

    public OptimisticLockStockFacade(OptimisticLockStockService optimisticLockStockService) {
        this.optimisticLockStockService = optimisticLockStockService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (true) {
            try {
                optimisticLockStockService.decrease(id, quantity);

                // 정상적으로 업데이트 되는 경우 break 활용하며 while문 빠져나옴
                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }
}
