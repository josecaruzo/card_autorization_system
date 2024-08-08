package br.com.fiap.mscustomer.entity.validation.zipcode;

import br.com.fiap.mscustomer.entity.request.CustomerRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CEPValidation implements ConstraintValidator<ValidCEP, CustomerRequest> {
	@Override
	public boolean isValid(CustomerRequest customer, ConstraintValidatorContext context) {
		if(customer.getCep() == null){
			return false;
		}

		Pattern regex = Pattern.compile("^\\d{5}-\\d{3}$"); // 00000-000
		Matcher matcher = regex.matcher(customer.getCep());
		return matcher.matches();
	}
}
