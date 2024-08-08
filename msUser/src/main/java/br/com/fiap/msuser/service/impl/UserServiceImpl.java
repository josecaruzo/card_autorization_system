package br.com.fiap.msuser.service.impl;

import br.com.fiap.msuser.entity.User;
import br.com.fiap.msuser.entity.request.LoginRequest;
import br.com.fiap.msuser.entity.response.TokenResponse;
import br.com.fiap.msuser.repository.UserRepository;
import br.com.fiap.msuser.security.authentication.JwtTokenService;
import br.com.fiap.msuser.security.userdetails.UserDetailsImpl;
import br.com.fiap.msuser.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenService jwtTokenService;

	public void save(User user){
		String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
		user.setPassword(encryptedPassword);

		userRepository.save(user);
	}

	public TokenResponse login(LoginRequest loginRequest) {
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
				new UsernamePasswordAuthenticationToken(loginRequest.getUsuario(), loginRequest.getSenha());

		Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		return TokenResponse.builder()
				.token(jwtTokenService.generateToken(userDetails))
				.build();
	}
}