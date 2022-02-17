/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : HyungSeok Kim
 * Create Date : 2022. 02. 16.
 * File Name : Pt03_WebClient.java
 * DESC : springframework reactive http 요청
 *****************************************************************/
package kr.co.ecoletree.ch02;

import org.springframework.http.MediaType;

import java.util.Map;

import kr.co.ecoletree.common.helper.MapBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Pt03_WebClient {

//    private static void testHttp() {
//        final String url = "http://101.101.217.170:8080/Boxwood_portal_demo/demo/infer/rdd";
//        final Map<String, Object> body = MapBuilder.of(
//                "po_num", "11111111",
//                "provider","abvc",
//                "total", "12",
//                "rdd", "2021-12-10 14:51:33",
//                "user_id", "hskim@ecoletree.com",
//                "apiKey", "asdasd"
//        );
//
//        final String responseBody = WebClient.create(url)
//                .post()
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(body))
//                .retrieve()
//                .bodyToMono(Map.class)
//                .flatMap(rBody -> WebClient.create(url).post().body(BodyInserters.fromValue(rBody)).retrieve().bodyToMono(String.class))
//                .block();
//        log.info("Response Body: {}", body);
//    }
//
//    public static void main(String[] args) {
//        testHttp();
//    }
}
