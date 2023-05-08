package com.juno.pacsserver.dto;

import lombok.Setter;

@Setter
public class ResponseDto<T> {
    /** 응답 코드 */
    public final String code;

    /** 메시지 */
    public final String message;

    /** Data */
    public final T data;

    public ResponseDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public ResponseDto(String code, String message) {
        this(code, message, null);
    }
}
