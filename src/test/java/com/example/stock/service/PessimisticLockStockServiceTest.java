package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PessimisticLockStockServiceTest
 * <pre>
 * Describe here
 * </pre>
 *
 * @version 1.0,
 */

@SpringBootTest
class PessimisticLockStockServiceTest {

    @Autowired
    private PessimisticLockStockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before() {
        stockRepository.saveAndFlush(new Stock(1L, 100L));
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void 동시에_100개의_요청() throws InterruptedException {
        int threadCount = 100;

        // ExecutorService : 비동기로 실행하는 작업을 단순화 하여 사용할 수 있게 도와주는 java API, 멀티 스레드 테스트 위해 사용
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // CountDownLatch : 다른 스레드에서 수행 중인 작업이 완료 될 때 까지 대기 할 수 있게 도와주는 클래스  , 100개의 요청이 모두 끝날 때 까지 기다릴 수 있게 사용
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0 ; i < threadCount ; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        // 100 - (1*100) = 0
        assertEquals(0, stock.getQuantity());
    }
}
