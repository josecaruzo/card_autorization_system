package br.com.fiap.mscard.service;

import br.com.fiap.mscard.entity.request.CardRequest;
import br.com.fiap.mscard.entity.response.CardResponse;

public interface CardService {
	CardResponse add(CardRequest cardRequest);
}
