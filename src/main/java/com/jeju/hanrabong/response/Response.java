package com.jeju.hanrabong.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response<T> {
    private final T data;

    public Response(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public static <T> ResponseEntity<Response<T>> createDataResponse(T data) {
        return ResponseEntity.ok(new Response<>(data));
    }

//    public static ResponseEntity<?> createResponse(HttpStatus status, String message, Object data) {
//        return ResponseEntity.status(status).body(new Response<>(status.value(), message, data));
//    }
}
