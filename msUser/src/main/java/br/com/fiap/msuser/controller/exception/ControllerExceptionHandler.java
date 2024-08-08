package br.com.fiap.msuser.controller.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

	private final StandardError newError = new StandardError();

	@ExceptionHandler(Exception.class)
	public ResponseEntity<StandardError> exception(Exception e, HttpServletRequest request){
		HttpStatus status = HttpStatus.UNAUTHORIZED;

		newError.setTimestamp(Instant.now());
		newError.setStatus(status.value());
		newError.setError("Erro ao autenticar o usuário."); // Data Integrity Violation
		newError.setMessage(e.getMessage());
		newError.setPath(request.getRequestURI());

		return ResponseEntity.status(status).body(newError);
	}
}
