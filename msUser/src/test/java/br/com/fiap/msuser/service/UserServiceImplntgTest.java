package br.com.fiap.msuser.service;

import br.com.fiap.msuser.entity.request.LoginRequest;
import br.com.fiap.msuser.entity.response.TokenResponse;
import br.com.fiap.msuser.service.impl.UserServiceImpl;
import br.com.fiap.msuser.utils.UserUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class UserServiceImplntgTest {
	@Autowired
	private UserServiceImpl userService;

	@Nested
	class LoginUser{
		@Test
		void allowUserLogin(){
			//Arrange
			LoginRequest login = UserUtils.getLogin();

			//Act
			TokenResponse tokenResponse = userService.login(login);

			//Assert
			assertThat(tokenResponse.getToken()).isNotNull();
		}

		@Test
		void shouldThrowException_WhenLogin(){
			//Arrange
			LoginRequest login = UserUtils.getLogin();
			login.setSenha("wrong_password");

			//Act && Assert
			assertThatExceptionOfType(Exception.class)
					.isThrownBy(() -> userService.login(login));
		}
	}
}
