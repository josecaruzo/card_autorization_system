package br.com.fiap.msauthorization.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusDesc {
	APPROVED("APROVADO"),
	REJECTED("REJEITADO");

	private final String value;

}
