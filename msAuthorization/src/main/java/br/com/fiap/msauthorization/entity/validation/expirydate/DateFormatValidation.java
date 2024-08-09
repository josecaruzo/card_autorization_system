package br.com.fiap.msauthorization.entity.validation.expirydate;

import br.com.fiap.msauthorization.entity.request.AuthorizationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFormatValidation implements ConstraintValidator<ValidDateFormat, AuthorizationRequest> {
	@Override
	public boolean isValid(AuthorizationRequest auth, ConstraintValidatorContext context) {
		if(auth.getData_validade() == null){
			return false;
		}

		String[] dates = auth.getData_validade().split("/");

		try {
			LocalDate date = LocalDate.of(Integer.parseInt("20" + dates[1]), Integer.parseInt(dates[0]), 1);
		}
		catch (Exception e){
			return false;
		}

		Pattern regex = Pattern.compile("^\\d{2}/\\d{2}$"); // 0000000000000000
		Matcher matcher = regex.matcher(auth.getData_validade());
		return matcher.matches();
	}
}
