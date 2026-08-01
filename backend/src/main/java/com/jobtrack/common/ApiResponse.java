package com.jobtrack.common;

public record ApiResponse<T>(String message, T data) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("success", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(message, data);
    }
}
