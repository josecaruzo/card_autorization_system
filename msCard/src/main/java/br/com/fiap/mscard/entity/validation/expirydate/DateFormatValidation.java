package br.com.fiap.mscard.entity.validation.expirydate;

import br.com.fiap.mscard.entity.request.CardRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFormatValidation implements ConstraintValidator<ValidDateFormat, CardRequest> {
	@Override
	public boolean isValid(CardRequest card, ConstraintValidatorContext context) {
		if(card.getData_validade() == null){
			return false;
		}

		Pattern regex = Pattern.compile("^\\d{2}/\\d{2}$"); // 0000000000000000
		Matcher matcher = regex.matcher(card.getData_validade());
		return matcher.matches();
	}
}
