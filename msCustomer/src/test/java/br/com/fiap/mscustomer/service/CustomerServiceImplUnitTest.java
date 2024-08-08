package br.com.fiap.mscustomer.service;

import br.com.fiap.mscustomer.entity.Customer;
import br.com.fiap.mscustomer.entity.request.CustomerRequest;
import br.com.fiap.mscustomer.entity.response.CustomerResponse;
import br.com.fiap.mscustomer.repository.CustomerRepository;
import br.com.fiap.mscustomer.service.impl.CustomerServiceImpl;
import br.com.fiap.mscustomer.utils.CustomerUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class CustomerServiceImplUnitTest {

	private CustomerServiceImpl customerService;

	@Mock
	private CustomerRepository customerRepository;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		customerService = new CustomerServiceImpl(customerRepository);
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class AddCustomer{
		@Test
		void allowAddCustomer() {
			//Arrange
			Long id = 1L;
			Customer customer = CustomerUtils.createFakeCustomer(id);
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.empty());
			when(customerRepository.save(any(Customer.class))).thenReturn(customer);

			//Act
			CustomerResponse savedCustomer = customerService.add(customerRequest);


			//Assert
			assertThat(savedCustomer.getId_cliente()).isNotNull().isEqualTo(customerRequest.getCpf());
			verify(customerRepository, times(1)).findByCpf(any(String.class));
			verify(customerRepository, times(1)).save(any(Customer.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.of(customer));

			//Act && Assert
			assertThatExceptionOfType(DataIntegrityViolationException.class)
					.isThrownBy(() -> customerService.add(customerRequest));
			verify(customerRepository, times(1)).findByCpf(any(String.class));
		}
	}

}
