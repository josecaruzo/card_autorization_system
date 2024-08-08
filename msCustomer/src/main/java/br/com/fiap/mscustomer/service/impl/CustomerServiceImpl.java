package br.com.fiap.mscustomer.service.impl;

import br.com.fiap.mscustomer.entity.Customer;
import br.com.fiap.mscustomer.entity.request.CustomerRequest;
import br.com.fiap.mscustomer.entity.response.CustomerResponse;
import br.com.fiap.mscustomer.repository.CustomerRepository;
import br.com.fiap.mscustomer.service.CustomerService;
import br.com.fiap.mscustomer.service.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import static br.com.fiap.mscustomer.constants.CustomerConstants.*;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;

	public CustomerResponse add(CustomerRequest customerRequest){
		Customer customer = CustomerMapper.requestToEntity(customerRequest);

		if(customerRepository.findByCpf(customer.getCpf()).isPresent())
			throw new DataIntegrityViolationException(CUSTOMER_ALREADY_EXISTS);

		return CustomerMapper.entityToResponse(customerRepository.save(customer));
	}
}
