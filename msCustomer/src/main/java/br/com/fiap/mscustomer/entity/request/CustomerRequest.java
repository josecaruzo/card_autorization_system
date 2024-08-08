package br.com.fiap.mscustomer.entity.request;

import br.com.fiap.mscustomer.entity.validation.cpf.ValidCPF;
import br.com.fiap.mscustomer.entity.validation.zipcode.ValidCEP;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidCEP //Validating the CEP request
@ValidCPF //Validating the CPF request
public class CustomerRequest {
	@NotBlank
	private String cpf;

	@NotBlank
	private String nome;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Length(max = 17)
	private String telefone;

	@NotBlank
	private String rua;

	@NotBlank
	private String cidade;

	@NotBlank
	private String estado;

	@NotBlank
	private String cep;

	@NotBlank
	private String pais;
}
