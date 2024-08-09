package br.com.fiap.msauthorization.entity.validation.cpf;

import br.com.fiap.msauthorization.entity.request.AuthorizationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CPFValidation implements ConstraintValidator<ValidCPF, AuthorizationRequest> {
	@Override
	public boolean isValid(AuthorizationRequest auth, ConstraintValidatorContext context) {
		if (auth.getCpf() == null){
			return false;
		}

		Pattern regex = Pattern.compile("^\\d{11}$"); // 00000000000
		Matcher matcher = regex.matcher(auth.getCpf());
		return matcher.matches();
	}
}
