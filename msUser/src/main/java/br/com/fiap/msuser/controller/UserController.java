package br.com.fiap.msuser.controller;

import br.com.fiap.msuser.entity.request.LoginRequest;
import br.com.fiap.msuser.entity.response.TokenResponse;
import br.com.fiap.msuser.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/autenticacao")
public class UserController {
	private final UserService userService;

	@PostMapping
	public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest loginRequest){
		return ResponseEntity.ok(this.userService.login(loginRequest));
	}

}
