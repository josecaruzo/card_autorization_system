package br.com.fiap.mscustomer.service.mapper;

import br.com.fiap.mscustomer.entity.Customer;
import br.com.fiap.mscustomer.entity.request.CustomerRequest;
import br.com.fiap.mscustomer.entity.response.CustomerResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerMapper {

	public Customer requestToEntity(CustomerRequest customerRequest){
		return Customer.builder()
				.cpf(customerRequest.getCpf())
				.name(customerRequest.getNome())
				.email(customerRequest.getEmail())
				.phoneNumber(customerRequest.getTelefone())
				.street(customerRequest.getRua())
				.city(customerRequest.getCidade())
				.state(customerRequest.getEstado())
				.zipCode(customerRequest.getCep())
				.country(customerRequest.getPais())
				.build();
	}

	public CustomerResponse entityToResponse(Customer customer){
		return CustomerResponse.builder()
				.id_cliente(customer.getCpf())
				.build();
	}
}
