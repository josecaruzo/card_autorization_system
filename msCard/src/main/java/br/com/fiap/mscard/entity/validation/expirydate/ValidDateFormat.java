package br.com.fiap.mscard.entity.validation.expirydate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateFormatValidation.class)
public @interface ValidDateFormat {
	//Error trying to validate the card, the expiry date is invalid, format MM/YY
	String message() default "Erro na validação do cartão, a data de expiração do cartão é inválida formato MM/YY";

	Class<?>[] groups() default {};
	Class<? extends Payload> [] payload() default {};
}

