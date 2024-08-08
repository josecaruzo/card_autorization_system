package br.com.fiap.msauthorization.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCode {
	APPR("APPR"),
	OTB_REJC("OTB_REJC"), // Credit limit rejection
	CVV_REJC("CVV_REJC"), // CVV rejection
	EXP_REJC("EXP_REJC"), // Expiry date rejection
	CPF_REJCT("CPF_REJC"); // Wrong customer rejection !!POTENTIAL FRAUD!!

	private final String value;

}
