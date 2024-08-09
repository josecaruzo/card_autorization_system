package br.com.fiap.mscard.entity.validation.cpf;

import br.com.fiap.mscard.entity.request.CardRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CPFValidation implements ConstraintValidator<ValidCPF, CardRequest> {
	@Override
	public boolean isValid(CardRequest card, ConstraintValidatorContext context) {
		if (card.getCpf() == null){
			return false;
		}

		Pattern regex = Pattern.compile("^\\d{11}$"); // 00000000000
		Matcher matcher = regex.matcher(card.getCpf());
		return matcher.matches();
	}
}
