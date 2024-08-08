package br.com.fiap.msauthorization.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrxnMethodType {
	CREDIT("Cartão de crédito");

	private final String value;

}
