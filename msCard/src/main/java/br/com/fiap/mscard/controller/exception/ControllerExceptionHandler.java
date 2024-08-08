package br.com.fiap.mscard.controller.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static br.com.fiap.mscard.constants.CardConstants.*;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

	private final StandardError newError = new StandardError();

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<StandardError> dataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

		if(e.getMessage().equals(String.format(CUSTOMER_HAS_TOO_MANY_CARDS,NUM_MAX_CARDS))){
			status = HttpStatus.FORBIDDEN;
		}

		newError.setTimestamp(Instant.now());
		newError.setStatus(status.value());
		newError.setError("Violação da integridade dos dados."); // Data Integrity Violation
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
