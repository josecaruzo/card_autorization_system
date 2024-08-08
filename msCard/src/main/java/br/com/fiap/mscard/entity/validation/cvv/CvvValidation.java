package br.com.fiap.mscard.entity.validation.cvv;

import br.com.fiap.mscard.entity.request.CardRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CvvValidation implements ConstraintValidator<ValidCvv, CardRequest> {
	@Override
	public boolean isValid(CardRequest card, ConstraintValidatorContext context) {
		if(card.getCvv() == null){
			return false;
		}

		Pattern regex = Pattern.compile("^\\d{3}$"); // 0000000000000000
		Matcher matcher = regex.matcher(card.getCvv());
		return matcher.matches();
	}
}
