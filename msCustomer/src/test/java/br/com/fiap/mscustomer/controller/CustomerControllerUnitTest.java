package br.com.fiap.mscustomer.controller;

import br.com.fiap.mscustomer.controller.exception.ControllerExceptionHandler;
import br.com.fiap.mscustomer.entity.Customer;
import br.com.fiap.mscustomer.entity.request.CustomerRequest;
import br.com.fiap.mscustomer.entity.response.CustomerResponse;
import br.com.fiap.mscustomer.service.impl.CustomerServiceImpl;
import br.com.fiap.mscustomer.utils.CustomerUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerControllerUnitTest {
	private MockMvc mockMvc;

	@Mock
	private CustomerServiceImpl customerService;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		CustomerController customerController = new CustomerController(customerService);
		mockMvc = MockMvcBuilders.standaloneSetup(customerController)
				.setControllerAdvice(new ControllerExceptionHandler())
				.build();
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class AddCustomer {
		@Test
		void allowAddCustomer() throws Exception {
			//Arrange
			Long id = 1L;
			Customer customer = CustomerUtils.createFakeCustomer(id);
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			CustomerResponse customerResponse = CustomerUtils.entityToResponse(customer);
			when(customerService.add(any(CustomerRequest.class))).thenReturn(customerResponse);

			//Act
			mockMvc.perform(post("/api/cliente")
							.contentType(MediaType.APPLICATION_JSON)
							.content(CustomerUtils.asJsonString(customerRequest)))
					.andExpect(status().isOk());

			//Assert
			verify(customerService, times(1)).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenAddCustomer_CustomerAlreadyExists() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			when(customerService.add(any(CustomerRequest.class))).thenThrow(DataIntegrityViolationException.class);

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isInternalServerError());
			verify(customerService, times(1)).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCustomer_cpf_blank() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			customerRequest.setCpf(null);

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCustomer_cpf_invalid() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			customerRequest.setCpf("00000000"); //Wrong pattern

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCustomer_nome_blank() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			customerRequest.setNome(null);

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCustomer_email_blank() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			customerRequest.setEmail(null);

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCustomer_email_invalid() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			customerRequest.setEmail("email.com");

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCustomer_telefone_blank() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			customerRequest.setTelefone(null);

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCustomer_telefone_invalid() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			customerRequest.setTelefone("+55 19 9 8127-6175"); //Wrong pattern format - string too long

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCustomer_rua_blank() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			customerRequest.setRua(null);

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCustomer_cidade_blank() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			customerRequest.setCidade(null);

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCustomer_estado_blank() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			customerRequest.setEstado(null);

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCustomer_cep_blank() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			customerRequest.setCep(null);

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCustomer_cep_invalid() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			customerRequest.setCep("000000"); // Wrong pattern

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).add(any(CustomerRequest.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenAddCustomer_pais_blank() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			CustomerRequest customerRequest = CustomerUtils.entityToRequest(customer);
			customerRequest.setPais(null);

			//Act && Assert
			mockMvc.perform(post("/api/cliente")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customerRequest))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).add(any(CustomerRequest.class));
		}
	}
}
