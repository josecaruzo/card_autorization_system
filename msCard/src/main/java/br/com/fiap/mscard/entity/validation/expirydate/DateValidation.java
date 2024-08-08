package br.com.fiap.mscard.entity.validation.expirydate;

import br.com.fiap.mscard.entity.request.CardRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateValidation implements ConstraintValidator<ValidDate, CardRequest> {
	@Override
	public boolean isValid(CardRequest card, ConstraintValidatorContext context) {
		if(card.getData_validade() == null){
			return false;
		}

		String[] dates = card.getData_validade().split("/");

		try {
			LocalDate date = LocalDate.of(Integer.parseInt("20" + dates[1]), Integer.parseInt(dates[0]), 1);
			if (date.isBefore(LocalDate.now())){
				return false;
			}
		}
		catch (Exception e){
			return false;
		}

		return true;
	}
}
