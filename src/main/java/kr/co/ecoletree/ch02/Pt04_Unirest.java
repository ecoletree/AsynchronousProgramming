/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : HyungSeok Kim
 * Create Date : 2022. 02. 16.
 * File Name : Pt04_Unirest.java
 * DESC :
 *****************************************************************/
package kr.co.ecoletree.ch02;

import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kr.co.ecoletree.common.helper.MapBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Pt04_Unirest {
    private static final String url = "http://101.101.217.170:8080/Boxwood_portal_demo/demo/infer/rdd";
    private static final Map<String, Object> body = MapBuilder.of(
            "po_num", "11111111",
            "provider","abvc",
            "total", "12",
            "rdd", "2021-12-10 14:51:33",
            "user_id", "hskim@ecoletree.com",
            "apiKey", "asdasd"
    );

    public static void main(String[] args) throws InterruptedException {
        final CompletableFuture<HttpResponse<Map<String, Object>>> response = Unirest.post(url)
                .accept("application/json")
                .contentType("application/json")
                .body(body)
                .asObjectAsync(new GenericType<Map<String, Object>>() {});
//                .map(res -> {
//                    try {
//                        return new ObjectMapper().readValue(res, Map.class);
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                });
    }
}
