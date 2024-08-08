package br.com.fiap.mscard.service.mapper;

import br.com.fiap.mscard.entity.Card;
import br.com.fiap.mscard.entity.request.CardRequest;
import br.com.fiap.mscard.entity.response.CardResponse;
import lombok.experimental.UtilityClass;

import static br.com.fiap.mscard.constants.CardConstants.*;

@UtilityClass
public class CardMapper {

	public Card requestToEntity(CardRequest cardRequest){
		return Card.builder()
				.customerCpf(cardRequest.getCpf())
				.cardNumber(cardRequest.getNumero())
				.creditLimit(cardRequest.getLimite())
				.creditOtb(cardRequest.getLimite())
				.expiryDate(cardRequest.getData_validade())
				.cvv(cardRequest.getCvv())
				.build();
	}

	public CardResponse entityToResponse(Card card){
		return CardResponse.builder()
				.cpf(card.getCustomerCpf())
				.numero(maskedCard(card.getCardNumber()))
				.limite(card.getCreditLimit())
				.build();
	}

	public String maskedCard(String cardNumber){
		int maxMaskedChars = cardNumber.length() - CARD_SUFFIX - BIN_LENGTH;

		return cardNumber.substring(0, BIN_LENGTH)
				+ "*".repeat(Math.max(0, maxMaskedChars)) //Masked card number part
				+ cardNumber.substring(BIN_LENGTH + maxMaskedChars);
	}
}
