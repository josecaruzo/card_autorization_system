package br.com.fiap.msauthorization.entity.validation.cvv;

import br.com.fiap.msauthorization.entity.request.AuthorizationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CvvValidation implements ConstraintValidator<ValidCvv, AuthorizationRequest> {
	@Override
	public boolean isValid(AuthorizationRequest auth, ConstraintValidatorContext context) {
		if(auth.getCvv() == null){
			return false;
		}

		Pattern regex = Pattern.compile("^\\d{3}$"); // 0000000000000000
		Matcher matcher = regex.matcher(auth.getCvv());
		return matcher.matches();
	}
}
