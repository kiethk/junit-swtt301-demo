/**
 * ApiResponse — REFERENCE IMPLEMENTATION (dùng chung toàn bộ nhóm)
 *
 * Wrapper chuẩn cho mọi HTTP response của backend. Tất cả controller đều phải
 * trả về ApiResponse<T>, không trả thẳng entity hay kiểu dữ liệu thô.
 *
 * Cách dùng:
 *   return ApiResponse.success(data);       // { success: true, data: ..., message: "" }
 *   return ApiResponse.error("Not found");  // { success: false, data: null, message: "..." }
 *
 * Frontend đọc response theo interface ApiResponse<T> trong src/types/horse.ts (làm mẫu).
 */
package com.rtms.backend.dto;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "");
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
