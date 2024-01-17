package ru.practicum.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(2)
public class UnknonwnExceptionHandler {
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
	public ErrorResponse handleUnknownException(Exception e) {
		return new ErrorResponse(e.getMessage());
    }
}
