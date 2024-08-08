package br.com.fiap.msuser.service;

import br.com.fiap.msuser.entity.User;
import br.com.fiap.msuser.entity.request.LoginRequest;
import br.com.fiap.msuser.entity.response.TokenResponse;

public interface UserService {
	void save(User user);
	TokenResponse login(LoginRequest loginRequest);
}
