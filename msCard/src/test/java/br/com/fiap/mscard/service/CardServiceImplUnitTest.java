package br.com.fiap.mscard.service;

import br.com.fiap.mscard.entity.Card;
import br.com.fiap.mscard.entity.Customer;
import br.com.fiap.mscard.entity.request.CardRequest;
import br.com.fiap.mscard.entity.response.CardResponse;
import br.com.fiap.mscard.repository.CardRepository;
import br.com.fiap.mscard.repository.CustomerRepository;
import br.com.fiap.mscard.service.impl.CardServiceImpl;
import br.com.fiap.mscard.service.mapper.CardMapper;
import br.com.fiap.mscard.utils.CardUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static br.com.fiap.mscard.constants.CardConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CardServiceImplUnitTest {

	private CardServiceImpl cardService;

	@Mock
	private CardRepository cardRepository;

	@Mock
	private CustomerRepository customerRepository;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		cardService = new CardServiceImpl(cardRepository, customerRepository);
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class AddCard {
		@Test
		void allowAddCard() {
			//Arrange
			Customer customer = CardUtils.createFakeCustomer();
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.of(customer));
			when(cardRepository.countByCustomerCpf(any(String.class))).thenReturn(0);
			when(cardRepository.findByCardNumber(any(String.class))).thenReturn(Optional.empty());
			when(cardRepository.save(any(Card.class))).thenReturn(card);

			//Act
			CardResponse savedCard = cardService.add(cardRequest);

			//Assert
			assertThat(savedCard.getCpf()).isNotNull().isEqualTo(cardRequest.getCpf());
			assertThat(savedCard.getNumero()).isNotNull().isEqualTo(CardMapper.maskedCard(cardRequest.getNumero()));
			assertThat(savedCard.getLimite()).isPositive().isEqualTo(cardRequest.getLimite());
			verify(customerRepository, times(1)).findByCpf(any(String.class));
			verify(cardRepository, times(1)).countByCustomerCpf(any(String.class));
			verify(cardRepository, times(1)).findByCardNumber(any(String.class));
			verify(cardRepository, times(1)).save(any(Card.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenAddCard_CustomerNotFound() {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.empty());

			//Act && Assert
			assertThatThrownBy(() -> cardService.add(cardRequest))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(String.format(CUSTOMER_CPF_NOT_FOUND)); //Trying to create same customer twice
			verify(customerRepository, times(1)).findByCpf(any(String.class));
			verify(cardRepository, never()).countByCustomerCpf(any(String.class));
			verify(cardRepository, never()).findByCardNumber(any(String.class));
			verify(cardRepository, never()).save(any(Card.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddCard_TooManyCards() {
			//Arrange
			Customer customer = CardUtils.createFakeCustomer();
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.of(customer));
			when(cardRepository.countByCustomerCpf(any(String.class))).thenReturn(2);

			//Act && Assert
			assertThatThrownBy(() -> cardService.add(cardRequest))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(String.format(CUSTOMER_HAS_TOO_MANY_CARDS,NUM_MAX_CARDS)); //Trying to create same customer twice
			verify(customerRepository, times(1)).findByCpf(any(String.class));
			verify(cardRepository, times(1)).countByCustomerCpf(any(String.class));
			verify(cardRepository, never()).findByCardNumber(any(String.class));
			verify(cardRepository, never()).save(any(Card.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddCard_CardAlreadyExists() {
			//Arrange
			Customer customer = CardUtils.createFakeCustomer();
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);
			when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.of(customer));
			when(cardRepository.countByCustomerCpf(any(String.class))).thenReturn(0);
			when(cardRepository.findByCardNumber(any(String.class))).thenReturn(Optional.of(card));

			//Act && Assert
			assertThatThrownBy(() -> cardService.add(cardRequest))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(CARD_ALREADY_EXISTS); //Trying to create same customer twice
			verify(customerRepository, times(1)).findByCpf(any(String.class));
			verify(cardRepository, times(1)).countByCustomerCpf(any(String.class));
			verify(cardRepository, times(1)).findByCardNumber(any(String.class));
			verify(cardRepository, never()).save(any(Card.class));
		}
	}
}
