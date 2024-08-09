package br.com.fiap.msauthorization.entity.validation.cvv;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CvvValidation.class)
public @interface ValidCvv {
	//Error trying to validate the card, the cvv number is invalid, format 000
	String message() default "Erro na validação do cartão, o cvv possui um formato inválido, formato 000";

	Class<?>[] groups() default {};
	Class<? extends Payload> [] payload() default {};
}

