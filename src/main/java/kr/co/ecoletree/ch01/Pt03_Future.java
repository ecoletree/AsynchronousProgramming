/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : HyungSeok Kim
 * Create Date : 2022. 02. 16.
 * File Name : Pt03_Future.java
 * DESC : Continuous processing using FutureTask, ListenableFuture & CompletableFuture. (e.g. CPS(Continuation Passing Style))
 *****************************************************************/
package kr.co.ecoletree.ch01;

import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Pt03_Future {

    private static void handleWithFutureTask() {

    }

    private static void handleWithListenableFuture() {

    }

    private static void handleWithCompletableFuture() {

    }

    private static void plsInvokeMe(final Object asyncResult) {
        log.info("Invoked with {}", asyncResult);
    }

    public static void main(String[] args) {

    }

    static class GenerateValue implements Callable<String> {

        @Override
        public String call() throws Exception {
            return "Completed";
        }
    }
}
