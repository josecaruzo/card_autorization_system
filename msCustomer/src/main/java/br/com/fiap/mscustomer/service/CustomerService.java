package br.com.fiap.mscustomer.service;

import br.com.fiap.mscustomer.entity.request.CustomerRequest;
import br.com.fiap.mscustomer.entity.response.CustomerResponse;

public interface CustomerService {
	CustomerResponse add(CustomerRequest customerRequest);
}
