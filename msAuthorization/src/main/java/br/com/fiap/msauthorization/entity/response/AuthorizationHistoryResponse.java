package br.com.fiap.msauthorization.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationHistoryResponse {
	private String numero_cartao;
	private float valor;
	private String descricao;
	private String metodo_pagamento;
	private String status;
}
