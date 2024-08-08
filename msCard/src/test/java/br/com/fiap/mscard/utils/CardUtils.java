package br.com.fiap.mscard.utils;

import br.com.fiap.mscard.entity.Card;
import br.com.fiap.mscard.entity.Customer;
import br.com.fiap.mscard.entity.request.CardRequest;
import br.com.fiap.mscard.entity.response.CardResponse;
import br.com.fiap.mscard.security.entity.Role;
import br.com.fiap.mscard.security.entity.User;
import br.com.fiap.mscard.security.entity.enums.RoleType;
import br.com.fiap.mscard.service.mapper.CardMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

public class CardUtils {
	public static Customer createFakeCustomer() {
		return Customer.builder()
				.cpf("00000000000")
				.name("Dummy Customer")
				.email("email@email.com")
				.phoneNumber("19 99999-8888")
				.street("Street 1")
				.city("City 1")
				.state("São Paulo")
				.zipCode("00000-000")
				.country("Brasil")
				.build();
	}

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

	public static CardRequest entityToRequest(Card card) {
		return CardRequest.builder()
				.cpf(card.getCustomerCpf())
				.numero(card.getCardNumber())
				.limite(card.getCreditLimit())
				.data_validade(card.getExpiryDate())
				.cvv(card.getCvv())
				.build();
	}

	public static CardResponse entityToResponse(Card card) {
		return CardResponse.builder()
				.cpf(card.getCustomerCpf())
				.numero(CardMapper.maskedCard(card.getCardNumber()))
				.limite(card.getCreditLimit())
				.build();
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
