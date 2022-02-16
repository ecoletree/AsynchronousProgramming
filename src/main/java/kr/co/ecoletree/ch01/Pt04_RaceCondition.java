/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : HyungSeok Kim
 * Create Date : 2022. 02. 16.
 * File Name : Pt04_RaceCondition.java
 * DESC : 동시성 문제 Race Condition
 *****************************************************************/
package kr.co.ecoletree.ch01;

import java.util.concurrent.CyclicBarrier;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Pt04_RaceCondition {

    public static void startRace(final Runnable runnable, final int threadCount) {
        final CyclicBarrier cb = new CyclicBarrier(threadCount);

    }

    public static void main(String[] args) {
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
