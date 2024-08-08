package br.com.fiap.mscard.security;

import br.com.fiap.mscard.repository.UserRepository;
import br.com.fiap.mscard.security.entity.User;
import br.com.fiap.mscard.security.userdetails.UserDetailsServiceImpl;
import br.com.fiap.mscard.utils.CardUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static br.com.fiap.mscard.constants.SecurityConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private UserDetailsServiceImpl userDetailsService;

	private User user;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		user = CardUtils.getUser();
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Test
	void mustBeReturnUserSuccess(){

		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

		Assertions.assertNotNull(userDetails);
		Assertions.assertEquals(user.getUsername(), userDetails.getUsername());
		assertThat(userDetails.getPassword()).isNotNull();
		assertThat(userDetails.isCredentialsNonExpired()).isTrue();
		assertThat(userDetails.isAccountNonLocked()).isTrue();
		assertThat(userDetails.isAccountNonExpired()).isTrue();
		assertThat(userDetails.isEnabled()).isTrue();

		verify(userRepository, times(1)).findByUsername(anyString());
	}

	@Test
	void mustBeThrownExceptionWhenUserNotFound(){

		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userDetailsService.loadUserByUsername(user.getUsername()))
				.isInstanceOf(UsernameNotFoundException.class)
				.hasMessage(String.format(USER_NOT_FOUND, user.getUsername()));

		verify(userRepository, times(1)).findByUsername(anyString());
	}

}
