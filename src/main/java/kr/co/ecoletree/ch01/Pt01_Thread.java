/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : HyungSeok Kim
 * Create Date : 2022. 02. 16.
 * File Name : Pt01_Thread_1.java
 * DESC : 자바 쓰레드 주요 개념 설명 (1)
 *****************************************************************/
package kr.co.ecoletree.ch01;

import java.util.stream.Stream;

public class Pt01_Thread {

    /**
     * anonymous 클래스로 구현하여 Thread 실행
     */
    private static Thread testNormalThread() {
        return null;
    }

    /**
     * Runnable 사용하여 Thread 실행
     */
    private static Thread testNormalThread(final Runnable runnable) {
        return null;
    }

    /**
     * Daemon Thread
     *
     * Daemon Thread: Main Thread 종료되면 따라서 종료된다.
     * 객체 제거되는지 finalize() 구현 필요
     *
     * @return
     */
    private static Thread testDaemonThread() {
        return null;
    }

    private static void printMessage(final String messageFormat, final Object... o) {
        final Object[] params = Stream.concat(Stream.of(Thread.currentThread().getName()), Stream.of(o)).toArray(Object[]::new);
        System.out.printf("[%s]\t\t\t" + messageFormat + "\n", params);
    }

    public static void main(String[] args) {
        final Runnable r1 = () -> {};

        testNormalThread().start();
        testNormalThread(r1).start();

        testDaemonThread().start();
    }
}
