package br.com.fiap.mscustomer.entity.validation.cpf;

import br.com.fiap.mscustomer.entity.request.CustomerRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CPFValidation implements ConstraintValidator<ValidCPF, CustomerRequest> {
	@Override
	public boolean isValid(CustomerRequest customer, ConstraintValidatorContext context) {
		if (customer.getCpf() == null){
			return false;
		}

		Pattern regex = Pattern.compile("^\\d{11}$"); // 00000000000
		Matcher matcher = regex.matcher(customer.getCpf());
		return matcher.matches();
	}
}
