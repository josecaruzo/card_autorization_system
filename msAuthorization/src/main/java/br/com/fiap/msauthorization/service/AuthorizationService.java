package br.com.fiap.msauthorization.service;

import br.com.fiap.msauthorization.entity.request.AuthorizationRequest;
import br.com.fiap.msauthorization.entity.response.AuthorizationHistoryResponse;
import br.com.fiap.msauthorization.entity.response.AuthorizationResponse;

import java.util.List;

public interface AuthorizationService {
	List<AuthorizationHistoryResponse> getCustomerAuthorizations(String cpf);
	AuthorizationResponse add(AuthorizationRequest authorizationRequest);
}
