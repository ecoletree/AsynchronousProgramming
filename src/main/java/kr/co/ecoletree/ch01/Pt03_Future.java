/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : HyungSeok Kim
 * Create Date : 2022. 02. 16.
 * File Name : Pt03_Future.java
 * DESC : Continuous processing using FutureTask, ListenableFuture & CompletableFuture. (e.g. CPS(Continuation Passing Style))
 *****************************************************************/
package kr.co.ecoletree.ch01;

import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;
import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import kr.co.ecoletree.common.exception.ETException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Pt03_Future {

    private static final ExecutorService ES = Executors.newFixedThreadPool(5);

    private static String handleWithFutureTask(final Callable<String> c) throws ExecutionException, InterruptedException {
        final FutureTask<String> ft = new FutureTask<>(c);
        ft.run();
        while (ft.isDone()) {
            return ft.get();
        }
        return null;
    }

    private static void handleWithListenableFuture(final Callable<String> callable) {
        final ListenableFutureTask<String> task
                = new ListenableFutureTask<>(callable);
        task.addCallback(
                result -> {
                    log.info("ListenableFuture Result: {}", result);
                },
                ex -> {
                    log.error("Error message: {}", ex.getMessage(), ex);
                }
        );
        task.run();
    }

    private static void handleWithCompletableFuture() throws InterruptedException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        future
                .thenApply(result -> result + "!")
                .thenAccept(result -> log.info("Result1: {}", result))
                .thenAcceptAsync(result -> log.info("Result2: {}", result))
                .thenAcceptAsync(result -> log.info("Result3: {}", result))
                .thenAcceptAsync(result -> log.info("Result4: {}", result))
                .thenAcceptAsync(result -> log.info("Result5: {}", result))
                .thenAcceptAsync(result -> log.info("Result6: {}", result))
                .thenAcceptAsync(result -> log.info("Result7: {}", result))
                .exceptionally(t -> {
                    log.error("Msg: {}", t.getMessage(), t);
                    return (Void) null;
                });

        ES.execute(() -> {
            try { TimeUnit.MILLISECONDS.sleep(300); }
            catch (InterruptedException ignore) {}
            future.complete("completed");
//            future.completeExceptionally(new ETException("Failed"));
        });
    }

    private static void plsInvokeMe(final Object asyncResult) {
        log.info("Invoked with {}", asyncResult);
    }

    public static void main(String[] args) throws Exception {
        final Callable<String> callable = new GenerateValue();
//        System.out.println(handleWithFutureTask(callable));
//        ES.execute(() -> handleWithListenableFuture(callable));
        handleWithCompletableFuture();
        ES.shutdown();
        System.out.println("finished");

    }

    static class GenerateValue implements Callable<String> {

        @Override
        public String call() throws Exception {
            TimeUnit.SECONDS.sleep(1);
            return "Completed";
        }
    }
}
