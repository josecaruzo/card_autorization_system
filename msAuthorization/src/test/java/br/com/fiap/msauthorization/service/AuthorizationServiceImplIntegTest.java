package br.com.fiap.msauthorization.service;

import br.com.fiap.msauthorization.entity.Authorization;
import br.com.fiap.msauthorization.entity.Card;
import br.com.fiap.msauthorization.entity.request.AuthorizationRequest;
import br.com.fiap.msauthorization.entity.response.AuthorizationHistoryResponse;
import br.com.fiap.msauthorization.entity.response.AuthorizationResponse;
import br.com.fiap.msauthorization.repository.AuthorizationRepository;
import br.com.fiap.msauthorization.repository.CardRepository;
import br.com.fiap.msauthorization.service.impl.AuthorizationServiceImpl;
import br.com.fiap.msauthorization.service.mapper.AuthorizationMapper;
import br.com.fiap.msauthorization.utils.AuthorizationUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static br.com.fiap.msauthorization.constants.AuthorizationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Rollback
public class AuthorizationServiceImplIntegTest {

	@Autowired
	private AuthorizationServiceImpl authorizationService;

	@Autowired
	private AuthorizationRepository authorizationRepository;

	@Autowired
	private CardRepository cardRepository;

	@Nested
	class GetAuthorizations {
		@Test
		void allowGetAuthorizationsByCustomer(){
			//Arrange
			String cpf = "00000000000";
			ArrayList<Authorization> authorizationList = AuthorizationUtils.getAuthorizationList();

			//Act
			authorizationRepository.save(authorizationList.get(0));
			authorizationRepository.save(authorizationList.get(1));
			List<AuthorizationHistoryResponse> authFound = authorizationService.getCustomerAuthorizations(cpf);

			//Assert
			assertThat(authFound).isNotNull();
			assertThat(authFound.get(0).getNumero_cartao()).isNotNull()
					.isEqualTo(AuthorizationMapper.maskedCard(authorizationList.get(0).getCardNumber()));
			assertThat(authFound.get(0).getValor()).isPositive().isEqualTo(authorizationList.get(0).getAmount());
			assertThat(authFound.get(0).getDescricao()).isEqualTo(authorizationList.get(0).getDescription());
			assertThat(authFound.get(0).getMetodo_pagamento()).isEqualTo(authorizationList.get(0).getTrxnMethod().getValue());
			assertThat(authFound.get(0).getStatus()).isEqualTo(authorizationList.get(0).getStatusDescription().getValue());

		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenGetAuthorizations_AuthorizationsNotFound(){
			//Arrange
			String cpf = "99999999999";

			//Act && Assert
			assertThatThrownBy(() -> authorizationService.getCustomerAuthorizations(cpf))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(String.format(AUTHORIZATIONS_NOT_FOUND));
		}
	}

	@Nested
	class AddAuthorizations {
		@Test
		void allowAddAuthorization() {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);


			//Act
			cardRepository.save(card);
			AuthorizationResponse savedAuthorization = authorizationService.add(authorizationRequest);

			//Assert
			assertThat(savedAuthorization.getChave_pagamento()).isNotNull();
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenAddAuthorization_CardNotFound() {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);

			//Act && Assert
			assertThatThrownBy(() -> authorizationService.add(authorizationRequest))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(String.format(CARD_NOT_FOUND)); //Trying to create same customer twice
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddAuthorization_ValidationFailed_cpf() {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setCpf("99999999999");

			//Act
			cardRepository.save(card);

			//Assert
			assertThatThrownBy(() -> authorizationService.add(authorizationRequest))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(String.format(VALIDATION_FAILED));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddAuthorization_ValidationFailed_data_validade() {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setData_validade("01/30");

			//Act
			cardRepository.save(card);

			//Assert
			assertThatThrownBy(() -> authorizationService.add(authorizationRequest))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(String.format(VALIDATION_FAILED));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddAuthorization_ValidationFailed_cvv() {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setCvv("999");

			//Act
			cardRepository.save(card);

			//Assert
			assertThatThrownBy(() -> authorizationService.add(authorizationRequest))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(String.format(VALIDATION_FAILED));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddAuthorization_InsufficientLimit() {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setValor(9999);

			//Act
			cardRepository.save(card);

			//Assert
			assertThatThrownBy(() -> authorizationService.add(authorizationRequest))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(String.format(INSUFFICIENT_LIMIT));
		}
	}
}
