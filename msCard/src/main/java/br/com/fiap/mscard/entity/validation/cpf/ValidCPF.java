package br.com.fiap.mscard.entity.validation.cpf;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CPFValidation.class)
public @interface ValidCPF {
	//Error trying to validate the customer, the CPF needs to follow the pattern 00000000000
	String message() default "Erro na validação do cartão, o CPF precisa seguir o padrão de 11 números sem caracteres especiais 00000000000";

	Class<?>[] groups() default {};
	Class<? extends Payload> [] payload() default {};
}

