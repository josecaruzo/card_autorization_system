package br.com.fiap.mscard.service.impl;

import br.com.fiap.mscard.entity.Card;
import br.com.fiap.mscard.entity.Customer;
import br.com.fiap.mscard.entity.request.CardRequest;
import br.com.fiap.mscard.entity.response.CardResponse;
import br.com.fiap.mscard.repository.CardRepository;
import br.com.fiap.mscard.repository.CustomerRepository;
import br.com.fiap.mscard.service.CardService;
import br.com.fiap.mscard.service.mapper.CardMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import static br.com.fiap.mscard.constants.CardConstants.*;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
	private final CardRepository cardRepository;
	private final CustomerRepository customerRepository;

	public CardResponse add(CardRequest cardRequest){
		Card card = CardMapper.requestToEntity(cardRequest);

		if(customerRepository.findByCpf(card.getCustomerCpf()).isEmpty())
			throw new EntityNotFoundException(CUSTOMER_CPF_NOT_FOUND);

		if(cardRepository.countByCustomerCpf(card.getCustomerCpf()).equals(NUM_MAX_CARDS))
			throw new DataIntegrityViolationException(String.format(CUSTOMER_HAS_TOO_MANY_CARDS,NUM_MAX_CARDS));

		if(cardRepository.findByCardNumber(card.getCardNumber()).isPresent())
			throw new DataIntegrityViolationException(CARD_ALREADY_EXISTS);

		return CardMapper.entityToResponse(cardRepository.save(card));
	}
}
