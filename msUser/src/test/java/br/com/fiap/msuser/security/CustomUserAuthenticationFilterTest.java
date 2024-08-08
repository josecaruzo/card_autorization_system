package br.com.fiap.msuser.security;

import br.com.fiap.msuser.repository.UserRepository;
import br.com.fiap.msuser.security.authentication.CustomUserAuthenticationFilter;
import br.com.fiap.msuser.security.authentication.JwtTokenService;
import br.com.fiap.msuser.entity.User;
import br.com.fiap.msuser.utils.UserUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


class CustomUserAuthenticationFilterTest {

	@Mock
	private JwtTokenService jwtTokenService;
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private CustomUserAuthenticationFilter customUserAuthenticationFilter;
	private User user;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private FilterChain filterChain;

	private final String default_path = "/api/autenticacao";

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		user = UserUtils.getUser();
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Test
	void mustBeValidatePublicEndpointSuccess() {

		when(request.getRequestURI()).thenReturn(default_path);
		Assertions.assertDoesNotThrow(() -> customUserAuthenticationFilter.doFilter(request, response, filterChain));

		verify(request, times(1)).getRequestURI();
		verifyNoInteractions(userRepository);
		verifyNoInteractions(jwtTokenService);
	}
}
