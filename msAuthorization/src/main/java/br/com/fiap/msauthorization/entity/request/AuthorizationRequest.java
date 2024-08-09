package br.com.fiap.msauthorization.entity.request;

import br.com.fiap.msauthorization.entity.validation.cardnumber.ValidNumber;
import br.com.fiap.msauthorization.entity.validation.cpf.ValidCPF;
import br.com.fiap.msauthorization.entity.validation.cvv.ValidCvv;
import br.com.fiap.msauthorization.entity.validation.expirydate.ValidDateFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidCPF
@ValidNumber
@ValidDateFormat
@ValidCvv
public class AuthorizationRequest {

	@NotBlank
	private String cpf;

	@NotBlank
	private String numero;

	@NotBlank
	private String data_validade;

	@NotBlank
	private String cvv;

	@Positive
	@NotNull
	private float valor;
}
