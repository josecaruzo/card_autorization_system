package br.com.fiap.mscard.entity.validation.expirydate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidation.class)
public @interface ValidDate {
	//Error trying to validate the card, the expiry date is invalid, card expiry date is before current date
	String message() default "Erro na validação do cartão, a data de expiração do cartão é menor que a data atual";

	Class<?>[] groups() default {};
	Class<? extends Payload> [] payload() default {};
}
