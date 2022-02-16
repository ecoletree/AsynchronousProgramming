/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : HyungSeok Kim
 * Create Date : 2022. 02. 16.
 * File Name : Pt02_Execute_Code.java
 * DESC : 비동기 로직 실행 (with Runnable & Callable)
 *****************************************************************/
package kr.co.ecoletree.ch01;

import java.util.concurrent.ExecutorService;

public class Pt02_Execute_Code {
    private static final ExecutorService ES = null;

    private static boolean asynchronouslyUpdateStateWithRunnable() {
        boolean plsUpdateThis = false;

        // TODO Runnable을 사용하여 비동기적으로 asynchronouslyUpdateStateWithRunnable() 반환값을 true로 변경한다.

        return plsUpdateThis;
    }

    private static boolean asynchronouslyUpdateState() {
        boolean plsUpdateThis = false;
        return plsUpdateThis;
    }

    public static void main(String[] args) {
        System.out.printf("Asynchronously State Update with Runnable: %s\n", asynchronouslyUpdateStateWithRunnable());
        System.out.printf("Asynchronously State Update: %s\n", asynchronouslyUpdateState());
    }
}
