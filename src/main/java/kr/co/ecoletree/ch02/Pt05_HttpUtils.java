/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : HyungSeok Kim
 * Create Date : 2022. 02. 16.
 * File Name : Pt05_HttpUtils.java
 * DESC :
 *****************************************************************/
package kr.co.ecoletree.ch02;

import kr.co.ecoletree.common.helper.HttpClientUtil;

public class Pt05_HttpUtils {

    public static void main(String[] args) {
        HttpClientUtil.post("")
                .body("")
                .exchangeAsMapAsync();
    }
}
