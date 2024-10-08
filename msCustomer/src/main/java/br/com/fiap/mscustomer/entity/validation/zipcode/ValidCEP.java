package br.com.fiap.mscustomer.entity.validation.zipcode;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CEPValidation.class)
public @interface ValidCEP {
	//Error trying to validate the customer, the zipCode (CEP) needs to follow the pattern 00000-000
	String message() default "Erro na validação do cliente, o zipCode (CEP) precisa seguir o padrão 00000-000";

	Class<?>[] groups() default {};
	Class<? extends Payload> [] payload() default {};
}

