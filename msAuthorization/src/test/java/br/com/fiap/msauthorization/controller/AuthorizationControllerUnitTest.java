package br.com.fiap.msauthorization.controller;

import br.com.fiap.msauthorization.controller.exception.ControllerExceptionHandler;
import br.com.fiap.msauthorization.entity.Authorization;
import br.com.fiap.msauthorization.entity.Card;
import br.com.fiap.msauthorization.entity.request.AuthorizationRequest;
import br.com.fiap.msauthorization.entity.response.AuthorizationHistoryResponse;
import br.com.fiap.msauthorization.entity.response.AuthorizationResponse;
import br.com.fiap.msauthorization.service.impl.AuthorizationServiceImpl;
import br.com.fiap.msauthorization.utils.AuthorizationUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static br.com.fiap.msauthorization.constants.AuthorizationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthorizationControllerUnitTest {
	private MockMvc mockMvc;

	@Mock
	private AuthorizationServiceImpl authorizationService;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		AuthorizationController customerController = new AuthorizationController(authorizationService);
		mockMvc = MockMvcBuilders.standaloneSetup(customerController)
				.setControllerAdvice(new ControllerExceptionHandler())
				.build();
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class GetAuthorizations {
		@Test
		void allowGetAuthorizationsByCustomer() throws Exception{
			//Arrange
			String cpf = "00000000000";
			ArrayList<Authorization> authorization = AuthorizationUtils.getAuthorizationList();
			List<AuthorizationHistoryResponse> authorizationResponse = authorization.stream().map(AuthorizationUtils::entityToHistoryResponse).toList();
			when(authorizationService.getCustomerAuthorizations(any(String.class))).thenReturn(authorizationResponse);

			//Act
			mockMvc.perform(get("/api/pagamentos/cliente/{cpf}",cpf))
					.andExpect(status().isOk())
					.andExpect(result -> {
						String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
						assertThat(json).contains(String.valueOf(authorizationResponse.get(0).getValor()));
						assertThat(json).contains(String.valueOf(authorizationResponse.get(0).getDescricao()));
						assertThat(json).contains(String.valueOf(authorizationResponse.get(0).getMetodo_pagamento()));
						assertThat(json).contains(String.valueOf(authorizationResponse.get(0).getStatus()));
						assertThat(json).contains(String.valueOf(authorizationResponse.get(0).getNumero_cartao()));
					});

			//Assert
			verify(authorizationService, times(1)).getCustomerAuthorizations(any(String.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenGetAuthorizations_AuthorizationsNotFound() throws Exception{
			//Arrange
			String cpf = "99999999999";
			when(authorizationService.getCustomerAuthorizations(any(String.class))).thenThrow(
					new EntityNotFoundException(AUTHORIZATIONS_NOT_FOUND)
			);

			//Act && Assert
			mockMvc.perform(get("/api/pagamentos/cliente/{cpf}",cpf))
					.andExpect(status().isInternalServerError());
			verify(authorizationService, times(1)).getCustomerAuthorizations(any(String.class));
		}
	}

	@Nested
	class AddAuthorizations {
		@Test
		void allowAddAuthorization() throws Exception {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			AuthorizationResponse authorizationResponse = AuthorizationUtils.entityToResponse(authorization);
			when(authorizationService.add(any(AuthorizationRequest.class))).thenReturn(authorizationResponse);

			//Act
			mockMvc.perform(post("/api/pagamentos")
							.contentType(MediaType.APPLICATION_JSON)
							.content(AuthorizationUtils.asJsonString(authorizationRequest)))
					.andExpect(status().isOk())
					.andExpect(result -> {
						String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
						assertThat(json).contains(String.valueOf(authorizationResponse.getChave_pagamento()));
					});

			//Assert
			verify(authorizationService, times(1)).add(any(AuthorizationRequest.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenAddAuthorization_CardNotFound() throws Exception{
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			when(authorizationService.add(any(AuthorizationRequest.class))).thenThrow(
					new EntityNotFoundException(CARD_NOT_FOUND));

			//Act
			mockMvc.perform(post("/api/pagamentos")
							.contentType(MediaType.APPLICATION_JSON)
							.content(AuthorizationUtils.asJsonString(authorizationRequest)))
					.andExpect(status().isInternalServerError())
					.andExpect(result -> {
						String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
						assertThat(json).contains(CARD_NOT_FOUND);
					});

			//Assert
			verify(authorizationService, times(1)).add(any(AuthorizationRequest.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddAuthorization_ValidationFailed_cpf() throws Exception {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setCpf("99999999999");

			when(authorizationService.add(any(AuthorizationRequest.class))).thenThrow(
					new DataIntegrityViolationException(VALIDATION_FAILED));

			//Act
			mockMvc.perform(post("/api/pagamentos")
							.contentType(MediaType.APPLICATION_JSON)
							.content(AuthorizationUtils.asJsonString(authorizationRequest)))
					.andExpect(status().isInternalServerError())
					.andExpect(result -> {
						String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
						assertThat(json).contains(VALIDATION_FAILED);
					});

			//Assert
			verify(authorizationService, times(1)).add(any(AuthorizationRequest.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddAuthorization_ValidationFailed_data_validade() throws Exception{
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setData_validade("01/30");

			when(authorizationService.add(any(AuthorizationRequest.class))).thenThrow(
					new DataIntegrityViolationException(VALIDATION_FAILED));

			//Act
			mockMvc.perform(post("/api/pagamentos")
							.contentType(MediaType.APPLICATION_JSON)
							.content(AuthorizationUtils.asJsonString(authorizationRequest)))
					.andExpect(status().isInternalServerError())
					.andExpect(result -> {
						String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
						assertThat(json).contains(VALIDATION_FAILED);
					});

			//Assert
			verify(authorizationService, times(1)).add(any(AuthorizationRequest.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddAuthorization_ValidationFailed_cvv() throws Exception{
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setCvv("999");

			when(authorizationService.add(any(AuthorizationRequest.class))).thenThrow(
					new DataIntegrityViolationException(VALIDATION_FAILED));

			//Act
			mockMvc.perform(post("/api/pagamentos")
							.contentType(MediaType.APPLICATION_JSON)
							.content(AuthorizationUtils.asJsonString(authorizationRequest)))
					.andExpect(status().isInternalServerError())
					.andExpect(result -> {
						String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
						assertThat(json).contains(VALIDATION_FAILED);
					});

			//Assert
			verify(authorizationService, times(1)).add(any(AuthorizationRequest.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddAuthorization_InsufficientLimit() throws Exception {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setValor(99999999);

			when(authorizationService.add(any(AuthorizationRequest.class))).thenThrow(
					new DataIntegrityViolationException(INSUFFICIENT_LIMIT));

			//Act
			mockMvc.perform(post("/api/pagamentos")
							.contentType(MediaType.APPLICATION_JSON)
							.content(AuthorizationUtils.asJsonString(authorizationRequest)))
					.andExpect(status().isPaymentRequired())
					.andExpect(result -> {
						String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
						assertThat(json).contains(INSUFFICIENT_LIMIT);
					});

			//Assert
			verify(authorizationService, times(1)).add(any(AuthorizationRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddAuthorization_cpf_blank() throws Exception {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setCpf(null);

			//Act && Assert
			mockMvc.perform(post("/api/pagamentos")
					.contentType(MediaType.APPLICATION_JSON)
					.content(AuthorizationUtils.asJsonString(authorizationRequest))
			).andExpect(status().isBadRequest());
			verify(authorizationService, never()).add(any(AuthorizationRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddAuthorization_numero_blank() throws Exception {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setNumero(null);

			//Act && Assert
			mockMvc.perform(post("/api/pagamentos")
					.contentType(MediaType.APPLICATION_JSON)
					.content(AuthorizationUtils.asJsonString(authorizationRequest))
			).andExpect(status().isBadRequest());
			verify(authorizationService, never()).add(any(AuthorizationRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddAuthorization_data_validade_blank() throws Exception {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setData_validade(null);

			//Act && Assert
			mockMvc.perform(post("/api/pagamentos")
					.contentType(MediaType.APPLICATION_JSON)
					.content(AuthorizationUtils.asJsonString(authorizationRequest))
			).andExpect(status().isBadRequest());
			verify(authorizationService, never()).add(any(AuthorizationRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddAuthorization_cvv_blank() throws Exception {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setCvv(null);

			//Act && Assert
			mockMvc.perform(post("/api/pagamentos")
					.contentType(MediaType.APPLICATION_JSON)
					.content(AuthorizationUtils.asJsonString(authorizationRequest))
			).andExpect(status().isBadRequest());
			verify(authorizationService, never()).add(any(AuthorizationRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddAuthorization_valor_NegativeOrZero() throws Exception {
			//Arrange
			Card card = AuthorizationUtils.createFakeCard();
			Authorization authorization = AuthorizationUtils.createFakeAuthorization(1L, card);
			AuthorizationRequest authorizationRequest = AuthorizationUtils.entityToRequest(authorization);
			authorizationRequest.setValor(-1);

			//Act && Assert
			mockMvc.perform(post("/api/pagamentos")
					.contentType(MediaType.APPLICATION_JSON)
					.content(AuthorizationUtils.asJsonString(authorizationRequest))
			).andExpect(status().isBadRequest());
			verify(authorizationService, never()).add(any(AuthorizationRequest.class));
		}
	}
}