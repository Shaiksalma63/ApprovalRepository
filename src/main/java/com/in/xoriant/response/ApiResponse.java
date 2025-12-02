package com.in.xoriant.response;

import lombok.Data;

@Data
public class ApiResponse<T> {
	private Integer code;
	private String message;
	private T data;
}