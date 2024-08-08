package br.com.fiap.msauthorization.utils;

import br.com.fiap.msauthorization.entity.Authorization;
import br.com.fiap.msauthorization.entity.Card;
import br.com.fiap.msauthorization.entity.enums.StatusCode;
import br.com.fiap.msauthorization.entity.enums.StatusDesc;
import br.com.fiap.msauthorization.entity.enums.TrxnMethodType;
import br.com.fiap.msauthorization.entity.request.AuthorizationRequest;
import br.com.fiap.msauthorization.entity.response.AuthorizationHistoryResponse;
import br.com.fiap.msauthorization.entity.response.AuthorizationResponse;
import br.com.fiap.msauthorization.security.entity.Role;
import br.com.fiap.msauthorization.security.entity.User;
import br.com.fiap.msauthorization.security.entity.enums.RoleType;
import br.com.fiap.msauthorization.service.mapper.AuthorizationMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.fiap.msauthorization.constants.AuthorizationConstants.SUCCESSFUL_TRANSACTION_DESCRIPTION;

public class AuthorizationUtils {

	public static Card createFakeCard(){
		return Card.builder()
				.customerCpf("00000000000")
				.cardNumber("0000000000000000")
				.creditLimit(1000f)
				.creditOtb(1000f)
				.expiryDate("12/28")
				.cvv("000")
				.build();
	}

	public static Authorization createFakeAuthorization(Long id){
		Card card = createFakeCard();

		return Authorization.builder()
				.id(id)
				.customerCpf(card.getCustomerCpf())
				.cardNumber(card.getCardNumber())
				.expiryDate(card.getExpiryDate())
				.cvv(card.getCvv())
				.amount(100)
				.description(SUCCESSFUL_TRANSACTION_DESCRIPTION)
				.trxnMethod(TrxnMethodType.CREDIT)
				.trxnDatetime(LocalDateTime.now())
				.statusDescription(StatusDesc.APPROVED)
				.statusCode(StatusCode.APPR)
				.build();
	}

	public static Authorization createFakeAuthorization(Long id, Card card){
		return Authorization.builder()
				.id(id)
				.customerCpf(card.getCustomerCpf())
				.cardNumber(card.getCardNumber())
				.expiryDate(card.getExpiryDate())
				.cvv(card.getCvv())
				.amount(100)
				.description(SUCCESSFUL_TRANSACTION_DESCRIPTION)
				.trxnMethod(TrxnMethodType.CREDIT)
				.trxnDatetime(LocalDateTime.now())
				.statusDescription(StatusDesc.APPROVED)
				.statusCode(StatusCode.APPR)
				.build();
	}


	public static AuthorizationRequest entityToRequest(Authorization authorization){
		return AuthorizationRequest.builder()
				.cpf(authorization.getCustomerCpf())
				.numero(authorization.getCardNumber())
				.data_validade(authorization.getExpiryDate())
				.cvv(authorization.getCvv())
				.valor(authorization.getAmount())
				.build();
	}

	public static AuthorizationResponse entityToResponse(Authorization authorization) {
		return AuthorizationResponse.builder()
				.chave_pagamento(authorization.getId())
				.build();
	}

	public static AuthorizationHistoryResponse entityToHistoryResponse(Authorization authorization) {
		return AuthorizationHistoryResponse.builder()
				.valor(authorization.getAmount())
				.descricao(authorization.getDescription())
				.metodo_pagamento(authorization.getTrxnMethod().getValue())
				.status(authorization.getStatusDescription().getValue())
				.numero_cartao(AuthorizationMapper.maskedCard(authorization.getCardNumber()))
				.build();
	}

	public static ArrayList<Authorization> getAuthorizationList() {
		ArrayList<Authorization> authList = new ArrayList<>();
		authList.add(createFakeAuthorization(1L));
		authList.add(createFakeAuthorization(2L));

		return authList;
	}

	public static User getUser(){
		List<Role> defaultRole = List.of(new Role(2L, RoleType.ROLE_COMMON));
		User user = User.builder()
				.id(1L)
				.username("adj2")
				.password("adj@1234")
				.roles(defaultRole)
				.build();

		String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
		user.setPassword(encryptedPassword);

		return user;
	}

	public static String asJsonString(final Object object) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(object);
	}
}
