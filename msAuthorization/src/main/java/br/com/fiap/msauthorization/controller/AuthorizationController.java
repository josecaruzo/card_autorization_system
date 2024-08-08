package br.com.fiap.msauthorization.controller;

import br.com.fiap.msauthorization.entity.request.AuthorizationRequest;
import br.com.fiap.msauthorization.entity.response.AuthorizationHistoryResponse;
import br.com.fiap.msauthorization.entity.response.AuthorizationResponse;
import br.com.fiap.msauthorization.service.impl.AuthorizationServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/pagamentos")
public class AuthorizationController {
	private final AuthorizationServiceImpl authorizationService;

	@GetMapping("/cliente/{cpf}")
	public ResponseEntity<List<AuthorizationHistoryResponse>> getCustomerAuthorizations(@PathVariable String cpf){
		return ResponseEntity.ok(authorizationService.getCustomerAuthorizations(cpf));
	}

	@PostMapping
	public ResponseEntity<AuthorizationResponse> add(@RequestBody @Valid AuthorizationRequest authorizationRequest){
		return ResponseEntity.ok(authorizationService.add(authorizationRequest));
	}
}
