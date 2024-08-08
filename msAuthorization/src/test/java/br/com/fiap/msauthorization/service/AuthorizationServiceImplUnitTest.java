package br.com.fiap.msauthorization.service;

import br.com.fiap.msauthorization.entity.Authorization;
import br.com.fiap.msauthorization.entity.Card;
import br.com.fiap.msauthorization.entity.enums.StatusDesc;
import br.com.fiap.msauthorization.entity.request.AuthorizationRequest;
import br.com.fiap.msauthorization.entity.response.AuthorizationHistoryResponse;
import br.com.fiap.msauthorization.entity.response.AuthorizationResponse;
import br.com.fiap.msauthorization.repository.AuthorizationRepository;
import br.com.fiap.msauthorization.repository.CardRepository;
import br.com.fiap.msauthorization.service.impl.AuthorizationServiceImpl;
import br.com.fiap.msauthorization.service.mapper.AuthorizationMapper;
import br.com.fiap.msauthorization.utils.AuthorizationUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.fiap.msauthorization.constants.AuthorizationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class AuthorizationServiceImplUnitTest {

	private AuthorizationServiceImpl authorizationService;

	@Mock
	private AuthorizationRepository authorizationRepository;

	@Mock
	private CardRepository cardRepository;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		authorizationService = new AuthorizationServiceImpl(authorizationRepository, cardRepository);
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class GetAuthorizations {
		@Test
		void allowGetAuthorizationsByCustomer(){
			//Arrange
			String cpf = "00000000000";
			ArrayList<Authorization> authorizationList = AuthorizationUtils.getAuthorizationList();
			when(authorizationRepository.findByCustomerCpfAndStatusDescription(any(String.class), any(StatusDesc.class)))
					.thenReturn(authorizationList);

			//Act
			List<AuthorizationHistoryResponse> authFound = authorizationService.getCustomerAuthorizations(cpf);

			//Assert
			assertThat(authFound).isNotNull();
			assertThat(authFound.get(0).getNumero_cartao()).isNotNull()
					.isEqualTo(AuthorizationMapper.maskedCard(authorizationList.get(0).getCardNumber()));
			assertThat(authFound.get(0).getValor()).isPositive().isEqualTo(authorizationList.get(0).getAmount());
			assertThat(authFound.get(0).getDescricao()).isEqualTo(authorizationList.get(0).getDescription());
			assertThat(authFound.get(0).getMetodo_pagamento()).isEqualTo(authorizationList.get(0).getTrxnMethod().getValue());
			assertThat(authFound.get(0).getStatus()).isEqualTo(authorizationList.get(0).getStatusDescription().getValue());
			verify(authorizationRepository, times(1))
					.findByCustomerCpfAndStatusDescription(any(String.class),any(StatusDesc.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenGetAuthorizations_AuthorizationsNotFound(){
			//Arrange
			String cpf = "99999999999";
			when(authorizationRepository.findByCustomerCpfAndStatusDescription(any(String.class), any(StatusDesc.class)))
					.thenReturn(new ArrayList<>());

			//Act && Assert
			assertThatThrownBy(() -> authorizationService.getCustomerAuthorizations(cpf))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(String.format(AUTHORIZATIONS_NOT_FOUND));
			verify(authorizationRepository, times(1))
					.findByCustomerCpfAndStatusDescription(any(String.class),any(StatusDesc.class));
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

			when(cardRepository.findByCardNumber(any(String.class))).thenReturn(Optional.of(card));
			when(cardRepository.save(any(Card.class))).thenReturn(card);
			when(authorizationRepository.save(any(Authorization.class))).thenReturn(authorization);


			//Act
			AuthorizationResponse savedAuthorization = authorizationService.add(authorizationRequest);

			//Assert
			assertThat(savedAuthorization.getChave_pagamento()).isNotNull().isEqualTo(authorization.getId());
			verify(cardRepository, times(1)).findByCardNumber(any(String.class));
			verify(cardRepository, times(1)).save(any(Card.class));
			verify(authorizationRepository, times(1)).save(any(Authorization.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenAddAuthorization_CardNotFound() {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);

			when(cardRepository.findByCardNumber(any(String.class))).thenReturn(Optional.empty());

			//Act && Assert
			assertThatThrownBy(() -> authorizationService.add(authorizationRequest))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(String.format(CARD_NOT_FOUND)); 
			verify(cardRepository, times(1)).findByCardNumber(any(String.class));
			verify(cardRepository, never()).save(any(Card.class));
			verify(authorizationRepository, never()).save(any(Authorization.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddAuthorization_ValidationFailed_cpf() {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setCpf("99999999999");

			when(cardRepository.findByCardNumber(any(String.class))).thenReturn(Optional.of(card));

			//Act && Assert
			assertThatThrownBy(() -> authorizationService.add(authorizationRequest))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(String.format(VALIDATION_FAILED)); 
			verify(cardRepository, times(1)).findByCardNumber(any(String.class));
			verify(cardRepository, never()).save(any(Card.class));
			verify(authorizationRepository, times(1)).save(any(Authorization.class)); //Saving history
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddAuthorization_ValidationFailed_data_validade() {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setData_validade("01/30");

			when(cardRepository.findByCardNumber(any(String.class))).thenReturn(Optional.of(card));

			//Act && Assert
			assertThatThrownBy(() -> authorizationService.add(authorizationRequest))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(String.format(VALIDATION_FAILED)); 
			verify(cardRepository, times(1)).findByCardNumber(any(String.class));
			verify(cardRepository, never()).save(any(Card.class));
			verify(authorizationRepository, times(1)).save(any(Authorization.class)); //Saving history
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddAuthorization_ValidationFailed_cvv() {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setCvv("999");

			when(cardRepository.findByCardNumber(any(String.class))).thenReturn(Optional.of(card));

			//Act && Assert
			assertThatThrownBy(() -> authorizationService.add(authorizationRequest))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(String.format(VALIDATION_FAILED)); 
			verify(cardRepository, times(1)).findByCardNumber(any(String.class));
			verify(cardRepository, never()).save(any(Card.class));
			verify(authorizationRepository, times(1)).save(any(Authorization.class)); //Saving history
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddAuthorization_InsufficientLimit() {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setValor(9999);

			when(cardRepository.findByCardNumber(any(String.class))).thenReturn(Optional.of(card));

			//Act && Assert
			assertThatThrownBy(() -> authorizationService.add(authorizationRequest))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(String.format(INSUFFICIENT_LIMIT)); 
			verify(cardRepository, times(1)).findByCardNumber(any(String.class));
			verify(cardRepository, never()).save(any(Card.class));
			verify(authorizationRepository, times(1)).save(any(Authorization.class)); //Saving history
		}
	}
}
