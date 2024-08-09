package br.com.fiap.mscustomer.controller;

import br.com.fiap.mscustomer.entity.request.CustomerRequest;
import br.com.fiap.mscustomer.entity.response.CustomerResponse;
import br.com.fiap.mscustomer.service.impl.CustomerServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cliente")
public class CustomerController {
	private final CustomerServiceImpl customerService;

	@PostMapping
	public ResponseEntity<CustomerResponse> add(@RequestBody @Valid CustomerRequest customerRequest){
		return ResponseEntity.ok(this.customerService.add(customerRequest));
	}
}
