/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : HyungSeok Kim
 * Create Date : 2022. 02. 16.
 * File Name : Pt02_RestTemplate.java
 * DESC : springframework 사용하여 Http 요청
 *****************************************************************/
package kr.co.ecoletree.ch02;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.http.HttpHeaders.*;

import kr.co.ecoletree.common.helper.MapBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Pt02_RestTemplate {

    private static void httpTest() {
        final String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=a";
        final RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        System.err.println(result.getBody());
    }

    private static void asyncHttpTest() {
        final AsyncRestTemplate restTemplate = new AsyncRestTemplate();
        final String url = "http://101.101.217.170:8080/Boxwood_portal_demo/emo/infer/rdd";
        final Map<String, Object> body = MapBuilder.of(
                "po_num", "11111111",
                "provider","abvc",
                "total", "12",
                "rdd", "2021-12-10 14:51:33",
                "user_id", "hskim@ecoletree.com",
                "apiKey", "asdasd"
        );
        final Map<String, List<String>> headers = MapBuilder.of(
                CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE),
                ACCEPT, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE)
        );
        final HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, new MultiValueMapAdapter<>(headers));
        restTemplate.postForEntity(
                        url
                        , request
                        , String.class)

                .addCallback(response -> {
                    log.info("Response: {}", response);
                }, e -> {
                    log.error(e.getMessage());
                });

    }

    public static void main(String[] args) {
//        httpTest();
        asyncHttpTest();
    }
}
