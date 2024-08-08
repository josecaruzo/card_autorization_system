package br.com.fiap.mscard.controller;

import br.com.fiap.mscard.controller.exception.ControllerExceptionHandler;
import br.com.fiap.mscard.entity.Card;
import br.com.fiap.mscard.entity.request.CardRequest;
import br.com.fiap.mscard.entity.response.CardResponse;
import br.com.fiap.mscard.service.impl.CardServiceImpl;
import br.com.fiap.mscard.utils.CardUtils;
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

import static br.com.fiap.mscard.constants.CardConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CardControllerUnitTest {
	private MockMvc mockMvc;

	@Mock
	private CardServiceImpl cardService;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		CardController customerController = new CardController(cardService);
		mockMvc = MockMvcBuilders.standaloneSetup(customerController)
				.setControllerAdvice(new ControllerExceptionHandler())
				.build();
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class AddCard {
		@Test
		void allowAddCard() throws Exception {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			CardResponse cardResponse = CardUtils.entityToResponse(card);
			when(cardService.add(any(CardRequest.class))).thenReturn(cardResponse);

			//Act
			mockMvc.perform(post("/api/cartao")
							.contentType(MediaType.APPLICATION_JSON)
							.content(CardUtils.asJsonString(cardRequest)))
					.andExpect(status().isOk());

			//Assert
			verify(cardService, times(1)).add(any(CardRequest.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddCard_CustomerNotFound() throws Exception{
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			when(cardService.add(any(CardRequest.class))).thenThrow(
					new EntityNotFoundException(CUSTOMER_CPF_NOT_FOUND)
			);

			//Act && Assert
			mockMvc.perform(post("/api/cartao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CardUtils.asJsonString(cardRequest))
			).andExpect(status().isInternalServerError());
			verify(cardService, times(1)).add(any(CardRequest.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddCard_TooManyCards() throws Exception {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			when(cardService.add(any(CardRequest.class))).thenThrow(
					new DataIntegrityViolationException(String.format(CUSTOMER_HAS_TOO_MANY_CARDS,NUM_MAX_CARDS))
			);

			//Act && Assert
			mockMvc.perform(post("/api/cartao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CardUtils.asJsonString(cardRequest))
			).andExpect(status().isForbidden());
			verify(cardService, times(1)).add(any(CardRequest.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddCard_CardAlreadyExists() throws Exception{
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			when(cardService.add(any(CardRequest.class))).thenThrow(
					new DataIntegrityViolationException(CARD_ALREADY_EXISTS)
			);

			//Act && Assert
			mockMvc.perform(post("/api/cartao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CardUtils.asJsonString(cardRequest))
			).andExpect(status().isInternalServerError());
			verify(cardService, times(1)).add(any(CardRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCard_cpf_blank() throws Exception {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			cardRequest.setCpf(null);

			//Act && Assert
			mockMvc.perform(post("/api/cartao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CardUtils.asJsonString(cardRequest))
			).andExpect(status().isBadRequest());
			verify(cardService, never()).add(any(CardRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCard_numero_blank() throws Exception {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			cardRequest.setNumero(null);

			//Act && Assert
			mockMvc.perform(post("/api/cartao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CardUtils.asJsonString(cardRequest))
			).andExpect(status().isBadRequest());
			verify(cardService, never()).add(any(CardRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCard_numero_invalid() throws Exception {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			cardRequest.setNumero("000000******0000"); //Invalid format

			//Act && Assert
			mockMvc.perform(post("/api/cartao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CardUtils.asJsonString(cardRequest))
			).andExpect(status().isBadRequest());
			verify(cardService, never()).add(any(CardRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCard_limite_notPositive() throws Exception {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			cardRequest.setLimite(-1);

			//Act && Assert
			mockMvc.perform(post("/api/cartao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CardUtils.asJsonString(cardRequest))
			).andExpect(status().isBadRequest());
			verify(cardService, never()).add(any(CardRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCard_data_validade_blank() throws Exception {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			cardRequest.setData_validade(null);

			//Act && Assert
			mockMvc.perform(post("/api/cartao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CardUtils.asJsonString(cardRequest))
			).andExpect(status().isBadRequest());
			verify(cardService, never()).add(any(CardRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCard_data_validade_invalidDate() throws Exception {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			cardRequest.setData_validade("13/25"); //Wrong date

			//Act && Assert
			mockMvc.perform(post("/api/cartao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CardUtils.asJsonString(cardRequest))
			).andExpect(status().isBadRequest());
			verify(cardService, never()).add(any(CardRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCard_data_validade_oldDate() throws Exception {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			cardRequest.setData_validade("12/20"); // Before current date

			//Act && Assert
			mockMvc.perform(post("/api/cartao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CardUtils.asJsonString(cardRequest))
			).andExpect(status().isBadRequest());
			verify(cardService, never()).add(any(CardRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCard_data_validade_invalidFormat() throws Exception {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			cardRequest.setData_validade("11/2025"); //Wrong format

			//Act && Assert
			mockMvc.perform(post("/api/cartao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CardUtils.asJsonString(cardRequest))
			).andExpect(status().isBadRequest());
			verify(cardService, never()).add(any(CardRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCard_cvv_blank() throws Exception {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			cardRequest.setCvv(null);

			//Act && Assert
			mockMvc.perform(post("/api/cartao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CardUtils.asJsonString(cardRequest))
			).andExpect(status().isBadRequest());
			verify(cardService, never()).add(any(CardRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCard_cvv_invalid() throws Exception {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			cardRequest.setCvv("AAA"); //Wrong format

			//Act && Assert
			mockMvc.perform(post("/api/cartao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CardUtils.asJsonString(cardRequest))
			).andExpect(status().isBadRequest());
			verify(cardService, never()).add(any(CardRequest.class));
		}
	}
}
