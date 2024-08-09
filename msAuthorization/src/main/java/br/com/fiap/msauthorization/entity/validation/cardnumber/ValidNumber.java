package br.com.fiap.msauthorization.entity.validation.cardnumber;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NumberValidation.class)
public @interface ValidNumber {
	//Error trying to validate the card, the card number needs to follow the pattern 0000000000000000
	String message() default "Erro na validação do autorização, o número precisa seguir o padrão de 16 números sem caracteres especiais 0000000000000000";

	Class<?>[] groups() default {};
	Class<? extends Payload> [] payload() default {};
}

