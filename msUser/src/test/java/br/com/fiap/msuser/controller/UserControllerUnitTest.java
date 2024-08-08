package br.com.fiap.msuser.controller;

import br.com.fiap.msuser.controller.exception.ControllerExceptionHandler;
import br.com.fiap.msuser.entity.User;
import br.com.fiap.msuser.entity.request.LoginRequest;
import br.com.fiap.msuser.entity.response.TokenResponse;
import br.com.fiap.msuser.security.authentication.JwtTokenService;
import br.com.fiap.msuser.security.userdetails.UserDetailsImpl;
import br.com.fiap.msuser.service.UserService;
import br.com.fiap.msuser.service.impl.UserServiceImpl;
import br.com.fiap.msuser.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UserControllerUnitTest {
	@Value("${spring.security.jwt.secret}")
	private String jwtSecret;
	@Value("${spring.security.oauth2.authorizationserver.issuer}")
	private String issuer;

	private MockMvc mockMvc;
	private final User user = UserUtils.getUser();

	private JwtTokenService jwtTokenService;

	@Mock
	private UserServiceImpl userService;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		UserController customerController = new UserController(userService);
		mockMvc = MockMvcBuilders.standaloneSetup(customerController)
				.setControllerAdvice(new ControllerExceptionHandler())
				.build();
		jwtTokenService = new JwtTokenService();
		ReflectionTestUtils.setField(jwtTokenService, "jwtSecret", jwtSecret);
		ReflectionTestUtils.setField(jwtTokenService, "issuer", issuer);
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class LoginUser {
		@Test
		void allowUserLogin() throws Exception{
			//Arrange
			LoginRequest login = UserUtils.getLogin();
			UserDetailsImpl userDetails = new UserDetailsImpl(user);
			String token = jwtTokenService.generateToken(userDetails);
			TokenResponse tokenResponse = UserUtils.entityToResponse(token);

			when(userService.login(any(LoginRequest.class))).thenReturn(tokenResponse);

			//Act
			mockMvc.perform(post("/api/autenticacao")
							.contentType(MediaType.APPLICATION_JSON)
							.content(UserUtils.asJsonString(login)))
					.andExpect(status().isOk());

			//Assert
			verify(userService, times(1)).login(any(LoginRequest.class));
		}

		@Test
		void shouldThrowException_WhenLogin_wrongPassword() throws Exception {
			//Arrange
			LoginRequest login = UserUtils.getLogin();
			login.setSenha("wrong_password");

			when(userService.login(any(LoginRequest.class))).thenThrow(RuntimeException.class);

			//Act
			mockMvc.perform(post("/api/autenticacao")
							.contentType(MediaType.APPLICATION_JSON)
							.content(UserUtils.asJsonString(login))
			).andExpect(status().isUnauthorized());

			//Assert
			verify(userService, times(1)).login(any(LoginRequest.class));
		}

		@Test
		void shouldThrowException_WhenLogin_wrongUsername() throws Exception {
			//Arrange
			LoginRequest login = UserUtils.getLogin();
			login.setUsuario("wrong_username");
			when(userService.login(any(LoginRequest.class))).thenThrow(RuntimeException.class);

			//Act
			mockMvc.perform(post("/api/autenticacao")
					.contentType(MediaType.APPLICATION_JSON)
					.content(UserUtils.asJsonString(login))
			).andExpect(status().isUnauthorized());

			//Assert
			verify(userService, times(1)).login(any(LoginRequest.class));
		}
	}
}
