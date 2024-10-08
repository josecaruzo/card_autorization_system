package br.com.fiap.msauthorization.security;

import br.com.fiap.msauthorization.repository.UserRepository;
import br.com.fiap.msauthorization.security.authentication.CustomUserAuthenticationFilter;
import br.com.fiap.msauthorization.security.authentication.JwtTokenService;
import br.com.fiap.msauthorization.security.entity.User;
import br.com.fiap.msauthorization.utils.AuthorizationUtils;
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

import java.util.NoSuchElementException;
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

	private final String default_path = "/api/pagamentos";

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		user = AuthorizationUtils.getUser();
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Test
	void mustBeValidatePublicEndpointSuccess() {

		when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");
		Assertions.assertDoesNotThrow(() -> customUserAuthenticationFilter.doFilter(request, response, filterChain));

		verify(request, times(1)).getRequestURI();
		verifyNoInteractions(userRepository);
		verifyNoInteractions(jwtTokenService);
	}

	@Test
	void mustBeValidateTokenSuccess() {
		when(request.getRequestURI()).thenReturn(default_path);
		when(request.getHeader(anyString())).thenReturn("Bearer eeyywuau.dadweqrrrrq.aususyur");
		when(jwtTokenService.getSubjectFromToken(anyString())).thenReturn(user.getUsername());
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

		Assertions.assertDoesNotThrow(() -> customUserAuthenticationFilter.doFilter(request, response, filterChain));

		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getHeader(anyString());
		verify(userRepository, times(1)).findByUsername(anyString());
		verify(jwtTokenService, times(1)).getSubjectFromToken(anyString());
	}

	@Test
	void mustBeThrowExceptionWhenTokenIsNull() {

		when(request.getRequestURI()).thenReturn(default_path);
		when(request.getHeader(anyString())).thenReturn(null);
		when(jwtTokenService.getSubjectFromToken(anyString())).thenReturn(null);

		assertThatThrownBy(() -> customUserAuthenticationFilter.doFilter(request, response, filterChain))
				.isInstanceOf(NoSuchElementException.class);

		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getHeader(anyString());
	}

}
