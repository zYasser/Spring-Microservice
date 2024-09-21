package com.microservice.inventory_service.exceptions;


import com.microservice.inventory_service.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;


@ControllerAdvice

@ResponseStatus

public class RestResponseEntityExceptionHandler
{


	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<BaseResponse<Object>> ResourceNotFoundException(ResourceNotFoundException exception,
	                                                                      WebRequest request) {
		BaseResponse<Object> errorMessage =
				new BaseResponse<Object>(null, null, exception.getMessage(), HttpStatus.NOT_FOUND.value());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
	}
}
