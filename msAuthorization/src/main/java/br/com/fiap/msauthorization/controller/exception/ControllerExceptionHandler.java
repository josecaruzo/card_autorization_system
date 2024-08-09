package br.com.fiap.msauthorization.controller.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.Instant;

import static br.com.fiap.msauthorization.constants.AuthorizationConstants.*;

@ControllerAdvice
public class ControllerExceptionHandler {

	private final StandardError newError = new StandardError();

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<StandardError> dataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

		if(e.getMessage().equals(INSUFFICIENT_LIMIT)){
			status = HttpStatus.PAYMENT_REQUIRED;
		}

		newError.setTimestamp(Instant.now());
		newError.setStatus(status.value());
		newError.setError("Authorização recusada."); // Data Integrity Violation
		newError.setMessage(e.getMessage());
		newError.setPath(request.getRequestURI());

		return ResponseEntity.status(status).body(newError);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<StandardError> entityNotFound(EntityNotFoundException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

		newError.setTimestamp(Instant.now());
		newError.setStatus(status.value());
		newError.setError("Entidade não encontrada."); //Entity not found
		newError.setMessage(e.getMessage());
		newError.setPath(request.getRequestURI());

		return ResponseEntity.status(status).body(newError);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<StandardError> accessDenied(AccessDeniedException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.UNAUTHORIZED;

		newError.setTimestamp(Instant.now());
		newError.setStatus(status.value());
		newError.setError("A autenticação falhou."); //Authentication failed
		newError.setMessage(e.getMessage());
		newError.setPath(request.getRequestURI());

		return ResponseEntity.status(status).body(newError);
	}

	@ExceptionHandler(AuthenticationServiceException.class)
	public ResponseEntity<StandardError> authenticationException(AuthenticationServiceException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.UNAUTHORIZED;

		newError.setTimestamp(Instant.now());
		newError.setStatus(status.value());
		newError.setError("A autenticação falhou."); //Authentication failed
		newError.setMessage(e.getMessage());
		newError.setPath(request.getRequestURI());

		return ResponseEntity.status(status).body(newError);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StandardError> entityValidation(MethodArgumentNotValidException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.BAD_REQUEST;

		ValidateError newValidError = new ValidateError();

		newValidError.setTimestamp(Instant.now());
		newValidError.setStatus(status.value());
		newValidError.setError("A entidade não é válida."); //Entity not valid
		newValidError.setMessage(e.getMessage());
		newValidError.setPath(request.getRequestURI());

		for(FieldError f: e.getBindingResult().getFieldErrors()){
			newValidError.addMessage(f.getObjectName(), f.getField(),f.getDefaultMessage());
		}

		for(ObjectError g: e.getBindingResult().getGlobalErrors()){
			newValidError.addMessage(g.getObjectName(), "Global", g.getDefaultMessage());
		}

		return ResponseEntity.status(status).body(newValidError);
	}
}
