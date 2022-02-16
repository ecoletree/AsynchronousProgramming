/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : HyungSeok Kim
 * Create Date : 2021. 10. 19.
 * File Name : HttpUtil.java
 * DESC : HTTP Client 유틸리티 클래스 (using Unirest)
 *****************************************************************/
package kr.co.ecoletree.common.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.CopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import kong.unirest.Callback;
import kong.unirest.Cookie;
import kong.unirest.Empty;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.MultipartBody;
import kong.unirest.RawResponse;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;
import kr.co.ecoletree.common.exception.ETRuntimeException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter(AccessLevel.PROTECTED)
@Getter
public abstract class HttpClientUtil<T extends HttpRequest<T>> {
    protected String url;
    protected String method;
    protected String contentType;
    protected String accept;
    protected Map<String, String> headers;
    protected Map<String, Object> requestParameter;
    protected Map<String, Object> queryParameters;
    protected Map<String, Object> routeParameters;
    protected List<Cookie> cookies;
    protected Duration connectionTimeout;
    protected Duration socketTimeout;
    protected T request;

    private HttpClientUtil(final String url, final String method, final T request) {
        this.request = request;
        this.url = url;
        this.method = method;
        this.connectionTimeout = Duration.ofSeconds(5);
        this.socketTimeout = Duration.ofSeconds(30);
    }

    /**
     * HTTP GET Request 빌더 생성
     *
     * @param url
     * @return
     */
    public static Get get(final String url) {
        return new Get(url);
    }

    /**
     * HTTP DELETE Request 빌더 생성
     *
     * @param url
     * @return
     */
    public static Delete delete(final String url) {
        return new Delete(url);
    }

    /**
     * HTTP POST Request 빌더 생성
     *
     * @param url
     * @return
     */
    public static Post post(final String url) {
        return new Post(url);
    }

    /**
     * HTTP PUT Request 빌더 생성
     *
     * @param url
     * @return
     */
    public static Put put(final String url) {
        return new Put(url);
    }

    /**
     * [Optional] Route Parameters 지정.
     *
     * Example
     *
     * URL: http://localhost:8080/user/{name}/{department}
     * routeParameters:
     * {
     *     "name": "Henry",
     *     "department": "HR"
     * }
     *
     * Result URL = "http://localhost:8080/user/Henry/HR"
     *
     * @param routeParameters
     * @return
     */
    public HttpClientUtil<T> routeParams(final Map<String, Object> routeParameters) {
        updateRequest(req -> req.routeParam(routeParameters));
        setRouteParameters(routeParameters);
        return this;
    }

    /**
     * [Optional] Query String 지정.
     *
     * @param queryParameters
     * @return
     */
    public HttpClientUtil<T> queryParams(final Map<String, Object> queryParameters) {
        updateRequest(req -> req.queryString(queryParameters));
        setQueryParameters(queryParameters);
        return this;
    }

    /**
     * [Optional] HTTP Headers 지정.
     *
     * @param headers
     * @return
     */
    public HttpClientUtil<T> headers(final Map<String, String> headers) {
        updateRequest(req -> req.headers(headers));
        setHeaders(headers);
        return this;
    }

    /**
     * [Optional] HTTP Header 중 Accept 값 지정.
     *
     * @param accept
     * @return
     */
    public HttpClientUtil<T> accept(final String accept) {
        updateRequest(req -> req.accept(accept));
        setAccept(accept);
        return this;
    }

    /**
     * [Optional] HTTP 인증 방식 중 BasicAuth 관련 데이터 지정.
     *
     * @param user
     * @param pwd
     * @return
     */
    public HttpClientUtil<T> basicAuth(final String user, final String pwd) {
        updateRequest(req -> req.basicAuth(user, pwd));
        return this;
    }

    /**
     * [Optional] HTTP Cookies 지정.
     *
     * @param cookies
     * @return
     */
    public HttpClientUtil<T> cookies(final Map<String, String> cookies) {
        return cookies(cookies.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getKey()))
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> new Cookie(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));
    }

    /**
     * [Optional] HTTP Cookies 지정.
     *
     * @param cookies
     * @return
     */
    public HttpClientUtil<T> cookies(final List<Cookie> cookies) {
        updateRequest(req -> req.cookie(cookies));
        setCookies(cookies);
        return this;
    }

    /**
     * HTTP Connection timeout 시간 지정.
     *
     * Connection Timeout: Client -> Server 연결 수립에 걸리는 시간이 지정한 시간보다 초과된 경우
     *
     * @param timeout
     * @return
     */
    public HttpClientUtil<T> connectionTimeout(final Duration timeout) {
        setConnectionTimeout(timeout);
        updateRequest(req -> req.connectTimeout(getConnectionTimeoutMillis()));
        return this;
    }

    /**
     * HTTP Socket timeout 시간 지정.
     *
     * Socket Timeout: HTTP 요청부터 응답 받기까지 걸리는 모든 시간이 지정한 시간보다 초과된 경우.
     *
     * @param timeout
     * @return
     */
    public HttpClientUtil<T> socketTimeout(final Duration timeout) {
        setSocketTimeout(timeout);
        updateRequest(req -> req.socketTimeout(getSocketTimeoutMillis()));
        return this;
    }

    public int getConnectionTimeoutMillis() {
        return (int) getConnectionTimeout().toMillis();
    }

    public int getSocketTimeoutMillis() {
        return (int) getSocketTimeout().toMillis();
    }

    /**
     * Set Method 로 처리하기 힘든 객체 업데이트 수행
     * @param fn
     */
    public void updateRequest(final Function<T, ? extends T> fn) {
        this.request = fn.apply(request);
    }


    ///////////////////////////////////////////
    ///               Start                 ///
    ///  Declarations of blocking exchange  ///
    ///////////////////////////////////////////

    /**
     * (Blocking) HTTP 요청 실행, Response Body 없는 경우
     *
     * @return
     */
    public Exchange<?> exchangeEmpty() {
        return Exchange.exchangeEmpty(getRequest());
    }
    /**
     * (Blocking) HTTP 요청 실행, 지정된 자바 클래스 타입으로 받음.
     *
     * @param clazz Response Body 타입 지정
     * @param <R>
     * @return
     */
    public <R> Exchange<R> exchange(final Class<? extends R> clazz) {
        return Exchange.exchangeAsObject(getRequest(), clazz);
    }

    /**
     * (Blocking) HTTP 요청 실행
     *
     * @param mapper Response Handler Function = response -> responseBody
     * @param <R>
     * @return
     */
    public <R> Exchange<R> exchange(final Function<RawResponse, R> mapper) {
        return Exchange.exchangeAsObject(getRequest(), mapper);
    }

    /**
     * (Blocking) HTTP 요청 실행, Response Body 를 문자열로 받음.
     *
     * @return
     */
    public Exchange<String> exchangeAsString() {
        return Exchange.exchangeAsString(getRequest());
    }

    /**
     * (Blocking) HTTP 요청 실행, Response Body 파일인 경우.
     *
     * @param outputFilePath 응답 파일 저장 경로
     * @param copyOptions 파일 저장 옵션 {@link java.nio.file.StandardCopyOption}
     * @return
     */
    public Exchange<File> exchangeAsFile(final String outputFilePath, final CopyOption... copyOptions) {
        return Exchange.exchangeAsFile(getRequest(), outputFilePath, copyOptions);
    }

    /**
     * (Blocking) HTTP 요청 실행, Response Body java.util.Map 객체로 받음.
     * @return
     */
    public Exchange<Map<String, Object>> exchangeAsMap() {
        return Exchange.exchangeAsObject(getRequest(), resp -> HttpClientUtil.responseBody2Map(resp, HttpClientUtil::isResponseStatus2xx));
    }
    ///////////////////////////////////////////
    ///                End                  ///
    ///  Declarations of blocking exchange  ///
    ///////////////////////////////////////////

    ///////////////////////////////////////////////
    ///                 Start                   ///
    ///  Declarations of non-blocking exchange  ///
    ///////////////////////////////////////////////
    /**
     * (Non-Blocking) HTTP 요청 실행, Response Body 없는 경우.
     * @return
     */
    public AsyncExchange<Empty> exchangeEmptyAsync() {
        return AsyncExchange.exchangeEmpty(getRequest());
    }

    /**
     * (Non-Blocking) HTTP 요청 실행, Response Body 문자열로 받음.
     *
     * @return
     */
    public AsyncExchange<String> exchangeAsStringAsync() {
        return AsyncExchange.exchangeAsString(getRequest());
    }

    /**
     * (Non-Blocking) HTTP 요청 실행, Response Body 문자열로 받음.
     *
     * @param callback ResponseBody callback function (a.k.a. ajax.success)
     * @return
     */
    public AsyncExchange<String> exchangeAsStringAsync(final Callback<String> callback) {
        return AsyncExchange.exchangeAsString(getRequest(), callback);
    }

    /**
     * (Non-Blocking) HTTP 요청 실행, Response Body 지정된 자바 클래스 타입으로 받음.
     *
     * @param clazz
     * @param <R>
     * @return
     */
    public <R> AsyncExchange<R> exchangeAsObjectAsync(final Class<? extends R> clazz) {
        return AsyncExchange.exchangeAsObject(getRequest(), clazz);
    }

    /**
     * (Non-Blocking) HTTP 요청 실행, Response Body 지정된 자바 클래스 타입으로 받음.
     *
     * @param clazz Response Body 타입 지정
     * @param callback ResponseBody callback function (a.k.a. ajax.success)
     * @param <R>
     * @return
     */
    public <R> AsyncExchange<R> exchangeAsObjectAsync(final Class<? extends R> clazz, final Callback<R> callback) {
        return AsyncExchange.exchangeAsObject(getRequest(), clazz, callback);
    }

    /**
     * (Non-Blocking) HTTP 요청 실행, Response Body 지정된 자바 클래스 타입으로 받음.
     *
     * @param mapper Response Handler Function = response -> responseBody
     * @param <R>
     * @return
     */
    public <R> AsyncExchange<R> exchangeAsObjectAsync(final Function<RawResponse, R> mapper) {
        return AsyncExchange.exchangeAsObject(getRequest(), mapper);
    }

    /**
     * (Non-Blocking) HTTP 요청 실행, Response Body 파일인 경우.
     *
     * @param outputFilePath 응답 파일 저장 경로
     * @param copyOptions 파일 저장 옵션 {@link java.nio.file.StandardCopyOption}
     * @return
     */
    public AsyncExchange<File> exchangeAsFileAsync(final String outputFilePath, final CopyOption... copyOptions) {
        return AsyncExchange.exchangeAsFile(getRequest(), outputFilePath, copyOptions);
    }

    /**
     * (Non-Blocking) HTTP 요청 실행, Response Body 파일인 경우.
     *
     * @param outputFilePath 응답 파일 저장 경로
     * @param callback Response Handler Function = response -> responseBody(File)
     * @param copyOptions 파일 저장 옵션 {@link java.nio.file.StandardCopyOption}
     * @return
     */
    public AsyncExchange<File> exchangeAsFileAsync(final String outputFilePath, final Callback<File> callback, final CopyOption... copyOptions) {
        return AsyncExchange.exchangeAsFile(getRequest(), outputFilePath, callback, copyOptions);
    }

    /**
     * (Non-Blocking) HTTP 요청 실행, Response Body java.util.Map 객체로 받음.
     *
     * @param responsePredicate Response 성공 여부 확인 function
     * @return
     */
    public AsyncExchange<Map<String, Object>> exchangeAsMapAsync(final Predicate<? super RawResponse> responsePredicate) {
        return AsyncExchange.exchangeAsMap(getRequest(), responsePredicate);
    }

    /**
     * (Non-Blocking) HTTP 요청 실행, Response Body java.util.Map 객체로 받음.
     *
     * @return
     */
    public AsyncExchange<Map<String, Object>> exchangeAsMapAsync() {
        return AsyncExchange.exchangeAsMap(getRequest());
    }
    ///////////////////////////////////////////////
    ///                  End                    ///
    ///  Declarations of non-blocking exchange  ///
    ///////////////////////////////////////////////

    ////////////////////////////////////////
    ///              Start               ///
    ///  Declarations of static classes  ///
    ////////////////////////////////////////

    /**
     * HTTP GET 요청 빌더 클래스
     */
    public static class Get extends HttpClientUtil<GetRequest> {
        public Get(final String url) {
            super(url, "GET", Unirest.get(url));
        }

        @Override
        public Get routeParams(final Map<String, Object> routeParameters) {
            super.routeParams(routeParameters);
            return this;
        }

        @Override
        public Get queryParams(final Map<String, Object> queryParameters) {
            super.queryParams(queryParameters);
            return this;
        }

        @Override
        public Get headers(final Map<String, String> headers) {
            super.headers(headers);
            return this;
        }

        @Override
        public Get accept(final String accept) {
            super.accept(accept);
            return this;
        }

        @Override
        public Get basicAuth(final String user, final String pwd) {
            super.basicAuth(user, pwd);
            return this;
        }

        @Override
        public Get cookies(final Map<String, String> cookies) {
            super.cookies(cookies);
            return this;
        }

        @Override
        public Get cookies(final List<Cookie> cookies) {
            super.cookies(cookies);
            return this;
        }

        @Override
        public Get connectionTimeout(final Duration timeout) {
            super.connectionTimeout(timeout);
            return this;
        }

        @Override
        public Get socketTimeout(final Duration timeout) {
            super.socketTimeout(timeout);
            return this;
        }
    }

    /**
     * HTTP DELETE 요청 빌더 클래스
     */
    public static class Delete extends ElseMethod {
        public Delete(final String url) {
            super(url, "DELETE");
        }

        @Override
        public Delete routeParams(final Map<String, Object> routeParameters) {
            super.routeParams(routeParameters);
            return this;
        }

        @Override
        public Delete queryParams(final Map<String, Object> queryParameters) {
            super.queryParams(queryParameters);
            return this;
        }

        @Override
        public Delete headers(final Map<String, String> headers) {
            super.headers(headers);
            return this;
        }

        @Override
        public Delete accept(final String accept) {
            super.accept(accept);
            return this;
        }

        @Override
        public Delete basicAuth(final String user, final String pwd) {
            super.basicAuth(user, pwd);
            return this;
        }

        @Override
        public Delete cookies(final Map<String, String> cookies) {
            super.cookies(cookies);
            return this;
        }

        @Override
        public Delete cookies(final List<Cookie> cookies) {
            super.cookies(cookies);
            return this;
        }

        @Override
        public Delete connectionTimeout(final Duration timeout) {
            super.connectionTimeout(timeout);
            return this;
        }

        @Override
        public Delete socketTimeout(final Duration timeout) {
            super.socketTimeout(timeout);
            return this;
        }

        @Override
        public Delete contentType(final String contentType) {
            super.contentType(contentType);
            return this;
        }
    }

    /**
     * HTTP POST 요청 빌더 클래스
     */
    public static class Post extends ElseMethod {
        public Post(final String url) {
            super(url, "POST");
        }

        @Override
        public Post routeParams(final Map<String, Object> routeParameters) {
            super.routeParams(routeParameters);
            return this;
        }

        @Override
        public Post queryParams(final Map<String, Object> queryParameters) {
            super.queryParams(queryParameters);
            return this;
        }

        @Override
        public Post headers(final Map<String, String> headers) {
            super.headers(headers);
            return this;
        }

        @Override
        public Post accept(final String accept) {
            super.accept(accept);
            return this;
        }

        @Override
        public Post basicAuth(final String user, final String pwd) {
            super.basicAuth(user, pwd);
            return this;
        }

        @Override
        public Post cookies(final Map<String, String> cookies) {
            super.cookies(cookies);
            return this;
        }

        @Override
        public Post cookies(final List<Cookie> cookies) {
            super.cookies(cookies);
            return this;
        }

        @Override
        public Post connectionTimeout(final Duration timeout) {
            super.connectionTimeout(timeout);
            return this;
        }

        @Override
        public Post socketTimeout(final Duration timeout) {
            super.socketTimeout(timeout);
            return this;
        }

        @Override
        public Post contentType(final String contentType) {
            super.contentType(contentType);
            return this;
        }
    }

    /**
     * HTTP PUT 요청 빌더 클래스
     */
    public static class Put extends ElseMethod {
        public Put(final String url) {
            super(url, "PUT");
        }

        @Override
        public Put routeParams(final Map<String, Object> routeParameters) {
            super.routeParams(routeParameters);
            return this;
        }

        @Override
        public Put queryParams(final Map<String, Object> queryParameters) {
            super.queryParams(queryParameters);
            return this;
        }

        @Override
        public Put headers(final Map<String, String> headers) {
            super.headers(headers);
            return this;
        }

        @Override
        public Put accept(final String accept) {
            super.accept(accept);
            return this;
        }

        @Override
        public Put basicAuth(final String user, final String pwd) {
            super.basicAuth(user, pwd);
            return this;
        }

        @Override
        public Put cookies(final Map<String, String> cookies) {
            super.cookies(cookies);
            return this;
        }

        @Override
        public Put cookies(final List<Cookie> cookies) {
            super.cookies(cookies);
            return this;
        }

        @Override
        public Put connectionTimeout(final Duration timeout) {
            super.connectionTimeout(timeout);
            return this;
        }

        @Override
        public Put socketTimeout(final Duration timeout) {
            super.socketTimeout(timeout);
            return this;
        }

        @Override
        public Put contentType(final String contentType) {
            super.contentType(contentType);
            return this;
        }
    }

    /**
     * HTTP GET 이외의 요청(POST,PUT,DELETE) 빌더 클래스
     */
    public static abstract class ElseMethod extends HttpClientUtil<HttpRequestWithBody> {
        ElseMethod(final String url, final String method) {
            super(url, method.toUpperCase(), Unirest.request(method.toUpperCase(), url));
        }

        public Body body(final Object body) {
            return new Body(getUrl(), getMethod(), getRequest().body(body));
        }

        public FormData formData(final Map<String, Object> formData) {
            return new FormData(getUrl(), getMethod(), getRequest().fields(formData));
        }

        public ElseMethod contentType(final String contentType) {
            updateRequest(req -> req.contentType(contentType));
            setContentType(contentType);
            return this;
        }
    }

    /**
     * Request Body 사용하는 요청 빌더 클래스
     */
    public static class Body extends HttpClientUtil<RequestBodyEntity> {
        Body(final String url, final String method, final RequestBodyEntity request) {
            super(url, method, request);
            setContentType("application/json");
        }

        public Body contentType(final String contentType) {
            updateRequest(req -> req.contentType(contentType));
            setContentType(contentType);
            return this;
        }
    }

    /**
     * Request Parameter Form 사용하는 요청 빌더 클래스
     */
    public static class FormData extends HttpClientUtil<MultipartBody> {
        FormData(final String url, final String method, final MultipartBody request) {
            super(url, method, request);
        }

        public FormData contentType(final String contentType) {
            updateRequest(req -> req.contentType(contentType));
            setContentType(contentType);
            return this;
        }
    }

    /**
     * Blocking HTTP 요청 실행 클래스
     *
     * @param <R> HTTP Response Body 타입
     */
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Exchange<R> {
        private HttpRequest<?> request;
        private HttpResponse<R> response;

        /**
         * (Blocking) HTTP Request 실행, ResponseBody 문자열로 변환
         *
         * @see Exchange#responseBody()
         * @see Exchange#getResponse()
         *
         * @param request Unirest HttpRequest<?> 객체
         * @return
         */
        public static Exchange<String> exchangeAsString(final HttpRequest<?> request) {
            return new Exchange<>(request, request.asString());
        }

        /**
         * (Blocking) HTTP Request 실행, ResponseBody 지정된 자바 타입 객체 변환
         *
         * @see Exchange#responseBody()
         * @see Exchange#getResponse()
         *
         * @param request Unirest HttpRequest<?> 객체
         * @return
         */
        public static <R> Exchange<R> exchangeAsObject(final HttpRequest<?> request, final Class<? extends R> clazz) {
            return new Exchange<>(request, request.asObject(clazz));
        }

        /**
         * (Blocking) HTTP Request 실행, ResponseBody 지정된 함수를 적용하여 ResponseBody 반환
         *
         * @see Exchange#responseBody()
         * @see Exchange#getResponse()
         *
         * @param request Unirest HttpRequest<?> 객체
         * @return
         */
        public static <R> Exchange<R> exchangeAsObject(final HttpRequest<?> request, final Function<RawResponse, R> mapper) {
            return new Exchange<>(request, request.asObject(mapper));
        }

        /**
         * (Blocking) HTTP Request 실행, ResponseBody 파일 객체로 변환
         *
         * @see Exchange#responseBody()
         * @see Exchange#getResponse()
         *
         * @param request Unirest HttpRequest<?> 객체
         * @return
         */
        public static Exchange<File> exchangeAsFile(final HttpRequest<?> request, final String outputFilePath, final CopyOption... copyOptions) {
            return new Exchange<>(request, request.asFile(outputFilePath, copyOptions));
        }

        /**
         * (Blocking) HTTP Request 실행, ResponseBody 없음
         *
         * @see Exchange#getResponse()
         *
         * @param request Unirest HttpRequest<?> 객체
         * @return
         */
        public static Exchange<?> exchangeEmpty(final HttpRequest<?> request) {
            return new Exchange<>(request, request.asEmpty());
        }

        /**
         * 실행한 HTTP Request에 대한 ResponseBody 리턴
         *
         * @return
         */
        public R responseBody() {
            return response.getBody();
        }

        /**
         * 실행한 HTTP Request에 대한 Response status code 리턴
         *
         * @return
         */
        public int statusCode() {
            return response.getStatus();
        }
    }

    /**
     * Non-blocking HTTP 요청 실행 클래스
     *
     * @param <R> HTTP Response Body 타입
     */
    @AllArgsConstructor
    @Getter
    @Setter
    public static class AsyncExchange<R> {
        private HttpRequest<?> request;
        private CompletableFuture<HttpResponse<R>> response;

        /**
         * (Non-Blocking) HTTP 요청 실행, Response Body 없는 경우.
         * @return
         */
        public static AsyncExchange<Empty> exchangeEmpty(final HttpRequest<?> request) {
            return new AsyncExchange<>(request, request.asEmptyAsync());
        }

        /**
         * (Non-Blocking) HTTP 요청 실행, Response Body 문자열로 받음.
         *
         * @param request Unirest HttpRequest<?> 객체
         * @return
         */
        public static AsyncExchange<String> exchangeAsString(final HttpRequest<?> request) {
            return new AsyncExchange<>(request, request.asStringAsync());
        }

        /**
         * (Non-Blocking) HTTP 요청 실행, Response Body 문자열로 받음.
         *
         * @param request Unirest HttpRequest<?> 객체
         * @param callback ResponseBody callback function (a.k.a. ajax.success)
         * @return
         */
        public static AsyncExchange<String> exchangeAsString(final HttpRequest<?> request, final Callback<String> callback) {
            return new AsyncExchange<>(request, request.asStringAsync(callback));
        }

        /**
         * (Non-Blocking) HTTP 요청 실행, Response Body 지정된 자바 클래스 타입으로 받음.
         *
         * @param request Unirest HttpRequest<?> 객체
         * @param clazz
         * @param <R>
         * @return
         */
        public static <R> AsyncExchange<R> exchangeAsObject(final HttpRequest<?> request, final Class<? extends R> clazz) {
            return new AsyncExchange<>(request, request.asObjectAsync(clazz));
        }

        /**
         * (Non-Blocking) HTTP 요청 실행, Response Body 지정된 자바 클래스 타입으로 받음.
         *
         * @param request Unirest HttpRequest<?> 객체
         * @param clazz Response Body 타입 지정
         * @param callback ResponseBody callback function (a.k.a. ajax.success)
         * @param <R>
         * @return
         */
        public static <R> AsyncExchange<R> exchangeAsObject(final HttpRequest<?> request, final Class<? extends R> clazz, final Callback<R> callback) {
            return new AsyncExchange<>(request, request.asObjectAsync(clazz, callback));
        }

        /**
         * (Non-Blocking) HTTP 요청 실행, Response Body 지정된 자바 클래스 타입으로 받음.
         *
         * @param request Unirest HttpRequest<?> 객체
         * @param mapper Response Handler Function = response -> responseBody
         * @param <R>
         * @return
         */
        public static <R> AsyncExchange<R> exchangeAsObject(final HttpRequest<?> request, final Function<RawResponse, R> mapper) {
            return new AsyncExchange<>(request, request.asObjectAsync(mapper));
        }

        /**
         * (Non-Blocking) HTTP 요청 실행, Response Body 파일인 경우.
         *
         * @param request Unirest HttpRequest<?> 객체
         * @param outputFilePath 응답 파일 저장 경로
         * @param copyOptions 파일 저장 옵션 {@link java.nio.file.StandardCopyOption}
         * @return
         */
        public static AsyncExchange<File> exchangeAsFile(final HttpRequest<?> request, final String outputFilePath, final CopyOption... copyOptions) {
            return new AsyncExchange<>(request, request.asFileAsync(outputFilePath, copyOptions));
        }

        /**
         * (Non-Blocking) HTTP 요청 실행, Response Body 파일인 경우.
         *
         * @param request Unirest HttpRequest<?> 객체
         * @param outputFilePath 응답 파일 저장 경로
         * @param callback Response Handler Function = response -> responseBody(File)
         * @param copyOptions 파일 저장 옵션 {@link java.nio.file.StandardCopyOption}
         * @return
         */
        public static AsyncExchange<File> exchangeAsFile(final HttpRequest<?> request, final String outputFilePath, final Callback<File> callback, final CopyOption... copyOptions) {
            return new AsyncExchange<>(request, request.asFileAsync(outputFilePath, callback, copyOptions));
        }

        /**
         * (Non-Blocking) HTTP 요청 실행, Response Body java.util.Map 객체로 받음.
         *
         * @param request Unirest HttpRequest<?> 객체
         * @return
         */
        public static AsyncExchange<Map<String, Object>> exchangeAsMap(final HttpRequest<?> request, final Predicate<? super RawResponse> responseValidator) {
            return exchangeAsObject(request, resp -> HttpClientUtil.responseBody2Map(resp, responseValidator));
        }

        /**
         * (Non-Blocking) HTTP 요청 실행, Response Body java.util.Map 객체로 받음.
         *
         * @param request Unirest HttpRequest<?> 객체
         * @return
         */
        public static AsyncExchange<Map<String, Object>> exchangeAsMap(final HttpRequest<?> request) {
            return exchangeAsMap(request, HttpClientUtil::isResponseStatus2xx);
        }

        /**
         * (Blocking) Response Body 리턴.
         * Blocking method 사용을 원한다면, 이 메서드 보다는 Exchange 클래스 사용
         *
         * @see Exchange#getResponse()
         *
         * @return
         * @throws ExecutionException
         * @throws InterruptedException
         */
        @Deprecated
        public HttpResponse<R> getResponse() throws ExecutionException, InterruptedException {
            return response.get();
        }

        /**
         * ResponseBody Future 객체 리턴
         *
         * @return
         */
        public CompletableFuture<R> body() {
            return body(HttpClientUtil::isResponseStatus2xx);
        }

        /**
         * Response Body Future 객체 리턴
         *
         * @param responseValidator HTTP Response 성공 여부 확인 함수, Response 실패하면 ETRuntimeException 발생
         * @return
         */
        public CompletableFuture<R> body(final Predicate<? super HttpResponse<R>> responseValidator) {
            return response.thenApply(resp -> {
                if (responseValidator.test(resp)) {
                    return resp.getBody();
                }
                throw new ETRuntimeException(String.format("Response Status: %d(%s), Headers: %s, Detail: %s", resp.getStatus(), resp.getStatusText(), resp.getHeaders(), resp.getParsingError().map(Throwable::getMessage).orElseGet(() -> String.valueOf(resp.getBody()))));
            });
        }

        /**
         * RxJava Flowable 연계
         *
         * @return
         */
        public Flowable<HttpResponse<R>> responseFlowable() {
            return Flowable.fromFuture(response);
        }

        /**
         * RxJava Single 연계
         *
         * @return
         */
        public Single<HttpResponse<R>> responseSingle() {
            return Single.fromFuture(response);
        }

        /**
         * RxJava Flowable 연계
         *
         * @param responseValidator
         * @return
         */
        public Flowable<R> responseBodyFlowable(final Predicate<? super HttpResponse<R>> responseValidator) {
            return Flowable.fromFuture(body(responseValidator));
        }

        /**
         * RxJava Flowable 연계
         *
         * @return
         */
        public Flowable<R> responseBodyFlowable() {
            return Flowable.fromFuture(body());
        }

        /**
         * RxJava Single 연계
         *
         * @param responseValidator
         * @return
         */
        public Single<R> responseBodySingle(final Predicate<? super HttpResponse<R>> responseValidator) {
            return Single.fromFuture(body(responseValidator));
        }

        /**
         * RxJava Single 연계
         *
         * @return
         */
        public Single<R> responseBodySingle() {
            return Single.fromFuture(body());
        }
    }
    ////////////////////////////////////////
    ///               End                ///
    ///  Declarations of static classes  ///
    ////////////////////////////////////////

    /**
     * Response 성공 여부 확인 및 ResponseBody java.util.Map 으로 변환하는 함수
     *
     * @param resp
     * @param responseValidator
     * @return
     */
    public static Map<String, Object> responseBody2Map(final RawResponse resp, final Predicate<? super RawResponse> responseValidator) {
        if (!responseValidator.test(resp)) {
            String body = "";
            try { body = resp.getContentAsString(); }
            catch (final Throwable ignore) { }
            throw new ETRuntimeException(String.format("Response Status: %d(%s), Headers: %s, Detail: %s", resp.getStatus(), resp.getStatusText(), resp.getHeaders(), body));
        }
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(resp.getContentAsString(), new TypeReference<Map<String, Object>>() {});
        } catch (final JsonProcessingException e) {
            throw new ETRuntimeException(e);
        }
    }

    /**
     * Response 성공 여부 확인 함수, Response Http status code 2xx 인 경우 성공.
     * @param response
     * @return
     */
    public static boolean isResponseStatus2xx(final RawResponse response) {
        return response.getStatus() / 100 == 2;
    }

    /**
     * Response 성공 여부 확인 함수, Response Http status code 2xx 인 경우 성공.
     * @param response
     * @return
     */
    public static boolean isResponseStatus2xx(final HttpResponse<?> response) {
        return response.getStatus() / 100 == 2;
    }
}
