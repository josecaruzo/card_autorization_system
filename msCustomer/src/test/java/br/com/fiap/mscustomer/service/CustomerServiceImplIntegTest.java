package br.com.fiap.mscustomer.service;

import br.com.fiap.mscustomer.entity.Customer;
import br.com.fiap.mscustomer.entity.request.CustomerRequest;
import br.com.fiap.mscustomer.entity.response.CustomerResponse;
import br.com.fiap.mscustomer.service.impl.CustomerServiceImpl;
import br.com.fiap.mscustomer.utils.CustomerUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;

import static br.com.fiap.mscustomer.constants.CustomerConstants.CUSTOMER_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class CustomerServiceImplIntegTest {

	@Autowired
	private CustomerServiceImpl customerService;

	@Nested
	class AddCustomer{
		@Test
		void allowAddCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);

			//Act
			CustomerResponse savedCustomer = customerService.add(customerRequest);

			//Assert
			assertThat(savedCustomer.getId_cliente()).isNotNull().isEqualTo(customerRequest.getCpf());
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			// Act
			customerService.add(customerRequest);

			//Assert
			assertThatThrownBy(() -> customerService.add(customerRequest))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(CUSTOMER_ALREADY_EXISTS); //Trying to create same customer twice
		}
	}
}
