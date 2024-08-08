package br.com.fiap.msuser.init;

import br.com.fiap.msuser.entity.Role;
import br.com.fiap.msuser.entity.User;
import br.com.fiap.msuser.entity.enums.RoleType;
import br.com.fiap.msuser.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoadDataService {
	private final UserServiceImpl userService;

	@EventListener(ApplicationReadyEvent.class)
	public void loadData()
	{
		List<Role> defaultRole = List.of(new Role(2L, RoleType.ROLE_COMMON));
		User user = User.builder()
				.id(1L)
				.username("adj2")
				.password("adj@1234")
				.roles(defaultRole)
				.build();

		userService.save(user);
	}
}
