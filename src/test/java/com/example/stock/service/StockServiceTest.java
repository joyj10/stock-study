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
 * StockServiceTest
 * <pre>
 * Describe here
 * </pre>
 *
 * @version 1.0,
 */
@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

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
    public void 재고감소() {
        stockService.decrease(1L, 1L);

        // 100 - 1 = 99
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(99, stock.getQuantity());
    }

    /**
     * 해당 테스트 실패
     * > Race Condition 발생으로 테스트 실패
     *  ㄴ Race Condition : 둘 이상의 스레드가 공유 데이터에 access 할 수 있고, 동시에 변경을 하려고 할 때 발생할 수 있는 문제
     * > 해결 하기 위해서는 한개의 스레드만 데이터에 접근 가능하도록 해야 함
     */
    @Test
    public void 동시에_100개의_요청_기본() throws InterruptedException {
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

    /**
     * 자바 단에서 동시성 해결
     * synchronized
     * 참고) @Transactional 은 메서드 삭제 필요, 트랜젝션 종료 되어야 DB에 반영 되는데, 종료 전에 다른 스레드 접근이 가능해서 트랜젝션 어노테이션 삭제 필요
     * 단점) 하나의 프로세스 안에서만 보장이 됨(서버가 한대일 때만 가능) 서버가 여러 대라면 데이터 동시 접근 가능
     */
    @Test
    public void 동시에_100개의_요청_synchronized() throws InterruptedException {
        int threadCount = 100;

        // ExecutorService : 비동기로 실행하는 작업을 단순화 하여 사용할 수 있게 도와주는 java API, 멀티 스레드 테스트 위해 사용
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // CountDownLatch : 다른 스레드에서 수행 중인 작업이 완료 될 때 까지 대기 할 수 있게 도와주는 클래스  , 100개의 요청이 모두 끝날 때 까지 기다릴 수 있게 사용
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0 ; i < threadCount ; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease_synchronized(1L, 1L);
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
