package br.com.fiap.mscard.entity.validation.cardnumber;

import br.com.fiap.mscard.entity.request.CardRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberValidation implements ConstraintValidator<ValidNumber, CardRequest> {
	@Override
	public boolean isValid(CardRequest card, ConstraintValidatorContext context) {
		if(card.getNumero() == null){
			return false;
		}

		Pattern regex = Pattern.compile("^\\d{16}$"); // 0000000000000000
		Matcher matcher = regex.matcher(card.getNumero());
		return matcher.matches();
	}
}
