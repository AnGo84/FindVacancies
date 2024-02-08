package ua.findvacancieswebflux.controller.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ValidationHandler {

	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<?> handleValidationException(WebExchangeBindException ex) {
		log.error("Get Validation error: {}", ex.getMessage());

		final BindingResult bindingResult = ex.getBindingResult();
		final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		final Map<String, String> errors = new HashMap<>();
		fieldErrors.forEach(error -> {
			log.info("Error: {}", error);
			errors.put(error.getField(), error.getDefaultMessage());
		});

		Map<String, Map<String, String>> result = new HashMap<>();
		result.put("errors", errors);

		return ResponseEntity.badRequest().body(result);

	}
}
