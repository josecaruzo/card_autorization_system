package br.com.fiap.msauthorization.service.mapper;

import br.com.fiap.msauthorization.entity.Authorization;
import br.com.fiap.msauthorization.entity.enums.TrxnMethodType;
import br.com.fiap.msauthorization.entity.request.AuthorizationRequest;
import br.com.fiap.msauthorization.entity.response.AuthorizationHistoryResponse;
import br.com.fiap.msauthorization.entity.response.AuthorizationResponse;
import lombok.experimental.UtilityClass;

import static br.com.fiap.msauthorization.constants.AuthorizationConstants.*;

@UtilityClass
public class AuthorizationMapper {

	public Authorization requestToEntity(AuthorizationRequest authorizationRequest){
		return Authorization.builder()
				.customerCpf(authorizationRequest.getCpf())
				.cardNumber(authorizationRequest.getNumero())
				.expiryDate(authorizationRequest.getData_validade())
				.cvv(authorizationRequest.getCvv())
				.amount(authorizationRequest.getValor())
				.trxnMethod(TrxnMethodType.CREDIT)
				.build();
	}

	public AuthorizationResponse entityToResponse(Authorization authorization){
		return AuthorizationResponse.builder()
				.chave_pagamento(authorization.getId())
				.build();
	}

	public AuthorizationHistoryResponse entityToHistoryResponse(Authorization authorization){
		return AuthorizationHistoryResponse.builder()
				.valor(authorization.getAmount())
				.descricao(authorization.getDescription())
				.metodo_pagamento(authorization.getTrxnMethod().getValue())
				.status(authorization.getStatusDescription().getValue())
				.numero_cartao(maskedCard(authorization.getCardNumber()))
				.build();
	}

	public String maskedCard(String cardNumber){
		int maxMaskedChars = cardNumber.length() - CARD_SUFFIX - BIN_LENGTH;

		return cardNumber.substring(0, BIN_LENGTH)
				+ "*".repeat(Math.max(0, maxMaskedChars)) //Masked card number part
				+ cardNumber.substring(BIN_LENGTH + maxMaskedChars);
	}
}
