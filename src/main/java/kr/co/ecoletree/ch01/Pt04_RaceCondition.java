/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : HyungSeok Kim
 * Create Date : 2022. 02. 16.
 * File Name : Pt04_RaceCondition.java
 * DESC : 동시성 문제 Race Condition
 *****************************************************************/
package kr.co.ecoletree.ch01;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Pt04_RaceCondition {

    public static final int THREAD_CNT = 3000;
    private int number = THREAD_CNT;
    private AtomicInteger cnt = new AtomicInteger();
    private final Random rnd = new Random();

    private final CyclicBarrier barrier = new CyclicBarrier(THREAD_CNT);

    private final Lock lock = new ReentrantLock();

    private Runnable consume = () -> {
        log.info("Number: {}", cnt.incrementAndGet());
    };

    private Runnable update = () -> {
        try {
            barrier.await();
//            System.out.printf("Number is %d\n", number--);
            number--;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    };

    public static void main(String[] args) throws InterruptedException {
        final ExecutorService es = Executors.newFixedThreadPool(THREAD_CNT);
        final Pt04_RaceCondition a = new Pt04_RaceCondition();

        IntStream.range(0, THREAD_CNT).forEach(i -> es.execute(a.update));

        es.shutdown();

        TimeUnit.SECONDS.sleep(2);
        System.out.println(a.number);
        // 1. Mutation Lock
        // 1.1. java.util.concurrent.locks.Lock
        // 1.2. java.util.concurrent.Semaphore
        // 1.3. synchronized

        // 2. Using Thread-safe data structure
        // Atomic...

        // 3. ThreadLocal
    }

    class Account {
        private int total;

        public int fetch(final int amount) {
            if (total > 0 && total > amount) {
                log.info("Fetching... {}", amount);
                return total -= amount;
            }
            return 0;
        }
    }
}
