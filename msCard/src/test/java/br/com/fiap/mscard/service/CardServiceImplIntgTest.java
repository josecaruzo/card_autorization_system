package br.com.fiap.mscard.service;

import br.com.fiap.mscard.entity.Card;
import br.com.fiap.mscard.entity.Customer;
import br.com.fiap.mscard.entity.request.CardRequest;
import br.com.fiap.mscard.entity.response.CardResponse;
import br.com.fiap.mscard.repository.CustomerRepository;
import br.com.fiap.mscard.service.impl.CardServiceImpl;
import br.com.fiap.mscard.service.mapper.CardMapper;
import br.com.fiap.mscard.utils.CardUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;

import static br.com.fiap.mscard.constants.CardConstants.*;
import static br.com.fiap.mscard.constants.CardConstants.CARD_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Rollback
public class CardServiceImplIntgTest {

	@Autowired
	private CardServiceImpl cardService;

	@Autowired
	private CustomerRepository customerRepository;


	@Nested
	class AddCard {
		@Test
		void allowAddCard() {
			//Arrange
			Customer customer = CardUtils.createFakeCustomer();
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);

			//Act
			customerRepository.save(customer);
			CardResponse savedCard = cardService.add(cardRequest);

			//Assert
			assertThat(savedCard.getCpf()).isNotNull().isEqualTo(cardRequest.getCpf());
			assertThat(savedCard.getNumero()).isNotNull().isEqualTo(CardMapper.maskedCard(cardRequest.getNumero()));
			assertThat(savedCard.getLimite()).isPositive().isEqualTo(cardRequest.getLimite());
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddCard_CustomerNotFound() {
			//Arrange
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);

			//Act && Assert
			assertThatThrownBy(() -> cardService.add(cardRequest))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(String.format(CUSTOMER_CPF_NOT_FOUND)); //Trying to create same customer twice
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddCard_TooManyCards() {
			//Arrange
			Customer customer = CardUtils.createFakeCustomer();
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest1 = CardUtils.entityToRequest(card);
			CardRequest cardRequest2 = CardUtils.entityToRequest(card);
			CardRequest cardRequest3 = CardUtils.entityToRequest(card);

			cardRequest2.setNumero("0000000000000002");
			cardRequest3.setNumero("0000000000000003");

			//Act
			customerRepository.save(customer);
			cardService.add(cardRequest1);
			cardService.add(cardRequest2);

			//Assert
			assertThatThrownBy(() -> cardService.add(cardRequest3))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(String.format(CUSTOMER_HAS_TOO_MANY_CARDS,NUM_MAX_CARDS)); //Trying to create same customer twice
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddCard_CardAlreadyExists() {
			//Arrange
			Customer customer = CardUtils.createFakeCustomer();
			Card card = CardUtils.createFakeCard();
			CardRequest cardRequest = CardUtils.entityToRequest(card);

			//Act
			customerRepository.save(customer);
			cardService.add(cardRequest);

			//Assert
			assertThatThrownBy(() -> cardService.add(cardRequest))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(CARD_ALREADY_EXISTS); //Trying to create same customer twice
		}
	}

}
