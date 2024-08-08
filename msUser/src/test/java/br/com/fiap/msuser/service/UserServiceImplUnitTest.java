package br.com.fiap.msuser.service;

import br.com.fiap.msuser.entity.User;
import br.com.fiap.msuser.entity.request.LoginRequest;
import br.com.fiap.msuser.entity.response.TokenResponse;
import br.com.fiap.msuser.repository.UserRepository;
import br.com.fiap.msuser.security.authentication.JwtTokenService;
import br.com.fiap.msuser.security.userdetails.UserDetailsImpl;
import br.com.fiap.msuser.service.impl.UserServiceImpl;
import br.com.fiap.msuser.utils.UserUtils;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class UserServiceImplUnitTest {
	@Mock
	JwtTokenService jwtTokenService;
	@Mock
	UserRepository userRepository;
	@Mock
	AuthenticationManager authenticationManager;
	@Mock
	BCryptPasswordEncoder passwordEncoder;
	@InjectMocks
	UserServiceImpl userService;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		userService = new UserServiceImpl(userRepository, authenticationManager, jwtTokenService);
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class SaveUser {
		@Test
		void allowSaveUser() {
			//Arrange
			User user = UserUtils.getUser();
			when(userRepository.save(any(User.class))).thenReturn(user);

			//Act
			userService.save(user);

			//Assert
			verify(userRepository, times(1)).save(any(User.class));
		}
	}

	@Nested
	class LoginUser{
		@Test
		void allowUserLogin(){
			//Arrange
			LoginRequest login = UserUtils.getLogin();

			Authentication authentication = mock(Authentication.class);
			UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

			when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
					.thenReturn(authentication);
			when(authentication.getPrincipal()).thenReturn(userDetails);
			when(jwtTokenService.generateToken(userDetails)).thenReturn("jwtToken");

			//Act
			TokenResponse tokenResponse = userService.login(login);

			//Assert
			Assertions.assertEquals("jwtToken", tokenResponse.getToken());
			verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
			verify(jwtTokenService, times(1)).generateToken(any(UserDetailsImpl.class));
			verify(authentication, times(1)).getPrincipal();
		}

		@Test
		void shouldThrowException_WhenLogin(){
			//Arrange
			LoginRequest login = UserUtils.getLogin();
			login.setSenha("wrong_password");

			//Act && Assert
			assertThatExceptionOfType(Exception.class)
					.isThrownBy(() -> userService.login(login));
			verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
		}
	}
}
