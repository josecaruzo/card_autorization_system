package br.com.fiap.msuser.utils;

import br.com.fiap.msuser.entity.Role;
import br.com.fiap.msuser.entity.User;
import br.com.fiap.msuser.entity.enums.RoleType;
import br.com.fiap.msuser.entity.request.LoginRequest;
import br.com.fiap.msuser.entity.response.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

public class UserUtils {

	public static User getUser(){
		List<Role> defaultRole = List.of(new Role(2L, RoleType.ROLE_COMMON));
		User user = User.builder()
				.id(1L)
				.username("adj2")
				.password("adj@1234")
				.roles(defaultRole)
				.build();

		String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
		user.setPassword(encryptedPassword);

		return user;
	}

	public static LoginRequest getLogin(){
		return LoginRequest.builder()
				.usuario("adj2")
				.senha("adj@1234")
				.build();
	}

	public static TokenResponse entityToResponse(String token) {
		return TokenResponse.builder()
				.token(token)
				.build();
	}

	public static String asJsonString(final Object object) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(object);
	}

}
