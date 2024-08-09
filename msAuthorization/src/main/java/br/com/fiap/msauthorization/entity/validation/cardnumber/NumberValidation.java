package br.com.fiap.msauthorization.entity.validation.cardnumber;

import br.com.fiap.msauthorization.entity.request.AuthorizationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberValidation implements ConstraintValidator<ValidNumber, AuthorizationRequest> {
	@Override
	public boolean isValid(AuthorizationRequest auth, ConstraintValidatorContext context) {
		if(auth.getNumero() == null){
			return false;
		}

		Pattern regex = Pattern.compile("^\\d{16}$"); // 0000000000000000
		Matcher matcher = regex.matcher(auth.getNumero());
		return matcher.matches();
	}
}
