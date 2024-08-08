package br.com.fiap.mscustomer.utils;

import br.com.fiap.mscustomer.entity.Customer;
import br.com.fiap.mscustomer.entity.request.CustomerRequest;
import br.com.fiap.mscustomer.entity.response.CustomerResponse;
import br.com.fiap.mscustomer.security.entity.Role;
import br.com.fiap.mscustomer.security.entity.User;
import br.com.fiap.mscustomer.security.entity.enums.RoleType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

public class CustomerUtils {

	public static Customer createFakeCustomer() {
		return Customer.builder()
				.cpf("00000000000")
				.name("Dummy Customer")
				.email("email@email.com")
				.phoneNumber("19 99999-8888")
				.street("Street 1")
				.city("City 1")
				.state("São Paulo")
				.zipCode("00000-000")
				.country("Brasil")
				.build();
	}

	public static Customer createFakeCustomer(Long id) {
		return Customer.builder()
				.id(id)
				.cpf("00000000000")
				.name("Dummy Customer")
				.email("email@email.com")
				.phoneNumber("19 99999-8888")
				.street("Street 1")
				.city("City 1")
				.state("São Paulo")
				.zipCode("00000-000")
				.country("Brasil")
				.build();
	}

	public static CustomerRequest entityToRequest(Customer customer){
		return CustomerRequest.builder()
				.cpf(customer.getCpf())
				.nome(customer.getName())
				.email(customer.getEmail())
				.telefone(customer.getPhoneNumber())
				.rua(customer.getStreet())
				.cidade(customer.getCity())
				.estado(customer.getState())
				.cep(customer.getZipCode())
				.pais(customer.getCountry())
				.build();
	}

	public static CustomerResponse entityToResponse(Customer customer){
		return CustomerResponse.builder()
				.id_cliente(customer.getCpf())
				.build();
	}

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

	public static String asJsonString(final Object object) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(object);
	}
}
