/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : HyungSeok Kim
 * Create Date : 2022. 02. 16.
 * File Name : Pt02_Execute_Code.java
 * DESC : 비동기 로직 실행 (with Runnable & Callable)
 *****************************************************************/
package kr.co.ecoletree.ch01;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Pt02_Execute_Code {
    private static final ThreadPoolTaskExecutor ES = new ThreadPoolTaskExecutor();

    static {
        ES.setCorePoolSize(2);
        ES.setMaxPoolSize(10);
        ES.setQueueCapacity(50);
        ES.setThreadNamePrefix("Ecoletree-");
        ES.initialize();
    }

    private static boolean asynchronouslyUpdateStateWithRunnable() {
        boolean plsUpdateThis = false;

        // TODO Runnable을 사용하여 비동기적으로 asynchronouslyUpdateStateWithRunnable() 반환값을 true로 변경한다.

        return plsUpdateThis;
    }

    private static boolean asynchronouslyUpdateState() throws Exception {
        final Callable<Boolean> c = () -> true;
        return c.call();
    }

    private static void run() throws ExecutionException, InterruptedException {
        final Future<Boolean> future = ES.submit(() -> {
            TimeUnit.SECONDS.sleep(1);
            log.info("Async Done");
            return true;
        });
        log.info("Asynchronously State Update from Future: {}", future.get());
    }

    public static void main(String[] args) throws Exception {
//        System.out.printf("Asynchronously State Update with Runnable: %s\n", asynchronouslyUpdateStateWithRunnable());
        log.info("Asynchronously State Update: {}", asynchronouslyUpdateState());

        run();

        ES.shutdown();
        System.out.println("finished");
    }
}
