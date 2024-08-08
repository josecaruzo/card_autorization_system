package br.com.fiap.mscard.security;

import br.com.fiap.mscard.security.authentication.JwtTokenService;
import br.com.fiap.mscard.security.entity.User;
import br.com.fiap.mscard.security.userdetails.UserDetailsImpl;
import br.com.fiap.mscard.utils.CardUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtTokenServiceTest {
	@Value("${spring.security.jwt.secret}")
	private String jwtSecret;
	@Value("${spring.security.oauth2.authorizationserver.issuer}")
	private String issuer;

	private final User user = CardUtils.getUser();

	private JwtTokenService jwtTokenService;
	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		jwtTokenService = new JwtTokenService();
		ReflectionTestUtils.setField(jwtTokenService, "jwtSecret", jwtSecret);
		ReflectionTestUtils.setField(jwtTokenService, "issuer", issuer);
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Test
	void mustGetSubjectFromToken(){
		UserDetailsImpl userDetails = new UserDetailsImpl(user);

		String token = jwtTokenService.generateToken(userDetails);
		String tokenSubject = jwtTokenService.getSubjectFromToken(token);

		assertThat(tokenSubject).isNotNull().isEqualTo(user.getUsername());
	}

}
