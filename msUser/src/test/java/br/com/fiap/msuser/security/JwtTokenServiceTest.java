package br.com.fiap.msuser.security;

import br.com.fiap.msuser.security.authentication.JwtTokenService;
import br.com.fiap.msuser.entity.User;
import br.com.fiap.msuser.security.userdetails.UserDetailsImpl;
import br.com.fiap.msuser.utils.UserUtils;
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

	private final User user = UserUtils.getUser();

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
	void mustGenerateToken(){
		UserDetailsImpl userDetails = new UserDetailsImpl(user);

		String token = jwtTokenService.generateToken(userDetails);
		assertThat(token).isNotNull();
	}

}
