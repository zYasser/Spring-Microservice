package com.microservice.inventory_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class BaseResponse<T>
{

	public T data;


	public String message;
	public String error;
	public int statusCode;
}
