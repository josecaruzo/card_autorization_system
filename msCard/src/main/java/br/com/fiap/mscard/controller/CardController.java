package br.com.fiap.mscard.controller;

import br.com.fiap.mscard.entity.request.CardRequest;
import br.com.fiap.mscard.entity.response.CardResponse;
import br.com.fiap.mscard.service.impl.CardServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/cartao")
public class CardController {
	private final CardServiceImpl cardService;

	@PostMapping
	public ResponseEntity<CardResponse> add(@RequestBody @Valid CardRequest cardRequest){
		return ResponseEntity.ok(cardService.add(cardRequest));
	}
}
